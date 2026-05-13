import greenfoot.*;


public class Plant extends AnimatedActor {
    private float health;
    
    @Override
    public float getHitboxWidth() {
        return 80; // TODO: adjust
    }
    
    @Override
    public float getHitboxHeight() {
        return 90; // TODO: adjust
    }

    public Plant(ReanimManager manager, String key, float health) {
        super(manager, key);
        
        this.health = health;
    }

    @Override
    public void act() {
        super.act();
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
}