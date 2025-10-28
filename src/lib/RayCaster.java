package lib;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class RayCaster {
    
    private final Map map;
    private final Player p;
    
    private DPoint playerPos;
    private double angle; //angulo del jugador en radianes
    
    private int FOV; //field of view, que tantos grados puede ver el jugador
    
    private int raysToCast;
    private Ray[] rays;
    
    Sprite spr = new Sprite("/res/greystone.png");
    
    
    //aspect ratio o relacion de aspecto, que indica la proporción que el renderizado mantendrá
    private Dimension aspectRatio;
    
    public RayCaster(Player p, Map map) {
        this.p = p;
        this.map = map;
        
        this.FOV = 60; //inicializa en 60 (el fov comun en la mayoria de juegos)
        this.raysToCast = 200;
        
        //relacion de aspecto de 4:3, que es la que se solia usar en juegos retro
        aspectRatio = new Dimension(4, 3);
        
        updatePlayerInfo();
        rays = new Ray[raysToCast];
    }
    
    //EN GRADOS
    public void setFOV(int FOV) {
        //mantiene el fov como numero par, para mantener todo centrado al jugador siempre
        if (FOV % 2 == 1) FOV++;
        
        /*caso especial, si el angulo es 360 lo mantiene sin normalizar. No afecta en nada realmente porque normalmente no se va a usar
        un fov de 360, pero un angulo de 360 es igual a 0, por lo que si intentas castear rayos que cobran todos los angulos posibles 
        estableciendo el fov a 360, en realidad quedarias con un aumento de 0, y todos los rayos tendrian exactamente el mismo angulo*/
        if (FOV == 360) {
            this.FOV = FOV;
            return;
        }
        
        FOV = (int) Engine.normalizeAngleDeg(FOV);
        this.FOV = FOV;
    }
    
    public void setRaysToCast(int raysToCast) {
        if (raysToCast % 2 == 1) raysToCast++; //igual con los rayos
        this.raysToCast = raysToCast;
        rays = new Ray[raysToCast];
    }
    
    public void setAspectRatio(int w, int h) {
        aspectRatio = new Dimension(w, h);
    }
    
    
    private void updatePlayerInfo() {
        playerPos = p.getPlayerPos();
        angle = p.getRadAngle();
    }
    
    
    public void update(double dt) {
        updatePlayerInfo();
        castRays();
    }
    
    
    private void castRays() {
        //incremento del angulo cada que se castea un nuevo rayo
        double angleIncrement = Math.toRadians(FOV / (raysToCast * 1.0));
        double rayAngle = angle - angleIncrement * (raysToCast / 2);
        
        for (int i = 0; i < raysToCast; i++) {
            rays[i] = new Ray(Engine.normalizeAngleRad(rayAngle), playerPos, map);
            rayAngle += angleIncrement;
        }
    }

    
    //METODOS PARA RENDERIZADO
    
    public void renderSimulation3D(Graphics2D g) {
        /*cada rayo va a dibujar una sola columna de pixeles de la vista en 3D, por lo que el ancho en pixeles será el mismo
        que el número de rayos trazados. Para calcular el alto de la simulación se necesita aplicar una formula considerando
        el aspect ratio, pues se conoce tanto el ancho y alto del aspect ratio como el ancho de la simulacion*/
        int simWidth = raysToCast;
        int simHeight = (simWidth * aspectRatio.height) / aspectRatio.width;
        
        Color shadow = new Color(0, 0, 0, 128); //dibujar sombras
        
        //escalas para acomodar al tamaño de la ventana
        double widthScale = 1.0 * Engine.WIN_WIDTH / simWidth;
        double heightScale = 1.0 * Engine.WIN_HEIGHT / simHeight;
        AffineTransform old = g.getTransform();
        g.scale(widthScale, heightScale);
        
        //fondo
        g.setColor(Color.black);
        g.fillRect(0, 0, simWidth, simHeight);
        
        for (int i = 0; i < raysToCast; i++) {
            
            double rayLength = rays[i].length;
            
            /*Se da un efecto de ojo de pez porque las columnas se hacen más pequeñas entra más largo sea el rayo y
            los rayos más cercanos a las orillas son más largos. por eso se obtiene la diferencia de angulos del jugador
            y del rayo, multiplicando la longitud por el coseno de este angulo se descompone en su componente horizontal
            lo que arregla el efecto*/
            double da = angle - rays[i].angle;
            da = Engine.normalizeAngleRad(da);
            rayLength *= Math.cos(da);
            
            //calcula el alto de cada columna columna de un rayo, obteniendo la inversa de su longitud y multiplicandola por el alto de la simulacion
            int rayHeight = (int) Math.round(Engine.TILE_SIZE / rayLength * simHeight);
            //rayHeight = Math.min(rayHeight, simHeight); //esta linea causa que las texturas se deformen entre más te acercas
            
            //para mantener la columna centrada
            int offset = (simHeight - rayHeight) / 2;
            
            g.setColor(Color.MAGENTA);
            
            int column;
            
            if (rays[i].isVertical) {
                column = (int) (rays[i].hit.y % Engine.TILE_SIZE);
            } else {
                column = (int) (rays[i].hit.x % Engine.TILE_SIZE);
            }
            
            g.setColor(Color.MAGENTA);
            //g.fillRect(i, offset, 1, rayHeight);
            spr.drawColumn(g, column, i, offset, 1, rayHeight);
            
            //dibujar una sombra
            if (rays[i].isHorizontal) {
                g.setColor(shadow);
                g.fillRect(i, offset, 1, rayHeight);
            }
        }
        
        g.setTransform(old);
    }
    
    
    public void renderView2D(Graphics2D g) {
        for (Ray i: rays) {
            i.drawRay(g);
        }
    }
    
    
}




