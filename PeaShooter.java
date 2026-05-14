import greenfoot.*;

public class PeaShooter extends Plant {
    private static final float ATTACK_INTERVAL = 1.425f;

    private Timer attackTimer = new Timer();

    public PeaShooter(ReanimManager manager) {
        super(manager, "REANIM_PEASHOOTERSINGLE", 300);

        setReanimState("anim_full_idle");
        updateFrame();
    }

    @Override
    public void lifecycleStop() {
        super.lifecycleStop();
        attackTimer.stop();
    }

    @Override
    public void lifecycleStart() {
        super.lifecycleStart();
        attackTimer.start();
    }

    @Override
    public void act() {
        super.act();
    }
}
