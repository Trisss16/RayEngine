package rayengine;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JDialog;
import javax.swing.JPanel;
import lib.*;

public class RayEngine {
    
    public static int[][] map = {
        {1,1,1,1,1,1},
        {1,0,0,0,0,1},
        {1,0,0,1,0,1,},
        {1,0,0,0,0,1},
        {1,0,0,0,0,1},
        {1,1,1,1,1,1},
    };

    public static void main(String[] args) {
        //leer mapa
        
        Map m = new Map("/res/map.txt");
        Map m2 = new Map(map);
        
        //crear player
        Player p = new Player(100, 100, 100);
        
        //iniciar el motor
        Engine e = new Engine(p, m);
        e.setWindowSize(new Dimension(400, 300));
        e.setTargetFPS(60);
        e.setRaysToCast(800); //indica cuantos rayos se lanzan
        e.setFOV(60); //indica el angulo de vision
        
        //e.add(i.sprite);
        JDialog dialog = new JDialog();
        dialog.add(new CustomPanel());
        dialog.pack();
        dialog.setLocationRelativeTo(e);
        dialog.setVisible(true);
        e.start();
    }
    
}


class CustomPanel extends JPanel {
    
    private final Sprite spr;
    
    public CustomPanel() {
        spr = new Sprite("/res/greystone.png");
        this.setPreferredSize(new Dimension(64, 150));
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int ctr = 0;
        
        while(ctr < Engine.TILE_SIZE * 2) {
            spr.drawColumn((Graphics2D) g, ctr, ctr, 0, 1, Engine.TILE_SIZE);
            ctr++;
        }
        
        spr.drawSprite((Graphics2D) g, 0, Engine.TILE_SIZE, Engine.TILE_SIZE, Engine.TILE_SIZE);
    }
}