final class Ray {
    
    public final double angle;
    private final double px, py; //posicion del personaje en x y y
    private final int m, n; //orden de la matriz del mapa
    
    
    //true si hay intersección antes de salir del mapa en cualquiera de los dos casos
    private boolean foundHorizontalIntersection = false;
    private boolean foundVerticalIntersection = false;
    
    private DPoint intersection;
    
    //datos del rayo
    public final double length;
    public final boolean isVertical;
    public final boolean isHorizontal;
    
    public final DPoint hit;
    
    
    //recibe el angulo en radianes
    public Ray(double angle, DPoint pos, Map map) {
        this.angle = angle;
        px = pos.x;
        py = pos.y;

        m = map.m;
        n = map.n;
        
        length = cast(map);
        isVertical = foundVerticalIntersection;
        isHorizontal = foundHorizontalIntersection;
        hit = intersection;
    }
    
    //aplica la formula de la distancia entre dos puntos (que también podria considerarse la de la magnitud de un vector, pues un rayo es un vector)
    private double distance(DPoint p1, DPoint p2) {
        double x1 = p1.x;
        double x2 = p2.x;
        double y1 = p1.y;
        double y2 = p2.y;
        
        double dx = x2-x1;
        double dy = y2-y1;
        
        return Math.sqrt(dx*dx + dy*dy);
    }
    
    private double cast(Map map) {
        DPoint pos = new DPoint(px, py);
        
        //obtiene los puntos de intersección vertical y horizontal
        DPoint horizontalHit = horizontalHit(map);
        DPoint verticalHit = verticalHit(map);
        
        double hLength;
        double vLength;
        
        //si se dio una interseccion horizontal calcula el valor del rayo horizontal, si no le da un valor muy alto
        hLength = foundHorizontalIntersection && horizontalHit != null ? distance(pos, horizontalHit) : Double.POSITIVE_INFINITY;
        
        //igual con la intersección vertical
        vLength = foundVerticalIntersection && verticalHit != null ? distance(pos, verticalHit) : Double.POSITIVE_INFINITY;
        
        //primero verifica si si encontró el punto, si no regresa el valor más alto posible
        /*ya que está regresando infinito, al hacer el calculo de la longitud de la columna de ese rayo siempre dará 0,
        pues se estaria dividiendo entre infinito. Asi nunca se dibujaria la pared y no daria ningun conflicto*/
        if (hLength == Double.POSITIVE_INFINITY && vLength == Double.POSITIVE_INFINITY) {
            intersection = null;
            return Double.POSITIVE_INFINITY;
        }
        
        //asigna la longitud más corta de entr los dos, además de guardar la posición
        if (hLength < vLength) {
            intersection = horizontalHit;
            foundVerticalIntersection = false;
            foundHorizontalIntersection = true;
            return hLength;
        } else {
            intersection = verticalHit;
            foundHorizontalIntersection = false;
            foundVerticalIntersection = true;
            return vLength;
        }
    }
    
    
    //ENCUENTRA TODAS LAS INTERSECCIONES HORIZONTALES
    private DPoint horizontalHit(Map map) {
        boolean facingDown = angle > 0 && angle < Math.PI;
        boolean facingUp = angle > Math.PI && angle < 2 * Math.PI;
        
        boolean foundWall = false;
        
        //se calcula la primera intersección pues el personaje no está alineado a las casillas
        double firstX;
        double firstY;
        
        //calcula la primera intersección en y, redondeando al valor del tamaño de la casilla
        /*ya que se buscan las intersecciones horizontales, la posicion en y de cada intersección siempre se encontrará
        alineada a las orillas de las casillas, por lo que para encontrar la primer intersección solo es necesario tomar
        la posición en y del jugador y dependiendo de si está mirando arriba o abajo, llevarla a la siguiente casilla
        hacia arriba o hacia abajo*/
        if (facingUp) {
            firstY = Math.floor(py / Engine.TILE_SIZE) * Engine.TILE_SIZE - 0.0001;
        } else if(facingDown) {
            firstY = Math.floor(py / Engine.TILE_SIZE) * Engine.TILE_SIZE + Engine.TILE_SIZE;
        } else { //cuando está mirando directamente a la izquierda o a la derecha no podrá encontrar jamás una intersección horizontal
            return null;
        }
        
        //ahora aplicar la formula para encontrar la primer intersección en x
        firstX = (firstY - py) / Math.tan(angle) + px;
        
        //ahora calcula nuevos valores para x y y para cada interseccion horizontal, iniciando desde la primer intersección
        double nextX = firstX;
        double nextY = firstY;
        
        //incrementos para encontrar cada nueva intersección
        double incrementX = 0;
        double incrementY = 0;
        
        //los incrementos en y serán el tilesize, para mantenerlo siempre alineado a las casillas del mapa
        if (facingUp) incrementY = - Engine.TILE_SIZE;
        else if (facingDown) incrementY = Engine.TILE_SIZE;
        
        incrementX = incrementY / Math.tan(angle);
        
        //empieza el loop de revisión, solo para de revisar cuando sale del mapa
        while(nextX >= 0 && nextX <= n * Engine.TILE_SIZE && nextY >= 0 && nextY <= m * Engine.TILE_SIZE ) {
            
            //si encuentra una pared en un incremento lo marca y deja de buscar
            if (map.insideOfWall(nextX, nextY)) {
                foundWall = true;
                break;
            } else { //si no la encuentra sigue aumentando
                nextX += incrementX;
                nextY += incrementY;
            }
            
        }
        
        //si encontró la pared regresa la posición en donde la encontró
        if (foundWall) {
            foundHorizontalIntersection = true;
            return new DPoint(nextX, nextY);
        }
        
        //si no la encontró regresa null
        return null;
    }
    
    
    
