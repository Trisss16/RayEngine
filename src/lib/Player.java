package lib;

import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author trili
 */
public final class Player {
    
    protected int v; //velocidad del jugador
    protected double x;
    protected double y;
    
    protected int lastMouseX;
    protected double angle; //angulo de la vista del jugador EN RADIANES
    protected double sensitibity;
    
    protected Input in;
    protected Map map;
    protected Engine e;
    
    protected Robot mouseController;
    
    //la posición de la casilla del mapa en el que está el jugador, se recalcula cada frame
    protected Position tile;
    
    public Player(int v, int x, int y) {
        this.v = v;
        this.x = x;
        this.y = y;
        
        this.sensitibity = 0.5;
        
        try {
            mouseController = new Robot();
        } catch (AWTException ex) {
            mouseController = null;
        }
        
        setAngle(-90);
    }
    
    public Point getPlayerPos() {
        return new Point((int) x, (int) y);
    }
    
    
    //para guardar una referencia de la clase Input de engine y acceder a los eventos de mouse y teclado del frame de engine
    public void addInput(Input i) {
        this.in = i;
        this.lastMouseX = in.getMouseX();
        mouseController.mouseMove(Engine.WIN_WIDTH / 2, Engine.WIN_HEIGHT / 2); //mueve el mouse al centro
    }
    
    public void addMap(Map map) {
        this.map = map;
    }
    
    public void addEngine(Engine e) {
        this.e = e;
    }
    
    
    //regresa el angulo en grados
    public double getAngle() {
        return Math.toDegrees(angle);
    }
    
    
   //angulo real en radianes
    public double getRadAngle() {
        return angle;
    }
    
    //recibe el angulo y lo guarda convertido a radianes
    public void setAngle(double newAngle) {
        angle = Math.toRadians(newAngle);
        normalizeAngle();
    }
    
    public void addAngle(double newAngle) {
        angle += Math.toRadians(newAngle);
        normalizeAngle();
    }
    
    public void normalizeAngle() {
        angle = angle % (2 * Math.PI);
        if (angle < 0) angle += 2 * Math.PI;
    }
    
    
    public void update(double dt) {
        //si aun no se agrea la clase de input o el map solo no actualiza
        if (in == null || map == null) return;
        
        updateAngle();
        updateMovement(dt); //actualiza el movimiento del personaje
        System.out.println(angle);
    }
    
    
    protected void updateMovement(double dt) {
        double moveX = 0;
        double moveY = 0;

        //normaliza la velocidad con el dt
        double speed = v * dt;

        /*descompone el movimiento diagonal descrito por el angulo (angle) en sus componentes en x y en y, usando seno y coseno. De esta forma
        se obtiene el aumento para cada eje para llegar a la posición deseada individualmente. Ya que seno y coseno solo proporcionan valores
        entre -1 y 1, para obtener el incremento total los componentes se multiplican por la velocidad normalizada*/
        
        //adelante y atrás
        if (in.isKeyDown(KeyEvent.VK_W)) {
            moveX += Math.cos(angle) * speed;
            moveY += Math.sin(angle) * speed;
        }
        if (in.isKeyDown(KeyEvent.VK_S)) {
            moveX -= Math.cos(angle) * speed;
            moveY -= Math.sin(angle) * speed;
        }

        //izquierda y derecha
        if (in.isKeyDown(KeyEvent.VK_A)) {
            moveX += Math.sin(angle) * speed;
            moveY -= Math.cos(angle) * speed;
        }
        if (in.isKeyDown(KeyEvent.VK_D)) {
            moveX -= Math.sin(angle) * speed;
            moveY += Math.cos(angle) * speed;
        }

        double newX = x + moveX;
        double newY = y + moveY;

        //commprueba si al modificar x colisiona
        int tileX = (int)(newX / Engine.TILE_SIZE);
        int tileY = (int)(y / Engine.TILE_SIZE);
        if (map.map[tileY][tileX] == 0) {
            x = newX;
        }

        //comprueba si al modificar y colisiona
        tileX = (int)(x / Engine.TILE_SIZE);
        tileY = (int)(newY / Engine.TILE_SIZE);
        if (map.map[tileY][tileX] == 0) {
            y = newY;
        }

        tile = Map.getTile(x, y);
        //System.out.println(tile);
    }
    
    
    protected void updateAngle() {
        Point canvas = e.getCanvasPos(); //posicion del canvas en la pantalla
        Dimension dim = e.getCanvasDimension(); //tamaño del canvas
        
        //calcula donde está el centro del canvas
        int centerX = canvas.x + (int) dim.getWidth() / 2;
        int centerY = canvas.y + (int) dim.getHeight() / 2;
        
        //calcula cuanto avanzó el mouse desde el frame anterior y lo transforma a un angulo que suma al angulo actual
        double offset = in.getMouseX() - (int) dim.getWidth() / 2;
        offset *= sensitibity;
        addAngle(offset);
        
        //regresa el mouse al centro
        mouseController.mouseMove(centerX, centerY);
    }
    
    
    /*METODOS DE DIBUJO*/
    
    protected void drawPlayer(Graphics2D g) {
        int pWidth = 10;
        
        //dibujar el personaje de prueba
        g.setColor(Color.red);
        Point pPos = getPlayerPos();
        g.fillRect(pPos.x - pWidth/2, pPos.y - pWidth/2, pWidth, pWidth); //dibuja el personaje centrado
        drawLine(g);
    }
    
    //dibuja la linea que indica hacia donde apunta el jugador
    protected void drawLine(Graphics2D g) {
        int x1 = (int) x;
        int x2 = (int) (x + Math.cos(angle) * 20);
        
        int y1 = (int) y;
        int y2 = (int) (y + Math.sin(angle) * 20);
        
        //dibujar la linea
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(2));
        g.drawLine(x1, y1, x2, y2);
        g.setStroke(old);
    }
}
