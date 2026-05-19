import greenfoot.*;  


public class ZombieWithBucket extends BasicZombie
{
protected static final float BUCKET_HEALTH_BUFF = 1100;
    
    public ZombieWithBucket(ReanimManager manager) {
        super(manager, BASIC_MAX_HEALTH + BUCKET_HEALTH_BUFF);
        
        hideLayer("anim_hair");
        unhideLayer("anim_bucket");
        
        updateFrame();
    }
    
    @Override
    public void takeDamage(float amount) {
        super.takeDamage(amount);
        
        if (currentHp < maxHp - BUCKET_HEALTH_BUFF) {
            unhideLayer("anim_hair");
            hideLayer("anim_bucket");
        } else {
            var bucketHealth = currentHp - BASIC_MAX_HEALTH;
            
            if (bucketHealth < BUCKET_HEALTH_BUFF * 0.3) {
                addImageSwap("IMAGE_REANIM_ZOMBIE_BUCKET1", "IMAGE_REANIM_ZOMBIE_BUCKET3");
            } else if (bucketHealth < BUCKET_HEALTH_BUFF * 0.6) {
                addImageSwap("IMAGE_REANIM_ZOMBIE_BUCKET1", "IMAGE_REANIM_ZOMBIE_BUCKET2");
            }
        }
    }
}