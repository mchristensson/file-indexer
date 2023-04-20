package org.se.mac.blorksandbox.analyzer.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public class GenerateConvValueFunction extends ConvUtil implements Function<BufferedImage, BufferedImage>, SaveImageToDiskSupport {

    private static final Logger logger = LoggerFactory.getLogger(GenerateConvValueFunction.class);

    private final Iterable<int[]> masks;

    public GenerateConvValueFunction(Iterable<int[]> masks) {
        this.masks = masks;
    }

    /**
     * Checks that that mask is ok
     */
    private void validateMasks() {
        int maxWidth = 2;
        int maxHeight = 2;
        for (int[] mask : masks) {
            if (mask == null || mask.length != (maxHeight * maxWidth)) {
                throw new RuntimeException("Mask size is not supported");
            }
        }
    }

    @Override
    public BufferedImage apply(BufferedImage bufferedImage) {
        validateMasks();
        int h = bufferedImage.getHeight();
        int w = bufferedImage.getWidth();
        if (h > 128 || w > 128) {
            logger.error("Image is too large. Checksum will not be generated [w={}, h={}]", w, h);
            return null;
        }

        //Apply masks onto image and produce a probability array
        for (int[] mask : masks) {
            logger.debug("Processing mask... [values={}]", Arrays.toString(mask));

            float[] maskOutput = getStatsFloat(bufferedImage.getColorModel(), bufferedImage.getRaster(), mask);
            logger.debug("MaxPoolingOutput: [mask={}, max_pooling.output={}]", Arrays.toString(mask), Arrays.toString(maskOutput));

        }
        return bufferedImage;
    }

    private float[] getStatsFloat(ColorModel colorModel, WritableRaster rasterInput, int[] mask) {
        logger.debug("Input Image size: [w={}, h={}]", rasterInput.getWidth(), rasterInput.getHeight());
        float[] output = averageByMask((DataBufferByte) rasterInput.getDataBuffer(), rasterInput.getWidth(), rasterInput.getHeight(), mask);

        return maxPooling(output, (int) (rasterInput.getWidth() / Math.sqrt(mask.length)), (int) (rasterInput.getHeight() / Math.sqrt(mask.length)), 2, 1, 1);
    }


}

