import greenfoot.*;
import java.awt.Rectangle;

public class WallNut extends Plant {
    public WallNut(ReanimManager manager) {
        super(manager, "REANIM_WALLNUT", 4000);
        
        setReanimState("anim_idle");
        updateFrame();
    }
    
    @Override
    public void act() {
        super.act();
        
        // TODO: update body image on damage
        // TODO: maybe stop animation while it's attacked like in original game
    }
}
