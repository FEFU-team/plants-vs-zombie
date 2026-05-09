import greenfoot.*;

public class Sun extends Actor {
    private static final int SUN_VALUE = 25;
    private static final float FALL_SPEED = 1.f;
    private static final int DISAPPEAR_TIME = 300;
    private static final float ROTATION_SPEED = 0.8f; 

    private int lifeTimer = 0;
    private float realY;
    private float targetY;
    private boolean falling = true;
    private float rotationAngle = 0;

    private GreenfootImage sunRaysOriginal;
    private GreenfootImage sunMiddle;
    private GreenfootImage sunCore;
    private int maxWidth;
    private int maxHeight;

    public Sun(ReanimManager reanimManager, int startX, int startY, int targetY) {
        this.realY = startY;
        this.targetY = targetY;

       
        try {
            sunRaysOriginal = new GreenfootImage("./images/reanim/Sun3.png");
            sunMiddle = new GreenfootImage("./images/reanim/Sun2.png");
            sunCore = new GreenfootImage("./images/reanim/Sun1.png");

            maxWidth = Math.max(Math.max(sunRaysOriginal.getWidth(), sunMiddle.getWidth()), sunCore.getWidth());
            maxHeight = Math.max(Math.max(sunRaysOriginal.getHeight(), sunMiddle.getHeight()), sunCore.getHeight());

            updateImage();
        } catch (Exception e) {
            GreenfootImage fallback = new GreenfootImage(40, 40);
            fallback.setColor(Color.YELLOW);
            fallback.fillOval(0, 0, 40, 40);
            setImage(fallback);
        }
    }

    private void updateImage() {
        GreenfootImage combined = new GreenfootImage(maxWidth, maxHeight);
        GreenfootImage rotatedRays1 = new GreenfootImage(sunRaysOriginal);
        rotatedRays1.rotate((int)rotationAngle);
        int rays1X = (maxWidth - rotatedRays1.getWidth()) / 2;
        int rays1Y = (maxHeight - rotatedRays1.getHeight()) / 2;
        combined.drawImage(rotatedRays1, rays1X, rays1Y);

        GreenfootImage rotatedMiddle1 = new GreenfootImage(sunMiddle);
        rotatedMiddle1.rotate((int)(rotationAngle * 0.7));
        int middle1X = (maxWidth - rotatedMiddle1.getWidth()) / 2;
        int middle1Y = (maxHeight - rotatedMiddle1.getHeight()) / 2;
        combined.drawImage(rotatedMiddle1, middle1X, middle1Y);

        int coreX = (maxWidth - sunCore.getWidth()) / 2;
        int coreY = (maxHeight - sunCore.getHeight()) / 2;
        combined.drawImage(sunCore, coreX, coreY);

        setImage(combined);
    }

    public void act() {
        rotationAngle = (rotationAngle + ROTATION_SPEED) % 360;
        updateImage();

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
        realY += FALL_SPEED;
        setLocation(getX(), (int)realY);

        if (realY >= targetY) {
            realY = targetY;
            setLocation(getX(), (int)realY);
            falling = false;
        }
    }

    private void checkClick() {
        if (Greenfoot.mouseClicked(this)) {
            collect();
        }
    }

    private void collect() {
        SunManager manager = getWorldOfType(MyWorld.class).getSunManager();
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
