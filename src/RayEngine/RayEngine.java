package rayengine;

import java.awt.Dimension;
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
        //Map m = new Map(map);
        
        //crear player
        Player p = new Player(100, 100, 100);
        
        //iniciar el motor
        Engine e = new Engine(p, m);
        e.setWindowSize(new Dimension(800, 600));
        e.setTargetFPS(60);
        e.setRaysToCast(800); //indica cuantos rayos se lanzan
        e.setFOV(60); //indica el angulo de vision
        e.start();
    }
    
}
