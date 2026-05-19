import greenfoot.*;
import java.util.List;

public class Door extends Actor {
    private enum DoorStyles {
        BARREN("IMAGE_BACKGROUND1_GAMEOVER_INTERIOR_OVERLAY", "IMAGE_BACKGROUND1_GAMEOVER_MASK", 0, 12),
        GARDEN_DAY("IMAGE_BACKGROUND1_GAMEOVER_INTERIOR_OVERLAY", "IMAGE_BACKGROUND1_GAMEOVER_MASK", -5, -25),
        GARDEN_NIGHT("IMAGE_BACKGROUND2_GAMEOVER_INTERIOR_OVERLAY", "IMAGE_BACKGROUND2_GAMEOVER_MASK", 0, 12),
        POOL_DAY("IMAGE_BACKGROUND3_GAMEOVER_INTERIOR_OVERLAY", "IMAGE_BACKGROUND3_GAMEOVER_MASK"),
        POOL_NIGHT("IMAGE_BACKGROUND4_GAMEOVER_INTERIOR_OVERLAY", "IMAGE_BACKGROUND4_GAMEOVER_MASK"),
        ROOF_DAY(null, "IMAGE_BACKGROUND5_GAMEOVER_MASK"),
        ROOF_NIGHT(null, "IMAGE_BACKGROUND6_GAMEOVER_MASK");

        private String interior;
        private String mask;
        private int maskOffsetX = 0;
        private int maskOffsetY = 0;

        DoorStyles(String interior, String mask, int maskOffsetX, int maskOffsetY) {
            this.interior = interior;
            this.mask = mask;
            this.maskOffsetX = maskOffsetX;
            this.maskOffsetY = maskOffsetY;
        }

        DoorStyles(String interior, String mask) {
            this.interior = interior;
            this.mask = mask;
        }

        public String getInterior() {
            return interior;
        }

        public String getMask() {
            return mask;
        }

        public int getMaskOffsetX() {
            return maskOffsetX;
        }

        public int getMaskOffsetY() {
            return maskOffsetY;
        }
    }

    public Door(ReanimManager reanimManager, Level.Style levelStyle) {
        var style = DoorStyles.valueOf(levelStyle.name());

        var interior = reanimManager.getImage(style.getInterior());
        var mask = reanimManager.getImage(style.getMask());
        
        var image = new GreenfootImage(300, 300);
        
        if (interior != null) {
            var interiorX = (image.getWidth() - interior.getWidth()) / 2;
            var interiorY = (image.getHeight() - interior.getHeight()) / 2;
            
            image.drawImage(interior, interiorX, interiorY);
            image.drawImage(mask, interiorX + style.getMaskOffsetX(), interiorY + style.getMaskOffsetY());
        } else {
            image.drawImage(mask, image.getWidth() / 2 + style.getMaskOffsetX(), image.getHeight() / 2 + style.getMaskOffsetY());
        }
        
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
