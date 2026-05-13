import greenfoot.*;


public class Plant extends AnimatedActor {
    private int health;
    
    @Override
    public float getHitboxWidth() {
        return 90; // TODO: adjust
    }
    
    @Override
    public float getHitboxHeight() {
        return 100; // TODO: adjust
    }

    public Plant(ReanimManager manager, String key, int health) {
        super(manager, key);
        
        this.health = health;
    }

    @Override
    public void act() {
        super.act();
    }
    
    public void takeDamage(int amount) {
        health -= amount;
        
        if (health <= 0) {
            var world = getWorld();
            if (world != null) {
                world.removeObject(this);
            }
        }
    }
}