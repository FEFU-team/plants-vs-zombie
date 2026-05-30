import greenfoot.*;
import java.awt.Rectangle;

public class Sun extends AnimatedActor {
    private static final int SUN_VALUE = 25;
    private static final float FALL_SPEED = 70.f;
    private static final int DISAPPEAR_TIME = 8;

    private Timer lifeTimer = new Timer();
    private Timer fallTimer = new Timer();
    private float targetY;
    private boolean falling = true;
    
    @Override
    public float getHitboxWidth() {
        return 80;
    }
    
    @Override
    public float getHitboxHeight() {
        return 80;
    }
    
    @Override
    public Rectangle.Float getHitbox() {
        float width = getHitboxWidth();
        float height = getHitboxHeight();
        
        return new Rectangle.Float(
            getRealX() - width / 2, getRealY() - height / 2,
            width, height
        );
    }

    public Sun(ReanimManager reanimManager) {
        this(reanimManager, -1);
    }

    public Sun(ReanimManager reanimManager, int targetY) {
        super(reanimManager, "REANIM_SUN", "Sun1");

        this.targetY = targetY;
    }

    @Override
    public void lifecycleStop() {
        super.lifecycleStop();
        lifeTimer.stop();
        fallTimer.stop();
    }

    @Override
    public void lifecycleStart() {
        super.lifecycleStart();
        lifeTimer.start();
        fallTimer.start();
    }
    
    public void setTargetY(int targetY) {
        this.targetY = targetY;
    }

    @Override
    public void act() {
        if (gameIsStopped()) return;
        
        super.act();

        if (lifeTimer.getDeltaSeconds() >= DISAPPEAR_TIME) {
            disappear();
        } else if (falling) {
            fall();
        }

        checkClick();
    }

    private void fall() {
        float realY = getRealY() + fallTimer.getDeltaSecondsAndReset() * FALL_SPEED;

        if (realY >= targetY) {
            realY = targetY;
            falling = false;
        }
        
        setLocation(getRealX(), realY);
    }

    private void checkClick() {
        if (Greenfoot.mouseClicked(null)) {
            MouseInfo mouse = Greenfoot.getMouseInfo();
            if (mouse != null && getHitbox().contains(mouse.getX(), mouse.getY())) {
                collect();
            }
        }
    }

    private void collect() {
        var world = getWorldOfType(MyWorld.class);
        if (world == null) return;
        
        SunManager manager = world.getSunManager();
        if (manager != null) {
            manager.addSun(SUN_VALUE);
        }
        getWorld().removeObject(this);
    }

    private void disappear() {
        getWorld().removeObject(this);
    }

    public int getValue() {
        return SUN_VALUE;
    }
}
