package org.se.mac.blorksandbox.analyzer.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modifier for {@link BufferedImage}'s size.
 */
public class ScaleFunction implements Function<BufferedImage, BufferedImage> {

    private static final Logger logger = LoggerFactory.getLogger(ScaleFunction.class);
    private final int targetColorMode;
    private final boolean crop;
    private int maxPixels;

    /**
     * Default constructor.
     *
     * @param maxPixels       The maximum/expected edge length of the width/height
     * @param crop Whether to crop (true) or maintain proportions.
     * @param targetColorMode Target color mode
     */
    public ScaleFunction(int maxPixels, boolean crop, int targetColorMode) {
        this.maxPixels = maxPixels;
        this.targetColorMode = targetColorMode;
        this.crop = crop;
    }

    @Override
    public BufferedImage apply(BufferedImage image) {
        int h0 = image.getHeight();
        int w0 = image.getWidth();
        int h1 = 1;
        int w1 = 1;
        logger.debug("Original Size [width={}, height={}, type={}, targetColorMode={}]", w0, h0,
                image.getType(), targetColorMode);

        if (h0 >= w0) {
            //Limit Height primarily
            h1 = maxPixels;
            w1 = crop? maxPixels : h0 * maxPixels / w0;
        } else {
            // Limit width
            h1 = crop? maxPixels : w0 * maxPixels / h0;
            w1 = maxPixels;
        }
        logger.trace("Target image size [width={}, height={}]", w1, h1);



        BufferedImage scaled = new BufferedImage(w1, h1, targetColorMode != BufferedImage.TYPE_CUSTOM ?
                targetColorMode : image.getType());
        Graphics2D graphics2D = scaled.createGraphics();
        if (crop) {
            BufferedImage croppedImage = image.getSubimage(0,0, Math.min(h0,w0), Math.min(h0,w0));
            graphics2D.drawImage(croppedImage, 0, 0, w1, h1, null);
        } else {
            graphics2D.drawImage(image, 0, 0, w1, h1, null);
        }
        //graphics2D.drawImage(image, 0, 0, w1, h1, null);
        graphics2D.dispose();

        return scaled;
    }

}
