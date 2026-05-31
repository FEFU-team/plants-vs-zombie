import greenfoot.*;
import java.awt.Rectangle;

public class SunFlower extends Plant {
    private static final int SUN_SPAWN_INTERVAL = 24;
    
    private Timer sunSpawnTimer = new Timer();
    
    public SunFlower(ReanimManager manager) {
        super(manager, "REANIM_SUNFLOWER", 300);
        
        setReanimState("anim_idle");
        updateFrame();
    }
    
    @Override
    public void lifecycleStop() {
        super.lifecycleStop();
        sunSpawnTimer.stop();
    }

    @Override
    public void lifecycleStart() {
        super.lifecycleStart();
        sunSpawnTimer.start();
    }
    
    @Override
    public void act() {
        if (gameIsStopped()) return;
        
        super.act();
        
        if (sunSpawnTimer.getDeltaSeconds() >= SUN_SPAWN_INTERVAL) {
            var world = getWorld();
            if (world != null) {
                var hitbox = getHitbox();
                
                Sun sun = new Sun(reanimManager, (int)hitbox.getMaxY());
                sun.setTargetY((int)(hitbox.getMaxY() - sun.getHitboxHeight() * 0.4));
                
                world.addObject(
                    sun,
                    (int)(hitbox.x + hitbox.width * 0.3),
                    (int)(hitbox.y + hitbox.height * 0.3)
                );
                sunSpawnTimer.reset();
            }
        }
    }
}
