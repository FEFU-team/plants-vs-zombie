import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.function.*;
import java.util.*;

public class ReanimParser {
    public Reanim parse(String fileName) throws Exception {
        String content = Files.readString(Path.of(fileName));
        String wrapped = "<root>" + content + "</root>";

        InputStream is = new ByteArrayInputStream(wrapped.getBytes(StandardCharsets.UTF_8));

        Document doc = DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(is);
        doc.getDocumentElement().normalize();

        var reanim = new Reanim();

        reanim.fps = Float.parseFloat(doc.getElementsByTagName("fps").item(0).getTextContent().split("\\.")[0]);

        NodeList tracks = doc.getElementsByTagName("track");

        for (int i = 0; i < tracks.getLength(); i++) {
            Element track = (Element) tracks.item(i);

            String trackName = getOptional(track, "name", "");
            NodeList frames = track.getElementsByTagName("t");

            var trackObj = new ReanimTrack();
            trackObj.name = trackName;

            var previousFrame = new ReanimFrame();
            previousFrame.f = 0;
            for (int j = 0; j < frames.getLength(); j++) {
                Element frame = (Element) frames.item(j);

                var frameObj = new ReanimFrame();
                frameObj.x = getOptional(frame, "x", Float::valueOf, previousFrame.x);
                frameObj.y = getOptional(frame, "y", Float::valueOf, previousFrame.y);
                frameObj.sx = getOptional(frame, "sx", Float::valueOf, previousFrame.sx);
                frameObj.sy = getOptional(frame, "sy", Float::valueOf, previousFrame.sy);
                frameObj.kx = getOptional(frame, "kx", Float::valueOf, previousFrame.kx);
                frameObj.ky = getOptional(frame, "ky", Float::valueOf, previousFrame.ky);
                frameObj.f = getOptional(frame, "f", Integer::valueOf, previousFrame.f);
                frameObj.image = getOptional(frame, "i", previousFrame.image);

                if (frameObj.f == 0) {
                    if (trackObj.firstFrame == -1) {
                        trackObj.firstFrame = j;
                    }
                    trackObj.lastFrame = j;
                }

                trackObj.frames.add(frameObj);
                previousFrame = frameObj;
            }

            reanim.tracks.add(trackObj);
        }

        return reanim;
    }

    private static String getOptional(Element parent, String tag) {
        return getOptional(parent, tag, Function.identity());
    }

    private static String getOptional(Element parent, String tag, String defaultValue) {
        return getOptional(parent, tag, Function.identity(), defaultValue);
    }

    private static <T> T getOptional(Element parent, String tag, Function<String, T> parser) {
        return getOptional(parent, tag, parser, null);
    }

    private static <T> T getOptional(Element parent, String tag, Function<String, T> parser, T defaultValue) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list.getLength() == 0) return defaultValue;
        return parser.apply(list.item(0).getTextContent());
    }
}
