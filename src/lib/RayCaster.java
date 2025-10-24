package lib;

import java.awt.*;

public class RayCaster {
    
    private final Map map;
    private final Player p;
    
    private Point playerPos;
    private double angle; //angulo del jugador en radianes
    
    private int FOV; //field of view, que tantos grados puede ver el jugador
    
    private int raysToCast;
    private Ray[] rays;
    
    public RayCaster(Player p, Map map) {
        this.p = p;
        this.map = map;
        
        this.raysToCast = 200;
        this.FOV = 60; //inicializa en 60 (el fov comun en la mayoria de juegos)
        
        updatePlayerInfo();
        rays = new Ray[raysToCast];
    }
    
    //EN GRADOS
    public void setFOV(int FOV) {
        this.FOV = FOV;
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
        double angleIncrement = Math.toRadians(FOV / (raysToCast * 1.0)); //incremento del angulo para cad
        double rayAngle = angle - angleIncrement * (raysToCast / 2);
        
        for (int i = 0; i < raysToCast; i++) {
            rays[i] = new Ray(Engine.normalizeAngleRad(rayAngle), playerPos, map);
            rayAngle += angleIncrement;
        }
    }
    
    
    //METODOS DE RENDERIZADO
    
    public void renderSimulation3D(Graphics2D g) {
    }
    
    public void renderView2D(Graphics2D g) {
        for (Ray i: rays) {
            i.drawRay(g);
        }
    }
}




final class Ray {
    
    private final double angle;
    private final double px, py; //posicion del personaje en x y y
    private final int m, n; //orden de la matriz del mapa
    
    
    //true si hay intersección antes de salir del mapa en cualquiera de los dos casos
    private boolean foundHorizontalIntersection = false;
    private boolean foundVerticalIntersection = false;
    
    private Point intersection;
    
    public final double length;
    
    
    //recibe el angulo en radianes
    public Ray(double angle, Point pos, Map map) {
        this.angle = angle;
        px = pos.x;
        py = pos.y;

        m = map.m;
        n = map.n;
        
        length = cast(map);
    }
    
    //aplica la formula de la distancia entre dos puntos (que también podria considerarse la de la magnitud de un vector, pues un rayo es un vector)
    private double distance(Point p1, Point p2) {
        double x1 = p1.x;
        double x2 = p2.x;
        double y1 = p1.y;
        double y2 = p2.y;
        
        double dx = x2-x1;
        double dy = y2-y1;
        
        return Math.sqrt(dx*dx + dy*dy);
    }
    
    private double cast(Map map) {
        Point pos = new Point((int) px, (int) py);
        
        //obtiene los puntos de intersección vertical y horizontal
        Point horizontal = horizontalHit(map);
        Point vertical = verticalHit(map);
        
        double hLength;
        double vLength;
        
        //si se dio una interseccion horizontal calcula el valor del rayo horizontal, si no le da un valor aslto
        if (foundHorizontalIntersection) {
            hLength = distance(pos, horizontal);
        } else {
            hLength = Integer.MAX_VALUE;
        }
        
        //igual con la intersección vertical
        if (foundVerticalIntersection) {
            vLength = distance(pos, vertical);
        } else {
            vLength = Integer.MAX_VALUE;
        }
        
        //asigna la longitud más corta de entr los dos, además de guardar la posición
        if (hLength < vLength) {
            intersection = horizontal;
            return hLength;
        } else {
            intersection = vertical;
            return vLength;
        }
    }
    
    
    //ENCUENTRA TODAS LAS INTERSECCIONES HORIZONTALES
    private Point horizontalHit(Map map) {
        //para casos donde el angulo apunte directamente a la derecha o izquierda se valida con cierta tolerancia
        double tolerance = 0.001;
        
        boolean facingDown = angle > tolerance && angle < (Math.PI - tolerance);
        boolean facingUp = angle > (Math.PI + tolerance) && angle < (2 * Math.PI - tolerance);
        
        boolean foundWall = false;
        
        //se calcula la primera intersección pues el personaje no está alineado a las casillas
        double firstX;
        double firstY;
        
        //calcula la primera intersección en y, redondeando al valor del tamaño de la casilla
        if (facingUp) {
            firstY = Math.floor(py / Engine.TILE_SIZE) * Engine.TILE_SIZE - 1;
        } else if(facingDown) {
            firstY = Math.floor(py / Engine.TILE_SIZE) * Engine.TILE_SIZE + Engine.TILE_SIZE;
        } else { //cuando está mirando directamente a la izquierda o a la derecha no podrá encontrar jamás una intersección horizontal
            return new Point((int) px, (int) py);
        }
        
        //ahora aplicar la formula para encontrar la primer intersección en x
        firstX = (firstY - py) / Math.tan(angle) + px;
        
        //ahora calcula nuevos valores para x y y para cada interseccion horizontal, iniciando desde la primer intersección
        double nextX = firstX;
        double nextY = firstY;
        
        //incrementos para encontrar cada nueva intersección
        double incrementX = 0;
        double incrementY = 0;
        
        if (facingUp) incrementY = - Engine.TILE_SIZE;
        else if (facingDown) incrementY = Engine.TILE_SIZE;
        
        incrementX = incrementY / Math.tan(angle);
        
        //empieza el loop de revisión, solo para de revisar cuando sale del mapa
        while(nextX > 0 && nextX < n * Engine.TILE_SIZE && nextY > 0 && nextY < m * Engine.TILE_SIZE ) {
            
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
            return new Point((int) nextX, (int) nextY);
        }
        
        //si no la encontró regresa la posición del jugador
        return new Point((int) px, (int) py);
    }
    
    
    
    //ENCUENTRA TODAS LAS INTERSECCIONES VERTICALES
    private Point verticalHit(Map map) {
        //para casos donde el angulo apunte directamente a la derecha o izquierda se valida con cierta tolerancia
        double tolerance = 0.001;
        
        boolean facingLeft = angle > (Math.PI / 2 + tolerance) && angle < (3 * Math.PI / 2 - tolerance);
        boolean facingRight = (angle > 3 * Math.PI / 2 + tolerance) || (angle < Math.PI / 2 - tolerance);
        
        boolean foundWall = false;
        
        //se calcula la primera intersección pues el personaje no está alineado a las casillas
        double firstX;
        double firstY;
        
        //calcula la primera intersección en x, redondeando al valor del tamaño de la casilla
        if (facingRight) {
            firstX = Math.floor(px / Engine.TILE_SIZE) * Engine.TILE_SIZE + Engine.TILE_SIZE;
        } else if(facingLeft) {
            firstX = Math.floor(px / Engine.TILE_SIZE) * Engine.TILE_SIZE - 1;
        } else { //cuando está mirando directamente hacia arriba o abajo es imposible encontrar una interseccion vertical
            return new Point((int) px, (int) py);
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
        while(nextX > 0 && nextX < n * Engine.TILE_SIZE && nextY > 0 && nextY < m * Engine.TILE_SIZE ) {
            
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
            return new Point((int) nextX, (int) nextY);
        }
        
        //si no la encontró regresa la posición del jugador
        return new Point((int) px, (int) py);
    }
    
    
    
    public void drawRay(Graphics2D g) {
        g.setColor(Color.green);
        g.drawLine((int) px, (int) py, intersection.x, intersection.y);
    }
}