import greenfoot.*;
import java.util.*;
import java.awt.Image;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;

public class SeedBank {
    public class Seed {
        private static int WIDTH = 50;
        private static int HEIGHT = 70;
        private static int INDENT = 8;
        
        private SeedType type;
        private GreenfootImage image;
        private Timer reloadTimer = new Timer();
        
        Seed(SeedType type) {
            this.type = type;
            if (type.isDefaultReady()) {
                reloadTimer.add(type.getMechanics().reloadInterval());
            }
        }
        
        private GreenfootImage getImage() {
            GreenfootImage seedBacks = reanimManager.getImage("IMAGE_SEEDS");
            image = new GreenfootImage(WIDTH, seedBacks.getHeight());
            image.drawImage(seedBacks, -WIDTH * 2, 0);
            
            var options = new ReanimRenderOptions() {
                public ReanimExtraState getMainState() {
                    return new ReanimExtraState() {
                        public String getName() {
                            return type.getVisuals().reanimState();
                        }
                        
                        public float getCurrentFrame() {
                            return getInitFrame();
                        }
                        
                        public float getInitFrame() {
                            var typeVisuals = type.getVisuals();
                            return reanimManager.getFirstFrame(typeVisuals.reanimKey(), typeVisuals.reanimState());
                        }
                    };
                }
            };
            
            var plantImage = reanimManager.renderSprite(type.getVisuals().reanimKey(), options);
            if (plantImage != null) {
                var scaled = plantImage
                    .getAwtImage()
                    .getScaledInstance(
                        (int)(plantImage.getWidth() * type.getVisuals().imageScale()),
                        (int)(plantImage.getHeight() * type.getVisuals().imageScale()),
                        Image.SCALE_SMOOTH
                    );
                    
                var awtImage = image.getAwtImage();
                var g2d = awtImage.createGraphics();
                
                g2d.drawImage(
                    scaled,
                    (int)(type.getVisuals().imageIndentX() - scaled.getWidth(null) / 2.f),
                    (int)(type.getVisuals().imageIndentY() - scaled.getHeight(null) / 2.f),
                    null
                );
                
                float progress = Math.clamp(reloadTimer.getDeltaSeconds() / type.getMechanics().reloadInterval(), 0.f, 1.f);

                if (progress < 1.f) {
                    int lightHeight = (int)(progress * awtImage.getHeight());
                    darkenAreaInplace(awtImage, 0, 0, awtImage.getWidth(), awtImage.getHeight() - lightHeight, 0.6f);
                    darkenAreaInplace(awtImage, 0, 0, awtImage.getWidth(), awtImage.getHeight(), 0.4f);
                } else if (sunManager.getSunCount() < type.getSunCost()) {
                    darkenAreaInplace(awtImage, 0, 0, awtImage.getWidth(), awtImage.getHeight(), 0.4f);
                }
            } else {
                System.out.println("Cannot render plant image in SunBank");
            }
            
            image.setColor(Color.BLACK);
            image.setFont(new Font(14));
            String costText = String.valueOf(type.getSunCost());
            if (costText.length() == 2){
                image.drawString(costText, WIDTH - (image.getFont().getSize() * costText.length()) - 8, image.getHeight() - 4);
            } else {
                image.drawString(costText, WIDTH - (image.getFont().getSize() * 2) - 16, image.getHeight() - 4);
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
            return reloadTimer.getDeltaSeconds() >= type.getMechanics().reloadInterval()
                && sunManager.getSunCount() >= type.getSunCost();
        }

        public void resetReload() {
            reloadTimer.reset();
        }

        public SeedType getType() {
            return type;
        }
    }
    
    private static int SUN_BANK_WIDTH = 78;

    private LevelWorld world;
    private ReanimManager reanimManager;
    private SunManager sunManager;
    private GreenfootImage bankImage;
    private Actor bankDisplay;
    
    private List<Seed> seeds;

    public SeedBank(LevelWorld world, SunManager sunManager, ReanimManager reanimManager, List<SeedType> seeds) {
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

    private void updateBankDisplay() {
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
    
    public SeedType getReadySeedAt(int x, int y) {
        for (int i = 0; i < seeds.size(); ++i) {
            var hitbox = new Rectangle(
                SUN_BANK_WIDTH + Seed.INDENT + (Seed.WIDTH + Seed.INDENT) * i,
                (bankImage.getHeight() - Seed.HEIGHT) / 2,
                Seed.WIDTH,
                Seed.HEIGHT
            );
            
            if (hitbox.contains(x, y)) {
                return seeds.get(i).isReady() ? seeds.get(i).getType() : null;
            }
        }
        
        return null;
    }
    
    public void resetTimerForSeed(SeedType type) {
        for (var seed : seeds) {
            if (seed.type == type) {
                seed.resetReload();
                break;
            }
        }
    }
}