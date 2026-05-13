import greenfoot.*;

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

    public void lifecycleStop() {}

    public void lifecycleStart() {}
}
