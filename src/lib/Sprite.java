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
    
    //crea un sprite con un color solido
    public Sprite(Color clr) {
        sprite = getColorImg(clr);
        columns = new BufferedImage[Engine.TILE_SIZE];
        getColumns();
    }
    
    public final BufferedImage getColorImg(Color clr) {
        BufferedImage img = new BufferedImage(Engine.TILE_SIZE, Engine.TILE_SIZE, BufferedImage.TRANSLUCENT);
        Graphics2D g = img.createGraphics();
        g.setColor(clr);
        g.fillRect(0, 0, Engine.TILE_SIZE, Engine.TILE_SIZE);
        g.dispose();
        return img;
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
                //cuando no se procesa correctamente una textura crea un cuadrado magenta
                return getColorImg(Color.magenta);
            }
            
            im = ImageIO.read(is);
            im = Sprite.reescale(im, Engine.TILE_SIZE, Engine.TILE_SIZE);
            //System.out.println("Imagen extraida.");
            
        } catch (IOException e) {
            
            im = getColorImg(Color.magenta);
            //System.out.println("No se pudo leer la imagen: " + e);
            
        }
        
        return im;
    }
    
    
    private void getColumns() {
        for (int i = 0; i < columns.length; i++) {
            columns[i] = sprite.getSubimage(i, 0, 1, Engine.TILE_SIZE);
        }
    }
    
}

