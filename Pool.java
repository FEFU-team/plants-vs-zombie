import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class Pool extends Actor
{
    private enum PoolStyles {
        POOL_DAY("pool.jpg"),
        POOL_NIGHT("pool_night.jpg");
        
        private String img;
        
        PoolStyles(String img) {
            this.img = img;
        }
        
        public String getimg() {
            return img;
        }
    }
    
    public Pool(String type) {
        PoolStyles current = PoolStyles.valueOf(type);
        GreenfootImage fullImage = new GreenfootImage(750,180);
        GreenfootImage firstImage = new GreenfootImage(current.getimg());
        firstImage.scale(750,159);
        GreenfootImage secondImage = new GreenfootImage(current.getimg());
        secondImage.scale(750,159);
        secondImage.mirrorVertically();
        fullImage.drawImage(firstImage,0,0);
        fullImage.drawImage(secondImage,0,21);
        this.setImage(fullImage);
    }
    
    public void act()
    {
        // Add your action code here.
    }
}