    //ENCUENTRA TODAS LAS INTERSECCIONES VERTICALES
    private DPoint verticalHit(Map map) {     
        boolean facingLeft = angle > Math.PI / 2 && angle < 3 * Math.PI / 2;
        boolean facingRight = angle > 3 * Math.PI / 2 || angle < Math.PI / 2;
        
        boolean foundWall = false;
        
        //se calcula la primera intersección pues el personaje no está alineado a las casillas
        double firstX;
        double firstY;
        
        //calcula la primera intersección en x, redondeando al valor del tamaño de la casilla
        /*de la misma forma que en las intersecciones horizontales, la posicion en x de la intersección se alinea a las
        casillas, por lo que lleva la posicion en x a un valor que se alinee con las casillas*/
        if (facingRight) {
            firstX = Math.floor(px / Engine.TILE_SIZE) * Engine.TILE_SIZE + Engine.TILE_SIZE;
        } else if(facingLeft) {
            firstX = Math.floor(px / Engine.TILE_SIZE) * Engine.TILE_SIZE - 0.0001;
        } else { //cuando está mirando directamente hacia arriba o abajo es imposible encontrar una interseccion vertical
            return null;
        }
        
        //ahora aplicar la formula para encontrar la primer intersección en y
        firstY = py + (firstX - px) * Math.tan(angle);
        
        //ahora calcula nuevos valores para x y y para cada interseccion vertical, iniciando desde la primer intersección
        double nextX = firstX;
        double nextY = firstY;
        
        //incrementos para encontrar cada nueva intersección
        double incrementX = 0;
        double incrementY = 0;
        
        if (facingRight) incrementX = Engine.TILE_SIZE;
        else if (facingLeft) incrementX = - Engine.TILE_SIZE;
        
        incrementY = incrementX * Math.tan(angle);
        
        //empieza el loop de revisión, solo para de revisar cuando sale del mapa
        while(nextX >= 0 && nextX <= n * Engine.TILE_SIZE && nextY >= 0 && nextY <= m * Engine.TILE_SIZE ) {
            
            //si encuentra una pared en un incremento lo marca y deja de buscar
            if (map.insideOfWall(nextX, nextY)) {
                foundWall = true;
                break;
            } else { //si no la encuentra sigue aumentando
                nextX += incrementX;
                nextY += incrementY;
            }
            
        }
        
        //si encontró la pared regresa la posición en donde la encontró
        if (foundWall) {
            foundVerticalIntersection = true;
            return new DPoint(nextX, nextY);
        }
        
        //si no la encontró regresa null
        return null;
    }
    
    
 
    public void drawRay(Graphics2D g) {
        g.setColor(Color.green);
        
        if (intersection != null) {
            g.drawLine((int) px, (int) py, (int) intersection.x, (int) intersection.y);
        }
    }
}