package lib;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;

public class Sprite {
    
    private final BufferedImage sprite;
    private final BufferedImage[] columns;
    
    public Sprite(String path) {
        sprite = getImage(path);
        columns = new BufferedImage[Engine.TILE_SIZE];
        getColumns();
    }
    
    public static BufferedImage reescale(BufferedImage src, int w, int h) {
        BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
        Graphics2D g = scaled.createGraphics();
        //g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return scaled;
    }
    
    //dibuja una sola columna del sprite completo
    public final void drawColumn(Graphics2D g, int column, int x, int y, int w, int h) {
        if (column >= 0 && column < columns.length) {
            g.drawImage(columns[column], x, y, w, h, null);
        } else { //si recibe un número fuera del array dibuja un cuadrado verde
            g.setColor(Color.GREEN);
            g.fillRect(x, y, w, h);
        }
    }
    
    //dibuja toda la imagen
    public final void drawSprite(Graphics2D g, int x, int y, int w, int h) {
        g.drawImage(sprite, x, y, w, h, null);
    }
    
    
    private BufferedImage getImage(String path) {
        BufferedImage im;
        
        try {
            
            InputStream is = getClass().getResourceAsStream(path);
            
            if (is == null) {
                //System.out.println("input stream es null.");
                return getEmptyImage();
            }
            
            im = ImageIO.read(is);
            im = Sprite.reescale(im, Engine.TILE_SIZE, Engine.TILE_SIZE);
            //System.out.println("Imagen extraida.");
            
        } catch (IOException e) {
            
            im = getEmptyImage();
            //System.out.println("No se pudo leer la imagen: " + e);
            
        }
        
        return im;
    }
    
    private BufferedImage getEmptyImage() {
        BufferedImage im = new BufferedImage(64, 64, BufferedImage.TRANSLUCENT);
        Graphics2D g = im.createGraphics();
        g.setColor(Color.MAGENTA); //dibuja los muros 
        g.fillRect(0, 0, Engine.TILE_SIZE, Engine.TILE_SIZE);
        g.dispose();
        return im;
    }
    
    
    private void getColumns() {
        for (int i = 0; i < columns.length; i++) {
            columns[i] = sprite.getSubimage(i, 0, 1, Engine.TILE_SIZE);
        }
    }
    
}

