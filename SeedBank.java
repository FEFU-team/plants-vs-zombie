import greenfoot.*;
import java.util.*;
import java.util.function.Function;
import java.awt.Image;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
public class SeedBank {
    public static enum SeedType {
        SunFlower(SunFlower::new, "REANIM_SUNFLOWER", "anim_idle", 7.5f, true, 50),
        PeaShooter(PeaShooter::new, "REANIM_PEASHOOTERSINGLE", "anim_full_idle", 7.5f, true, 100),
        WallNut(WallNut::new, "REANIM_WALLNUT", "anim_idle", 30.f, false, 50),
        PotatoMine(PotatoMine::new, "REANIM_POTATOMINE", "anim_armed", 30.f, false, 25),
        Chomper(Chomper::new, "REANIM_CHOMPER", "anim_idle", 7.5f, false, 150);
        
        private Function<ReanimManager, ? extends Plant> creator;
        private String reanimKey;
        private String reanimState;
        private float reloadInterval;
        private boolean defaultReady;
        private int sunCost;
        
        SeedType(
            Function<ReanimManager, ? extends Plant> creator,
            String reanimKey,
            String reanimState,
            float reloadInterval,
            boolean defaultReady,
            int sunCost
        ) {
            this.creator = creator;
            this.reanimKey = reanimKey;
            this.reanimState = reanimState;
            this.reloadInterval = reloadInterval;
            this.defaultReady = defaultReady;
            this.sunCost = sunCost;
        }

        public int getSunCost() {
            return sunCost;
        }

        public Function<ReanimManager, ? extends Plant> getCreator() {
            return creator;
        }
    }
    
    public class Seed {
        private static int WIDTH = 50;
        private static int INDENT = 8;
        
        private SeedType type;
        private Timer reloadTimer = new Timer();
        
        Seed(SeedType type) {
            this.type = type;
           /* if (type.defaultReady) {
                reloadTimer.add(type.reloadInterval);
            }*/
        }
        
        private GreenfootImage getImage() {
            GreenfootImage seedBacks = reanimManager.getImage("IMAGE_SEEDS");
            var image = new GreenfootImage(WIDTH, seedBacks.getHeight());
            image.drawImage(seedBacks, -WIDTH * 2, 0);
            
            var options = new ReanimRenderOptions() {
                public ReanimExtraState getMainState() {
                    return new ReanimExtraState() {
                        public String getName() {
                            return type.reanimState;
                        }
                        
                        public float getCurrentFrame() {
                            return getInitFrame();
                        }
                        
                        public float getInitFrame() {
                            return reanimManager.getFirstFrame(type.reanimKey, type.reanimState);
                        }
                    };
                }
            };
            
            var plantImage = reanimManager.renderSprite(type.reanimKey, options);
            if (plantImage != null) {
                final float scale = 0.55f;
                
                var scaled = plantImage
                    .getAwtImage()
                    .getScaledInstance(
                        (int)(plantImage.getWidth() * scale),
                        (int)(plantImage.getHeight() * scale),
                        Image.SCALE_SMOOTH
                    );
                    
                var awtImage = image.getAwtImage();
                var g2d = awtImage.createGraphics();
                
                g2d.drawImage(scaled, 3 - scaled.getWidth(null) / 2, 4 - scaled.getHeight(null) / 2, null);
                
                float progress = Math.clamp(reloadTimer.getDeltaSeconds() / type.reloadInterval, 0.f, 1.f);
                boolean hasEnoughSun = sunManager.getSunCount() >= type.sunCost;

                if (progress < 1.f) {
                    int lightHeight = (int)(progress * awtImage.getHeight());
                    darkenAreaInplace(awtImage, 0, 0, awtImage.getWidth(), awtImage.getHeight() - lightHeight, 0.6f);
                    darkenAreaInplace(awtImage, 0, 0, awtImage.getWidth(), awtImage.getHeight(), 0.4f);
                }
               else if (!hasEnoughSun) {
                    darkenAreaInplace(awtImage, 0, 0, awtImage.getWidth(), awtImage.getHeight(), 0.4f);
                }
            } else {
                System.out.println("Cannot render plant image in SunBank");
            }
            
            image.setColor(Color.BLACK);
            image.setFont(new Font(14));
            String costText = String.valueOf(type.sunCost);
            if(costText.length()==2){
            image.drawString(costText, WIDTH - (image.getFont().getSize() * costText.length()) -8, image.getHeight() - 4);
        }
        else{
            image.drawString(costText, WIDTH - (image.getFont().getSize() * 2) -16, image.getHeight() - 4);
        }
            return image;
        }
        
