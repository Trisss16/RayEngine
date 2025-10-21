package lib;

import java.awt.*;

public class RayCaster {
    
    private Point player;
    private double angle; //angulo del jugador en radianes
    
    private double px, py;
    
    
    private int mapM;
    private int mapN;
    
    
    public RayCaster(Player p) {
        updatePlayerInfo(p);
    }
    
    
    private void updatePlayerInfo(Player p) {
        player = p.getPlayerPos();
        px = player.x;
        py = player.y;
        
        angle = p.getRadAngle();
    }
    
    
    
    public void update(double dt, Player p) {
        updatePlayerInfo(p);
    }
    
    
    private void castRays() {
        
        for (int i = 0; i < 1; i++) {
            
        }
    }
    
    
    
    public void renderSimulation3D(Graphics2D g) {
    }
    
    public void renderView2D(Graphics2D g) {
    }
}


class Ray {
    
    private double angle;
    
    private double px, py;
    
    private boolean facingUp;
    private boolean facingDown;
    private boolean facingLeft;
    private boolean facingRight;
    
    
    public Ray(double angle, Point pos) {
        this.angle = angle;
        px = pos.x;
        py = pos.y;
        
        facingUp = angle > Math.PI && angle < Math.PI;
    }
    
            
}