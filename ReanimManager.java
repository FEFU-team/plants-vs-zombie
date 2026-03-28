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

    public GreenfootImage generateSprite(String reanimKey, int frameIndex) {
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
            var f = track.frames.get(frameIndex);

            if (f == null || f.f == null || f.f == -1) continue;

            GreenfootImage gfImg = images.get(f.image);
            if (gfImg == null) continue;

            BufferedImage img = gfImg.getAwtImage();

            double x = f.x != null ? f.x : 0;
            double y = f.y != null ? f.y : 0;

            double sx = f.sx != null ? f.sx : 1.0;
            double sy = f.sy != null ? f.sy : 1.0;

            double kx = f.kx != null ? Math.toRadians(f.kx) : 0;
            double ky = f.ky != null ? Math.toRadians(f.ky) : 0;

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

    public int getNextFrame(String key, String state, int currentFrame) {
        for (var track : reanims.get(key).tracks) {
            if (track.name.equals(state)) {
                ++currentFrame;
                if (currentFrame < track.firstFrame || currentFrame > track.lastFrame) {
                    return track.firstFrame;
                } else {
                    return currentFrame;
                }
            }
        }
        return -1;
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
}