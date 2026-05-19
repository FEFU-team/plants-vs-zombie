import greenfoot.*;
import java.util.List;

public class Door extends Actor {
    private enum DoorStyles {
        BARREN("IMAGE_BACKGROUND1_GAMEOVER_INTERIOR_OVERLAY"),
        GARDEN_DAY("IMAGE_BACKGROUND1_GAMEOVER_INTERIOR_OVERLAY"),
        GARDEN_NIGHT("IMAGE_BACKGROUND2_GAMEOVER_INTERIOR_OVERLAY"),
        POOL_DAY("IMAGE_BACKGROUND3_GAMEOVER_INTERIOR_OVERLAY"),
        POOL_NIGHT("IMAGE_BACKGROUND4_GAMEOVER_INTERIOR_OVERLAY"),
        ROOF_DAY("IMAGE_BACKGROUND5_GAMEOVER_MASK"),
        ROOF_NIGHT("IMAGE_BACKGROUND6_GAMEOVER_MASK");

        private String key;

        DoorStyles(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public Door(ReanimManager reanimManager, Level.Style style) {
        DoorStyles current = DoorStyles.valueOf(style.name());

        var image = reanimManager.getImage(current.getKey());
        image.setTransparency(0);
        setImage(image);
    }

    void changeStatus() {
        var world = getWorld();
        if (world == null) return;

        for (Zombie zombie : world.getObjects(Zombie.class)) {
            if (Math.abs(zombie.getDistanceFromDoor(this).x) < 20) {
                getImage().setTransparency(255);
                return;
            }
        }
    }

    public void act()
    {
        changeStatus();
    }
}
