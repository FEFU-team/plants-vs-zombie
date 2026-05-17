import greenfoot.*;
import java.awt.Rectangle;

public class PeaShooter extends Plant {
    private static final float ATTACK_INTERVAL = 1.425f;
    private static final float ATTACK_DELAY = 0.6f; // TODO: speed up shoot animation, and reduce delay

    private Timer attackTimer = new Timer();
    private boolean attackDelayActive = false;
    private Timer attackDelayTimer = new Timer();

    public PeaShooter(ReanimManager manager) {
        super(manager, "REANIM_PEASHOOTERSINGLE", 300);

        setReanimState("anim_full_idle");
        setReanimSpeed(1.3f);
        updateFrame();
    }

    @Override
    public void lifecycleStop() {
        super.lifecycleStop();
        attackTimer.stop();

        if (attackDelayActive) {
            attackDelayTimer.stop();
        }
    }

    @Override
    public void lifecycleStart() {
        super.lifecycleStart();
        attackTimer.start();

        if (attackDelayActive) {
            attackDelayTimer.start();
        }
    }

    @Override
    public void act() {
        if (attackDelayActive && attackDelayTimer.getDeltaSeconds() > ATTACK_DELAY) {
            attackDelayActive = false;
            attackDelayTimer.stop();
            attackDelayTimer.reset();
            shoot();
        }

        if (attackTimer.getDeltaSeconds() >= ATTACK_INTERVAL && checkAttackTargets() && !attackDelayActive) {
            attackDelayActive = true;
            attackDelayTimer.start();
            attackTimer.reset();
            setReanimExtraState(
                new AnimationStateBuilder()
                    .name("anim_shooting")
                    .speed(1.3f)
                    .loop(false)
                    .build()
            );
        }

        super.act();
    }

    @Override
    public Rectangle.Float getAttackTargetBox() {
        var world = getWorld();
        if (world == null) return null;

        var hitbox = getHitbox();
        var x = (float)hitbox.getCenterX();

        return new Rectangle.Float(
            x, getRealY() + hitbox.height * 0.2f,
            world.getWidth() - x, hitbox.height * 0.6f
        );
    }

    public void shoot() {
        var world = getWorld();
        if (world != null) {
            var hitbox = getHitbox();
            world.addObject(
                new PeaProjectile(reanimManager),
                (int)(hitbox.x + hitbox.width * 0.85),
                (int)(hitbox.y + hitbox.height * 0.25)
            );
        }
    }
}
