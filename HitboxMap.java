import greenfoot.*;

public class HitboxMap extends Actor {
    private boolean showAttackBoxes = false;

    @Override
    public void addedToWorld(World world) {
        setImage(new GreenfootImage(world.getWidth(), world.getHeight()));
        update();
    }

    @Override
    public void act() {
        update();
    }

    public void toggleAttackBoxes(boolean show) {
        showAttackBoxes = show;
    }

    protected void update() {
        var world = getWorld();
        if (world != null) {
            var img = getImage();
            img.clear();

            for (var actor : world.getObjects(BaseActor.class)) {
                var hitbox = actor.getHitbox();
                img.setColor(Color.RED);
                img.drawRect((int)hitbox.x, (int)hitbox.y, (int)hitbox.width, (int)hitbox.height);

                if (showAttackBoxes && actor instanceof Plant plant) {
                    var attackBox = plant.getAttackTargetBox();
                    if (attackBox != null) {
                        img.setColor(Color.GREEN);
                        img.drawRect((int)attackBox.x, (int)attackBox.y, (int)attackBox.width, (int)attackBox.height);
                    }
                }
            }
        }
    }
}
