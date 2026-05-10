import greenfoot.*;

public class Sun extends AnimatedActor {
    private static final int SUN_VALUE = 25;
    private static final float FALL_SPEED = 1.f;
    private static final float COLLISION_BOX_WIDTH = 100.f;
    private static final float COLLISION_BOX_HEIGHT = 100.f;
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
        if (Greenfoot.mouseClicked(null)) {
            MouseInfo mouse = Greenfoot.getMouseInfo();
            if (mouse != null) {
                float left = getRealX() - COLLISION_BOX_WIDTH / 2.f;
                float top = getRealY() - COLLISION_BOX_HEIGHT / 2.f;
                int mouseX = mouse.getX();
                int mouseY = mouse.getY();

                if ((left <= mouseX && mouseX <= left + COLLISION_BOX_WIDTH) && (top <= mouseY && mouseY <= top + COLLISION_BOX_HEIGHT)) {
                    collect();
                }
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
