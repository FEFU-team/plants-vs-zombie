import java.util.function.Function;

public enum SeedType {
    SunFlower(SunFlower::new,
        new PlantVisuals("REANIM_SUNFLOWER", "anim_idle", 4, 4, 0.55f),
        new PlantMechanics(7.5f, true, 50)),
    
    PeaShooter(PeaShooter::new,
        new PlantVisuals("REANIM_PEASHOOTERSINGLE", "anim_full_idle", 3, 4, 0.55f),
        new PlantMechanics(7.5f, true, 100)),
    
    WallNut(WallNut::new,
        new PlantVisuals("REANIM_WALLNUT", "anim_idle", 4, 10, 0.5f),
        new PlantMechanics(30.f, false, 50)),
    
    PotatoMine(PotatoMine::new,
        new PlantVisuals("REANIM_POTATOMINE", "anim_armed", 10, 15, 0.4f),
        new PlantMechanics(30.f, false, 25)),
    
    Chomper(Chomper::new,
        new PlantVisuals("REANIM_CHOMPER", "anim_idle", 10, 14, 0.42f),
        new PlantMechanics(7.5f, false, 150));
        
    public static record PlantVisuals(
        String reanimKey,
        String reanimState,
        float imageIndentX,
        float imageIndentY,
        float imageScale
    ) {}
    
    public static record PlantMechanics(
        float reloadInterval,
        boolean defaultReady,
        int sunCost
    ) {}

    private final Function<ReanimManager, ? extends Plant> creator;
    private final PlantVisuals visuals;
    private final PlantMechanics mechanics;

    SeedType(
        Function<ReanimManager, ? extends Plant> creator, 
        PlantVisuals visuals, 
        PlantMechanics mechanics
    ) {
        this.creator = creator;
        this.visuals = visuals;
        this.mechanics = mechanics;
    }

    public Plant create(ReanimManager manager) { return creator.apply(manager); }
    
    public PlantVisuals getVisuals() { return visuals; }
    public PlantMechanics getMechanics() { return mechanics; }
    
    public boolean isDefaultReady() { return mechanics.defaultReady(); }
    
    public int getSunCost() { return mechanics.sunCost(); }
}
