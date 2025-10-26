package lib;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class Engine extends JFrame{
    
    public static final int TILE_SIZE = 64; //cada celda medirá 64x64 unidades, y sus sprites tendrán esa cantidad de pixeles
    
    //COMPONENTES
    private Player p;
    private final Map map;
    private final Canvas c;
    public final Input in;
    
    private RayCaster raycaster;
    
    //para ver loq ue sucede en la vista 2d
    private final Canvas view2d;
    private final JFrame frame2d;
    
    
    //ATRIBUTOS
    private boolean running;
    
    public static  int WIN_WIDTH = 800;
    public static int WIN_HEIGHT = 600;
    
    private double deltaTime;
    private int targetFPS;
    private boolean paused;
    
    public static void setWindowSize(Dimension d) {
        WIN_WIDTH = d.width;
        WIN_HEIGHT = d.height;
    }
    
    
    public Engine(Player player, Map map) {
        
        //canvas de renderizado
        this.c = new Canvas();
        c.setPreferredSize(new Dimension(WIN_WIDTH, WIN_HEIGHT));
        
        //listeners de eventos
        this.in = new Input();
        in.addEngine(this);
        c.addKeyListener(in);
        c.addMouseListener(in);
        c.addMouseMotionListener(in);
        
        //canvas para la vista 2d
        this.view2d = new Canvas();
        view2d.setPreferredSize(new Dimension(map.n * TILE_SIZE, map.m * TILE_SIZE));
        frame2d = new JFrame();
        frame2d.add(view2d);
        frame2d.pack();
        frame2d.setResizable(false);
        //frame2d.setFocusableWindowState(false); //lo hace no focuseable, solo para visualizar el mapa
        
        //guarda el player y le agrega el input
        this.p = player;
        p.addInput(in);
        p.addMap(map);
        p.addEngine(this);
        
        //mapa
        this.map = map;
        
        //raycaster
        this.raycaster = new RayCaster(p, map);
        
        //parámetros
        deltaTime = 0;
        targetFPS = -1;
        running = false;
        paused = false;
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setFocusable(true);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.add(c);
        this.pack();
    }
    
    //pausar o resumir el juego
    public void togglePause() {
        paused = !paused;
        System.out.println("Pausado: " + paused);
    }
    
    
    //RAYCASTER
    
    public void setFOV(int FOV) {
        raycaster.setFOV(FOV);
    }
    
    public void setRaysToCast(int rays) {
        raycaster.setRaysToCast(rays);
    }
    
    
    //CANVAS
    
    public Point getCanvasPos() {
        return c.getLocationOnScreen();
    }
    
    public Dimension getCanvasDimension() {
        return new Dimension(c.getWidth(), c.getHeight());
    }
    
    
    public void start() {
        running = true;
        this.setVisible(true);
        frame2d.setVisible(true);
        
        /*para el renderizado utiliza un canvas en vez de hacer override de algun componente de jswing, pues usando canvas controlas exactamente cuando
        quieres que dibuje y limpie (al inicio de cada frame). Usa un bufferStrategy de 2, es decir 2 buffers. Mientras un frame se muestra, el otro frame se renderiza*/
        c.createBufferStrategy(2);
        BufferStrategy bs = c.getBufferStrategy();
        
        view2d.createBufferStrategy(2);
        BufferStrategy bs2d = view2d.getBufferStrategy();
        
        while(running) {
            long start = System.nanoTime();
            
            if (!paused) update(deltaTime);
            render(bs);
            render2d(bs2d);
            
            long finish = System.nanoTime();
            
            //recalcular el dt
            deltaTime = (finish - start) / 1_000_000_000.0;
            deltaTime += delay(); //detiene el hilo para mantener los fps esperadoa (targetFPS) y suma el tiempo que se detuvo al dt
        }
    }
    
    
    public void stop() {
        running = false;
    }
    
    
    /*regresa el delay necesario para llegar a los targetFPS. Por ejemplo, para 60 fps cada frame dura 0.016s, pero
    si el frame dura menos que eso se calcula el tiempo que se necesita dormir el hilo para que dure lo correcto*/
    private double delay() {
        if (targetFPS <= 0) return 0;
        
        long start = System.nanoTime();
        
        double dtTarget = 1.0 / targetFPS;
        double timeLeft = dtTarget - deltaTime;
        
        if (timeLeft <= 0) return 0; //si es negativo ya paso más tiempo del esperado para llegar a los targetFPS
        
        try {
            Thread.sleep((long)(timeLeft * 1000)); 
        } catch (InterruptedException e) {
            //no hace nada pero sleep pide un try catch por si es interrumpido
        }
        
        long finish = System.nanoTime();
        return (finish - start) / 1_000_000_000.0;
    }
    
    
    public void setTargetFPS(int fps) {
        targetFPS = fps;
    }
    
    
    private void update(double dt) {
        //evita que las teclas y botones que estaban activos cuando se perdio el foco se queden activadas
        if (!this.isFocused())in.allFalse();
        
        p.update(dt);
        raycaster.update(dt);
    }
    
    
    private void render(BufferStrategy bs) {
        Graphics2D g = (Graphics2D) bs.getDrawGraphics(); //obtiene el objeto para dibujar al canvas
        
        /*desactiva el antialiasing, que es un efecto de suavisado en las imagenes pixeleadas. Esto sirve para que
        si se utilizan texturas de píxel art, o se renderiza con pocos rayos, la imagen no se vea borrosa*/
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        //limpia el frame anterior
        g.clearRect(0, 0, c.getWidth(), c.getHeight());
        
        
        
        //todo lo que se quiere renderizar
            raycaster.renderSimulation3D(g);
        
        
        
        //muestra el frame dibujar
        g.dispose();
        bs.show();
    }
    
    //renderiza la vista en 2d
    private void render2d(BufferStrategy bs) {
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        
        //limpia el frame anterior
        g.clearRect(0, 0, view2d.getWidth(), view2d.getHeight());
        
        
        
        //RENDERIZADO
            g.setColor(Color.GRAY);
            g.fillRect(0, 0, view2d.getWidth(), view2d.getHeight());
            
            map.renderMap(g);
            raycaster.renderView2D(g);
            p.drawPlayer(g);
            
            //información
            drawTextBox(g, String.format("dt: %.6f", deltaTime), 10, 10);
            if (paused) drawTextBox(g, "PAUSADO", WIN_WIDTH / 2, 10);
        
        
        
        //muestra el frame dibujar
        g.dispose();
        bs.show();
    }
    
    
    //dibuja un texto con un fondo negro
    private void drawTextBox(Graphics2D g, String str, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        
        int fWidth = fm.stringWidth(str);
        int fHeight = fm.getHeight();
        
        int offset = fHeight / 2;
        int textX = x + offset;
        int textY = y + offset + fm.getAscent();
        
        g.setColor(Color.black);
        g.fillRect(x, y, fWidth + fHeight, fHeight * 2);
        g.setColor(Color.white);
        g.drawString(str, textX, textY);
    }
    
    
    
    //UTILIDADES
    
    public static void setWindowSize(int width, int height) {
        WIN_WIDTH = width;
        WIN_HEIGHT = height;
    }
    
    //normaliza un angulo, manteniendolo dentro del rango de 0 a 2pi
    public static double normalizeAngleRad(double a) {
        a %= 2 * Math.PI;
        if (a < 0) a += 2 * Math.PI;
        return a;
    }
    
    public static double normalizeAngleDeg(double a) {
        a %= 360;
        if (a < 0) a += 360;
        return a;
    }
 
}
