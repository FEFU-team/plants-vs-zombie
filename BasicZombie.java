import greenfoot.*;


public class BasicZombie extends Zombie {
    protected static final float BASIC_MAX_HEALTH = 190;
    
    public BasicZombie(ReanimManager manager) {
        this(manager, BASIC_MAX_HEALTH);
    }
    
    protected BasicZombie(ReanimManager manager, float health) {
        super(manager, "REANIM_ZOMBIE", health, 1 / 5.f);
        setReanimSpeed(1.f);
        
        hideLayer("Zombie_flaghand");
        hideLayer("Zombie_innerarm_screendoor");
        hideLayer("Zombie_duckytube");
        hideLayer("Zombie_whitewater");
        hideLayer("Zombie_mustache");
        hideLayer("anim_screendoor");
        hideLayer("Zombie_innerarm_screendoor_hand");
        hideLayer("Zombie_outerarm_screendoor");
        hideLayer("Zombie_whitewater2");
        hideLayer("anim_cone");
        hideLayer("anim_bucket");
        
        updateFrame();
    }
}