        private static void darkenAreaInplace(BufferedImage img, int x, int y, int width, int height, float alpha) {
            alpha = Math.max(0.0f, Math.min(1.0f, alpha));
            if (alpha == 0.0f) return;
    
            var subImage = img.getSubimage(x, y, width, height);
            
            var g2d = subImage.createGraphics();
            try {
                g2d.setComposite(AlphaComposite.SrcAtop);
                g2d.setColor(new java.awt.Color(0, 0, 0, (int)(alpha * 255)));
                g2d.fillRect(0, 0, width, height);
            } finally {
                g2d.dispose();
            }
        }

        public boolean isReady() {
            return reloadTimer.getDeltaSeconds() >= type.reloadInterval && sunManager.getSunCount() >= type.sunCost;
        }

        public void resetReload() {
            reloadTimer.reset();
        }

        public SeedType getType() {
            return type;
        }
    }
    
    private static int SUN_BANK_WIDTH = 78;

    private MyWorld world;
    private ReanimManager reanimManager;
    private SunManager sunManager;
    private GreenfootImage bankImage;
    private Actor bankDisplay;
    
    private List<Seed> seeds;

    public SeedBank(MyWorld world, SunManager sunManager, ReanimManager reanimManager, List<SeedType> seeds) {
        this.world = world;
        this.sunManager = sunManager;
        this.reanimManager = reanimManager;
        
        this.seeds = seeds.stream().map(type -> new Seed(type)).toList();
        
        bankDisplay = new Actor() {};
        updateBankDisplay();
        world.addObject(bankDisplay, bankImage.getWidth() / 2, bankImage.getHeight() / 2);
    }

    public void lifecycleStop() {
        for (var seed : seeds) {
            seed.reloadTimer.stop();
        }
    }

    public void lifecycleStart() {
        for (var seed : seeds) {
            seed.reloadTimer.start();
        }
    }

    public void act() {
        updateBankDisplay();
    }

    public void updateBankDisplay() {
        GreenfootImage seedBank = reanimManager.getImage("IMAGE_SEEDBANK");

        if (bankImage == null) {
            bankImage = new GreenfootImage(seedBank.getWidth(), seedBank.getHeight());
        } else {
            bankImage.clear();
        }
        bankImage.drawImage(seedBank, 0, 0);

        bankImage.setColor(Color.BLACK);
        bankImage.setFont(new Font(20));

        String sunText = String.valueOf(sunManager.getSunCount());
        int textWidth = bankImage.getFont().getSize() * sunText.length() / 2;
        int textX = (SUN_BANK_WIDTH - textWidth) / 2;
        int textY = seedBank.getHeight() - 8;
        
        bankImage.drawString(sunText, textX, textY);
        
        for (int i = 0; i < seeds.size(); ++i) {
            var seedImage = seeds.get(i).getImage();
            bankImage.drawImage(
                seedImage,
                SUN_BANK_WIDTH + Seed.INDENT + (Seed.WIDTH + Seed.INDENT) * i,
                (bankImage.getHeight() - seedImage.getHeight()) / 2
            );
        }
        bankDisplay.setImage(bankImage);
    }

    public Seed getSeedAt(int x, int y) {
        if (y < 0 || y > bankImage.getHeight()) return null;
        if (x < SUN_BANK_WIDTH + Seed.INDENT) return null;

        int index = (x - SUN_BANK_WIDTH - Seed.INDENT) / (Seed.WIDTH + Seed.INDENT);
        if (index >= 0 && index < seeds.size()) {
            int startX = SUN_BANK_WIDTH + Seed.INDENT + index * (Seed.WIDTH + Seed.INDENT);
            if (x >= startX && x <= startX + Seed.WIDTH) {
                return seeds.get(index);
            }
        }
        return null;
    }

    public Actor getBankDisplay() {
        return bankDisplay;
    }
    public List<Seed> getSeeds() {
        return seeds;
    }
}