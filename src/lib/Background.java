package lib;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class Background {
    
    private final BufferedImage bg;
    
    public Background(String path) {
        bg = getBgImg(path);
    }
    
    public Background(Color ceiling, Color floor) {
        bg = getSolidColorImg(ceiling, floor);
    }
    
    private BufferedImage getBgImg(String path) {
        BufferedImage im;
        
        try {
            
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                //textura vacia
                return Sprite.getColorImg(Color.black);
            }
            im = ImageIO.read(is);
            
        } catch (IOException e) {
            
            im = Sprite.getColorImg(Color.black);
            //System.out.println("No se pudo leer la imagen: " + e);
            
        }
        
        return im;
    }
    
    
    private BufferedImage getSolidColorImg(Color ceiling, Color floor) {
        //mantiene el ancho y alto como pares
        int w = Engine.WIN_WIDTH % 2 == 0 ? Engine.WIN_WIDTH : Engine.WIN_WIDTH + 1;
        int h = Engine.WIN_HEIGHT % 2 == 0 ? Engine.WIN_HEIGHT : Engine.WIN_HEIGHT + 1;
        
        BufferedImage im = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
        
        Graphics2D g = im.createGraphics();
        
        g.setColor(ceiling);
        g.fillRect(0, 0, w, h / 2);
        g.setColor(floor);
        g.fillRect(0, h / 2, w, h / 2);
        g.dispose();
        
        return im;
    }
    
    public void draw(Graphics2D g, int x, int y, int w, int h) {
        g.drawImage(bg, x, y, w, h, null);
    }
}
