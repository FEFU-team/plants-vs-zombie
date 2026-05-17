import greenfoot.*;


public class ZombieWithCone extends BasicZombie {
    protected static final float CONE_HEALTH_BUFF = 370;
    
    public ZombieWithCone(ReanimManager manager) {
        super(manager, BASIC_MAX_HEALTH + CONE_HEALTH_BUFF);
        
        hideLayer("anim_hair");
        unhideLayer("anim_cone");
        
        updateFrame();
    }
    
    @Override
    public void takeDamage(float amount) {
        super.takeDamage(amount);
        
        if (currentHp < maxHp - CONE_HEALTH_BUFF) {
            unhideLayer("anim_hair");
            hideLayer("anim_cone");
        } else {
            var coneHealth = currentHp - BASIC_MAX_HEALTH;
            
            if (coneHealth < CONE_HEALTH_BUFF * 0.3) {
                addImageSwap("IMAGE_REANIM_ZOMBIE_CONE1", "IMAGE_REANIM_ZOMBIE_CONE3");
            } else if (coneHealth < CONE_HEALTH_BUFF * 0.6) {
                addImageSwap("IMAGE_REANIM_ZOMBIE_CONE1", "IMAGE_REANIM_ZOMBIE_CONE2");
            }
        }
    }
}
