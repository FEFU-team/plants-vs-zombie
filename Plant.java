import greenfoot.*;
import java.awt.Rectangle;

public class Plant extends AnimatedActor {
    protected float maxHealth;
    protected float health;

    @Override
    public float getHitboxWidth() {
        return 80; // TODO: adjust
    }

    @Override
    public float getHitboxHeight() {
        return 90; // TODO: adjust
    }

    public Rectangle.Float getAttackTargetBox() {
        return null;
    }

    public Plant(ReanimManager manager, String key, float health) {
        super(manager, key);

        this.maxHealth = health;
        this.health = health;
    }

    public void takeDamage(float amount) {
        health -= amount;

        if (health <= 0) {
            var world = getWorld();
            if (world != null) {
                world.removeObject(this);
            }
        }
    }
    
    protected Zombie findAttackTarget() {
        var world = getWorld();
        if (world == null) return null;

        var attackHitbox = getAttackTargetBox();
        if (attackHitbox == null) return null;

        for (Zombie zombie : world.getObjects(Zombie.class)) {
            if (!zombie.isUntouchable() && attackHitbox.intersects(zombie.getHitbox())) {
                return zombie;
            }
        }

        return null;
    }
    
    protected boolean checkAttackTargets() {
        return findAttackTarget() != null;
    }
}