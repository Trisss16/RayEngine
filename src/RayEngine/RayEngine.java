package rayengine;

import lib.*;

public class RayEngine {

    public static void main(String[] args) {
        //leer mapa
        Map m = new Map("/res/map.txt");
        
        //crear player
        Player p = new Player(100, 127, 127);
        
        //iniciar el motor
        Engine e = new Engine(p, m);
        e.setTargetFPS(60);
        e.start();
    }
    
}
