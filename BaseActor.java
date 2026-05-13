import greenfoot.*;
import java.awt.Rectangle;

public class BaseActor extends Actor {
    private float realX;
    private float realY;
    
    public float getRealX() {
        return realX;
    }

    public float getRealY() {
        return realY;
    }

    @Override
    public int getX() {
        return Math.round(realX);
    }

    @Override
    public int getY() {
        return Math.round(realY);
    }

    @Override
    public void setLocation(int x, int y) {
        setLocation((float)x, (float)y);
    }

    public void setLocation(float x, float y) {
        realX = x;
        realY = y;
        super.setLocation((int)x, (int)y);
    }
    
    public float getHitboxWidth() {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    public float getHitboxHeight() {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    public Rectangle.Float getHitbox() {
        return new Rectangle.Float(
            getRealX(), getRealY(),
            getHitboxWidth(), getHitboxHeight()
        );
    }

    public void lifecycleStop() {}

    public void lifecycleStart() {}
}
