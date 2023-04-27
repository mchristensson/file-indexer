package org.se.mac.blorksandbox.analyzer.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modifier for {@link BufferedImage}'s color mode.
 */
public class ColormodeFunction implements Function<BufferedImage, BufferedImage> {

    private static final Logger logger = LoggerFactory.getLogger(ColormodeFunction.class);

    @Override
    public BufferedImage apply(BufferedImage image) {
        BufferedImage scaled = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics2D = scaled.createGraphics();
        graphics2D.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        graphics2D.dispose();

        return scaled;
    }

}
