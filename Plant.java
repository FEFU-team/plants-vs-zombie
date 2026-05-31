import greenfoot.*;
import java.awt.image.*;
import java.awt.Rectangle;

public class Plant extends AnimatedActor {
    protected float maxHealth;
    protected float health;
    protected boolean highlight;

    @Override
    public float getHitboxWidth() {
        return 80;
    }

    @Override
    public float getHitboxHeight() {
        return 90;
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
    
    public boolean isGhost() {
        return false;
    }
    
    @Override
    public void setImage(GreenfootImage image) {
        if (highlight && image != null) {
            var op = new RescaleOp(1.1f, 20.f, null);
            op.filter(image.getAwtImage(), image.getAwtImage());
        }
        
        super.setImage(image);
    }
    
    public void highlight(boolean highlight) {
        this.highlight = highlight;
    }
}