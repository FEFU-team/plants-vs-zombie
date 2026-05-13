import greenfoot.*;

public class HitboxMap extends Actor {
    @Override
    public void addedToWorld(World world) {
        setImage(new GreenfootImage(world.getWidth(), world.getHeight()));
        update();
    }
    
    @Override
    public void act() {
        update();
    }
    
    protected void update() {
        var world = getWorld();
        if (world != null) {
            var img = getImage();
            img.clear();
            img.setColor(Color.RED);
            
            for (var actor : world.getObjects(BaseActor.class)) {
                var hitbox = actor.getHitbox();
                img.drawRect((int)hitbox.x, (int)hitbox.y, (int)hitbox.width, (int)hitbox.height);
            }
        }
    }
}
