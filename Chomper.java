import greenfoot.*;
import java.awt.Rectangle;

public class Chomper extends Plant {
    private static final float ATTACK_INTERVAL = 1.425f;

    private Timer attackTimer = new Timer();

    public Chomper(ReanimManager manager) {
        super(manager, "REANIM_CHOMPER", 4000);

        setReanimState("anim_idle");
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
