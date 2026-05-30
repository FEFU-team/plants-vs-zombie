import greenfoot.*;

public class HitboxMap extends Actor {
    private boolean showAttackBoxes = false;
    private boolean showCellBoxes = false;

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

    public void toggleCellBoxes(boolean show) {
        showCellBoxes = show;
    }

    protected void update() {
        var world = getWorld();
        if (world != null) {
            var img = getImage();
            img.clear();
            
            if (showCellBoxes && world instanceof LevelWorld levelWorld) {
                var level = levelWorld.getLevel();
                
                var winBox = level.getWinHitbox();
                img.setColor(Color.BLUE);
                img.drawRect((int)winBox.x, (int)winBox.y, (int)winBox.width, (int)winBox.height);
                
                for (int i = 0; i < level.getColsCount(); ++i) {
                    for (int j = 0; j < level.getRowsCount(); ++j) {
                        img.setColor(Color.BLUE);
                        img.drawRect(
                            Level.CELL_GRID_START_X + i * Cell.WIDTH,
                            Level.CELL_GRID_START_Y + j * Cell.HEIGHT,
                            Cell.WIDTH,
                            Cell.HEIGHT
                        );
                    }
                }
            }

            for (var actor : world.getObjects(BaseActor.class)) {
                try {
                    var hitbox = actor.getHitbox();
                    img.setColor(Color.RED);
                    img.drawRect((int)hitbox.x, (int)hitbox.y, (int)hitbox.width, (int)hitbox.height);
                } catch (UnsupportedOperationException error) {
                    continue;
                }

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
