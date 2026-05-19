import greenfoot.*;

public class ZombiesWon extends AnimatedActor {
    public static final float POSITION_X = 100;
    public static final float POSITION_Y = 0;
    
    public ZombiesWon(ReanimManager manager) {
        super(manager, "REANIM_ZOMBIESWON");
        setReanimState("ZombiesWon", false);
        setReanimSpeed(0.3f);
        
        setCanvas(new GreenfootImage(2000, 1200));
        updateFrame();
    }
}
