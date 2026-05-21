import greenfoot.*;

public class PlantGhost extends BaseActor {
    public static class Transparent extends PlantGhost {
        public Transparent(ReanimManager manager, SeedType seedType) {
            super(manager, seedType);
        }

        @Override
        protected void updateImage() {
            super.updateImage();
            
            getImage().setTransparency(100);
        }
    }
    
    private ReanimManager reanimManager;
    private SeedType seedType;
    
    @Override
    public float getHitboxWidth() {
        return 80;
    }

    @Override
    public float getHitboxHeight() {
        return 90;
    }
    
    public PlantGhost(ReanimManager manager, SeedType seedType) {
        this.reanimManager = manager;
        this.seedType = seedType;
        
        updateImage();
    }
    
    protected void updateImage() {
        var options = new ReanimRenderOptions() {
            public ReanimExtraState getMainState() {
                return new ReanimExtraState() {
                    public String getName() {
                        return seedType.getVisuals().reanimState();
                    }
                    
                    public float getCurrentFrame() {
                        return getInitFrame();
                    }
                    
                    public float getInitFrame() {
                        var typeVisuals = seedType.getVisuals();
                        return reanimManager.getFirstFrame(typeVisuals.reanimKey(), typeVisuals.reanimState());
                    }
                };
            }
        };
        
        setImage(reanimManager.renderSprite(seedType.getVisuals().reanimKey(), options));
    }
    
    public SeedType getSeedType() {
        return seedType;
    }
}
