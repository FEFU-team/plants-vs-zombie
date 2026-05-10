import greenfoot.*;

public class Sun extends AnimatedActor {
    private static final int SUN_VALUE = 25;
    private static final float FALL_SPEED = 1.f;
    private static final float COLLISION_BOX_WIDTH = 104.f;
    private static final float COLLISION_BOX_HEIGHT = 104.f;
    private static final int DISAPPEAR_TIME = 300;

    private int lifeTimer = 0;
    private float targetY;
    private boolean falling = true;

    public Sun(ReanimManager reanimManager, int targetY) {
        super(reanimManager, "REANIM_SUN", "Sun1");

        this.targetY = targetY;
    }

    public void act() {
        super.act();

        if (falling) {
            fall();
        } else {
            lifeTimer++;
            if (lifeTimer >= DISAPPEAR_TIME) {
                disappear();
            }
        }

        checkClick();
    }

    private void fall() {
        float realY = getRealY() + FALL_SPEED;

        if (realY >= targetY) {
            realY = targetY;
            falling = false;
        }
        
        setLocation(getRealX(), realY);
    }

    private void checkClick() {
        if (Greenfoot.mouseClicked(this)) {
            collect();
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
