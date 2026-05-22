import greenfoot.*;
import java.awt.Rectangle;

public class PeaShooter extends Plant {
    protected static final float ATTACK_INTERVAL = 1.425f;

    protected Timer attackTimer = new Timer();
    protected boolean attackDelayActive = false;
    protected Timer attackDelayTimer = new Timer();

    protected PeaShooter(ReanimManager manager, String reanimKey) {
        super(manager, reanimKey, 300);

        setReanimState("anim_full_idle");
        setReanimSpeed(1.6f);
        updateFrame();
    }

    public PeaShooter(ReanimManager manager) {
        this(manager, "REANIM_PEASHOOTERSINGLE");
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
        if (gameIsStopped()) return;
        
        if (attackDelayActive && attackDelayTimer.getDeltaSeconds() > getAttackDelay()) {
            attackDelayActive = false;
            attackDelayTimer.stop();
            attackDelayTimer.reset();
            shoot();
        }

        if (attackTimer.getDeltaSeconds() >= ATTACK_INTERVAL && checkAttackTargets() && !attackDelayActive) {
            attackDelayActive = true;
            attackDelayTimer.start();
            attackTimer.reset();
            playShootAnimation();
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
    
    public float getAttackDelay() {
        return 0.35f;
    }
    
    public float getAttackAnimationSpeed() {
        return 2.5f;
    }
    
    protected void playShootAnimation() {
        setReanimExtraState(
            new AnimationStateBuilder()
                .name("anim_shooting")
                .speed(getAttackAnimationSpeed())
                .loop(false)
                .build()
        );
    }

    protected void shoot() {
        var world = getWorld();
        if (world != null) {
            var hitbox = getHitbox();
            world.addObject(
                new PeaProjectile(reanimManager),
                (int)(hitbox.x + hitbox.width * 0.96),
                (int)(hitbox.y + hitbox.height * 0.3)
            );
        }
    }
}
