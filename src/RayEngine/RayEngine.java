package rayengine;

import lib.*;

public class RayEngine {

    public static void main(String[] args) {
        //leer mapa
        Map m = new Map("/res/map.txt");
        
        //crear player
        Player p = new Player(100, 80, 80);
        
        //iniciar el motor
        Engine e = new Engine(p, m);
        e.setTargetFPS(120);
        e.start();
    }
    
}
