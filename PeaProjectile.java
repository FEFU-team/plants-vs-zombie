import greenfoot.*;
import java.awt.Rectangle;

public class PeaProjectile extends BaseActor {
    private static final float CELL_WIDTH = 90; // TODO: adjust and move to more suitable place
    private static final float DAMAGE = 20;
    private static final float SPEED = CELL_WIDTH * 4;

    private Timer moveTimer = new Timer();

    @Override
    public float getHitboxWidth() {
        return 30;
    }

    @Override
    public float getHitboxHeight() {
        return 30;
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

    public PeaProjectile(ReanimManager manager) {
        setImage(manager.getImage("IMAGE_PROJECTILEPEA"));
    }

    @Override
    public void lifecycleStop() {
        super.lifecycleStop();
        moveTimer.stop();
    }

    @Override
    public void lifecycleStart() {
        super.lifecycleStart();
        moveTimer.start();
    }

    @Override
    public void act() {
        setLocation(getRealX() + moveTimer.getDeltaSecondsAndReset() * SPEED, getRealY());

        var world = getWorld();
        if (world != null) {
            if (getRealX() >= world.getWidth()) {
                world.removeObject(this);
            }

            var hitbox = getHitbox();

            for (Zombie zombie : world.getObjects(Zombie.class)) {
                if (zombie.isAlive() && hitbox.intersects(zombie.getHitbox())) {
                    zombie.takeDamage(DAMAGE);
                    world.removeObject(this);
                    return;
                }
            }
        }
    }
}
