package org.se.mac.blorksandbox.analyzer.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modifier for {@link BufferedImage}'s color mode.
 * For image analysis it is recommended to use {@link BufferedImage#TYPE_BYTE_GRAY} as
 * constructor argument.
 */
public class ColormodeFunction implements Function<BufferedImage, BufferedImage> {

    private static final Logger logger = LoggerFactory.getLogger(ColormodeFunction.class);
    private final int targetColorMode;

    public ColormodeFunction(int targetColorMode) {
        this.targetColorMode = targetColorMode;
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        logger.trace("Transforming image to color mode '{}'...", this.targetColorMode);
        BufferedImage scaled = new BufferedImage(image.getWidth(), image.getHeight(),
                targetColorMode);
        Graphics2D graphics2D = scaled.createGraphics();
        graphics2D.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        graphics2D.dispose();
        return scaled;
    }

}
