import greenfoot.*;
import java.util.List;

public class Door extends Actor {
    private enum DoorStyles {
        BARREN("background1_gameover_interior_overlay.png"),
        GARDEN_DAY("background1_gameover_interior_overlay.png"),
        GARDEN_NIGHT("background2_gameover_interior_overlay.png"),
        POOL_DAY("background3_gameover_interior_overlay.png"),
        POOL_NIGHT("background4_gameover_interior_overlay.png"),
        ROOF_DAY("background5_gameover_mask.png"),
        ROOF_NIGHT("background6_gameover_mask.png");
        
        private String img;
        
        DoorStyles(String img) {
            this.img = img;
        }
        
        public String getimg() {
            return img;
        }
    }
    
    public Door(String style) {
        DoorStyles current = DoorStyles.valueOf(style);
        
        //String maskImage = current.getimg().replace("interior_overlay","mask");
        GreenfootImage image = new GreenfootImage(current.getimg());
        //GreenfootImage overlay = new GreenfootImage(current.getimg());
        //image.drawImage(overlay,0,5);
        
        image.setTransparency(0);
        this.setImage(image);
    }
    
    void changeStatus() {
        List<Zombie> zombies = getWorld().getObjects(Zombie.class);
        for (Zombie z : zombies) {
            if (z.YDifference(this) == 50) {
                this.getImage().setTransparency(255);
                return;
            }
        }
    }
    
    public void act()
    {
        changeStatus();
    }
}
