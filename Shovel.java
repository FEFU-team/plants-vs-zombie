import greenfoot.*;
import java.awt.Rectangle;

public class Shovel extends BaseActor {
    public static final float IN_BANK_POSITION_X = ShovelBank.POSITION_X;
    public static final float IN_BANK_POSITION_Y = ShovelBank.POSITION_Y;
    
    private ReanimManager reanimManager;
    private Plant tintedPlant;
    private boolean isMoving;
    
    public Shovel(ReanimManager manager) {
        this.reanimManager = manager;
        
        setImage(manager.getImage("IMAGE_SHOVEL"));
    }
    
    @Override
    public float getHitboxWidth() {
        return 60;
    }
    
    @Override
    public float getHitboxHeight() {
        return 60;
    }
    
    @Override
    public Rectangle.Float getHitbox() {
        float width = getHitboxWidth();
        float height = getHitboxHeight();
        
        return new Rectangle.Float(
            getRealX() - width / 2, getRealY() - height / 2,
            width, height
        );
    }
    
    protected Plant getPlantAtMouse() {
        var world = getWorldOfType(LevelWorld.class);
        if (world == null) return null;
        
        var level = world.getLevel();
        
        var mouse = Greenfoot.getMouseInfo();
        if (mouse != null) {
            var cell = level.getCellAt(mouse.getX(), mouse.getY());
            if (cell == null) return null;
            
            return level.getPlantAtCell(cell.x, cell.y);
        }
        
        return null;
    }
    
    @Override
    public void act() {
        var mouse = Greenfoot.getMouseInfo();
        if (mouse != null) {
            if (Greenfoot.mousePressed(null) && mouse.getButton() == 1) {
                isMoving = getHitbox().contains(mouse.getX(), mouse.getY());
            }
            
            if (isMoving) {
                var target = getPlantAtMouse();
                if (target != tintedPlant) {
                    if (tintedPlant != null) {
                        tintedPlant.highlight(false);
                    }
                    if (target != null){
                        target.highlight(true);
                    }
                    
                    tintedPlant = target;
                }
        
                if (Greenfoot.mouseClicked(null)) {
                    isMoving = false;
                    if (target != null) {
                        if (mouse.getButton() == 1) {
                            target.getWorld().removeObject(target);
                        } else {
                            target.highlight(false);
                        }
                    }
                    setLocation(IN_BANK_POSITION_X, IN_BANK_POSITION_Y);
                }
            }
            
            if (isMoving) {
                setLocation(mouse.getX(), mouse.getY());
            }
        }
    }
}
