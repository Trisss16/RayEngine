package rayengine;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;
import lib.*;

public class RayEngine {
    
    public static int[][] map = {
        {1,1,1,1,1,1},
        {1,0,0,0,0,1},
        {1,3,2,1,0,1},
        {1,0,0,0,0,1},
        {1,0,0,0,0,1},
        {1,1,1,1,1,1},
    };
    
    public static int[][] map2 = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,3,0,0,0,0,0,1},
        {1,0,0,0,3,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,3,0,0,0,0,0,1},
        {1,0,0,0,3,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,3,0,0,0,0,0,1},
        {1,0,0,0,3,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,2,2,2,0,0,0,0,0,0,0,2,2,2,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,2,0,2,0,0,0,0,0,0,0,2,0,2,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,2,0,2,0,0,0,0,0,0,0,2,0,2,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,2,0,2,0,0,0,0,0,0,0,2,0,2,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };
    

    public static void main(String[] args) {
        Map map = new Map("/res/map.txt");
        //Map map = new Map(map);
        //Map map = new Map(map2);
        
        //crear player
        Player p = new Player(120, 100, 100);
        
        //iniciar el motor
        Engine e = new Engine(p, map);
        e.setWindowSize(new Dimension(800, 600));
        e.setRaysToCast(1200); //indica cuantos rayos se lanzan
        e.setFOV(60); //indica el angulo de vision
        //e.setWindowSize(new Dimension(1280, 720));

        //e.setBackground(new Background("/res/background.png"));
        e.setBackground(new Background(Color.DARK_GRAY, Color.GRAY));
        //e.setBackground(new Background(Color.GREEN, Color.BLUE));
        
        map.addTileBehavior(1, new Sprite("/res/greystone.png"));
        map.addTileBehavior(2, new Sprite("/res/arrow.png"));
        map.addTileBehavior(3, new Sprite(Color.magenta));
        map.addTileBehavior(4, new Sprite(Color.red));
        map.addTileBehavior(10, new Sprite(Color.black));
        
        //Crear entidades
        Random r = new Random();
        Sprite entitySprite = new Sprite("/res/pillar.png");
        for (int i = 0; i < 1; i++) {
            int x, y;
            
            do {
                x = r.nextInt(map.n * Engine.TILE_SIZE);
                y = r.nextInt(map.m * Engine.TILE_SIZE);
            } while(map.insideOfWall(x, y));
            
            //e.addEntity(new Entity(entitySprite, x, y));
        }
        
        e.addEntity(new Entity(entitySprite, 320, 192));
        
        e.start();
    }
    
}