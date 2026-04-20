import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import greenfoot.*;

public class ReanimManager {
    private ReanimParser reanimParser = new ReanimParser();
    private Map<String, Reanim> reanims = new HashMap<>();
    private Map<String, GreenfootImage> images = new HashMap<>();

    void loadReanims(String dir, String keyPrefix) {
        File reanimDirectory = new File(dir);
        if (reanimDirectory.exists() && reanimDirectory.isDirectory()) {
            File[] reanimFiles = reanimDirectory.listFiles((_dir, name) -> name.toLowerCase().endsWith(".reanim"));
            if (reanimFiles != null) {
                for (File file : reanimFiles) {
                    try {
                        String fileName = file.getName();
                        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
                        String key = keyPrefix + baseName.toUpperCase().replaceAll("[^A-Z0-9]", "_");

                        reanims.put(key, reanimParser.parse(file.getAbsolutePath()));
                    } catch (Exception e) {
                        System.err.println("Error loading reanim file: " + file.getName() + " - " + e.getMessage());
                    }
                }
            }
        }
    }

    void loadImages(String dir, String keyPrefix) {
        File imageDirectory = new File(dir);
        if (imageDirectory.exists() && imageDirectory.isDirectory()) {
            File[] imageFiles = imageDirectory.listFiles((_dir, name) ->
                name.toLowerCase().endsWith(".png") ||
                name.toLowerCase().endsWith(".jpg") ||
                name.toLowerCase().endsWith(".jpeg")
            );
            if (imageFiles != null) {
                for (File file : imageFiles) {
                    try {
                        String fileName = file.getName();
                        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
                        String key = keyPrefix + baseName.toUpperCase().replaceAll("[^A-Z0-9]", "_");

                        images.put(key, new GreenfootImage(file.getAbsolutePath()));
                    } catch (Exception e) {
                        System.err.println("Error loading image file: " + file.getName() + " - " + e.getMessage());
                    }
                }
            }
        }
    }

    public GreenfootImage generateSprite(String reanimKey, float frameIndex) {
        Reanim reanim = reanims.get(reanimKey);
        if (reanim == null || frameIndex < 0) {
            return new GreenfootImage(1, 1);
        }

        int canvasW = 300;
        int canvasH = 300;
        BufferedImage canvas = new BufferedImage(canvasW, canvasH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = canvas.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int originX = canvasW / 2;
        int originY = canvasH / 2;

        for (ReanimTrack track : reanim.tracks) {
            int frameA = (int) Math.floor(frameIndex);

            if (frameA < track.firstFrame || frameA > track.lastFrame) {
                continue;
            }
            
            int frameB = (frameA == track.lastFrame)
                ? track.firstFrame
                : frameA + 1;
            float t = frameIndex - frameA;
            
            //frameB = frameA;
            //t = 0;
            
            var f1 = track.frames.get(frameA);
            var f2 = (frameB < track.frames.size()) ? track.frames.get(frameB) : f1;

            if (f1 == null || f1.f == null || f1.f == -1) continue;

            GreenfootImage gfImg = images.get(f1.image);
            if (gfImg == null) continue;

            BufferedImage img = gfImg.getAwtImage();

            double x = lerp(f1.x != null ? f1.x : 0, f2.x != null ? f2.x : 0, t);
            double y = lerp(f1.y != null ? f1.y : 0, f2.y != null ? f2.y : 0, t);
    
            double sx = lerp(f1.sx != null ? f1.sx : 1.0, f2.sx != null ? f2.sx : 1.0, t);
            double sy = lerp(f1.sy != null ? f1.sy : 1.0, f2.sy != null ? f2.sy : 1.0, t);
    
            double kx = Math.toRadians(lerp(
                f1.kx != null ? f1.kx : 0,
                f2.kx != null ? f2.kx : 0,
                t
            ));
    
            double ky = Math.toRadians(lerp(
                f1.ky != null ? f1.ky : 0,
                f2.ky != null ? f2.ky : 0,
                t
            ));

            int w = img.getWidth();
            int h = img.getHeight();

            double a = Math.cos(kx) * sx;
            double b = Math.sin(kx) * sx;
            double c = -Math.sin(ky) * sy;
            double d = Math.cos(ky) * sy;

            double tx = originX + x;
            double ty = originY + y;

            AffineTransform at = new AffineTransform(
                a, b,
                c, d,
                tx, ty
            );

            g2d.drawImage(img, at, null);
        }

        g2d.dispose();

        return createGreenfootImageFromAwt(canvas);
    }

    public float getNextFrame(String key, String state, float currentFrame, float speed) {
        for (var track : reanims.get(key).tracks) {
            if (track.name.equals(state)) {
                currentFrame += speed;
    
                if (currentFrame < track.firstFrame || currentFrame > track.lastFrame) {
                    return track.firstFrame;
                }
    
                return currentFrame;
            }
        }
        return -1f;
    }
    
    public float getFirstFrame(String key, String state) {
        return getNextFrame(key, state, -1, 0);
    }
    
    public float getFPS(String key) {
        Reanim reanim = reanims.get(key);
        if (reanim == null) {
            return 0;
        }
        
        return reanim.fps;
    }

    private static String toUpperSnakeCase(String input) {
        return input.toUpperCase().replaceAll("[^A-Z0-9]", "_");
    }

    public static GreenfootImage createGreenfootImageFromAwt(BufferedImage awtImage) {
        int width = awtImage.getWidth(null);
        int height = awtImage.getHeight(null);

        GreenfootImage gfImage = new GreenfootImage(width, height);
        BufferedImage backingBuffer = gfImage.getAwtImage();
        Graphics g = backingBuffer.getGraphics();

        g.drawImage(awtImage, 0, 0, null);
        g.dispose();

        return gfImage;
    }
    
    private double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}