package lib;

import java.awt.Graphics2D;

public class Entity {
    
    protected final Sprite s;
    
    protected double x;
    protected double y;
    
    public Entity(Sprite s, double x, double y) {
        this.s = s;
        this.x = x;
        this.y = y;
    }
    
    public Entity(String path, double x, double y) {
        s = new Sprite(path);
        this.x = x;
        this.y = y;
    }
    
    public void update(double dt)  {
    }
    
    public void render(Graphics2D g) {
    }
}
