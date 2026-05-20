import greenfoot.*;
import java.util.*;
import java.util.function.Function;
import java.awt.Image;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;

public class SeedBank {
    public static enum SeedType {
        SunFlower(SunFlower::new, "REANIM_SUNFLOWER", "anim_idle", 7.5f, true),
        PeaShooter(PeaShooter::new, "REANIM_PEASHOOTERSINGLE", "anim_full_idle", 7.5f, true),
        WallNut(WallNut::new, "REANIM_WALLNUT", "anim_idle", 30.f, false),
        PotatoMine(PotatoMine::new, "REANIM_POTATOMINE", "anim_armed", 30.f, false),
        Chomper(Chomper::new, "REANIM_CHOMPER", "anim_idle", 7.5f, false);
        
        private Function<ReanimManager, ? extends Plant> creator;
        private String reanimKey;
        private String reanimState;
        private float reloadInterval;
        private boolean defaultReady;
        
        SeedType(
            Function<ReanimManager, ? extends Plant> creator,
            String reanimKey,
            String reanimState,
            float reloadInterval,
            boolean defaultReady
        ) {
            this.creator = creator;
            this.reanimKey = reanimKey;
            this.reanimState = reanimState;
            this.reloadInterval = reloadInterval;
            this.defaultReady = defaultReady;
        }
    }
    
    private class Seed {
        private static int WIDTH = 50;
        private static int INDENT = 8;
        
        private SeedType type;
        private Timer reloadTimer = new Timer();
        
        Seed(SeedType type) {
            this.type = type;
            
            if (type.defaultReady) {
                reloadTimer.add(type.reloadInterval);
            }
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
                final float scale = 0.55f; // TODO: specify scale separately for each seed type
                
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
                
                if (progress < 1.f) {
                    int lightHeight = (int)(progress * awtImage.getHeight());
                    darkenAreaInplace(awtImage, 0, 0, awtImage.getWidth(), awtImage.getHeight() - lightHeight, 0.4f);
                    // TODO: left part also should be tinted slightly lighter while progress < 1.f
                    // TODO: tint if not enough suns
                }
            } else {
                System.out.println("Cannot render plant image in SunBank");
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
        
        updateBankDisplay();
        bankDisplay = new Actor() {};
        bankDisplay.setImage(bankImage);
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
    }
}
