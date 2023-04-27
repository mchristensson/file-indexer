package org.se.mac.blorksandbox.analyzer.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modifier for {@link BufferedImage}'s contrast.
 */
public class ContrastFunction implements Function<BufferedImage, BufferedImage> {
    private static final Logger logger = LoggerFactory.getLogger(ContrastFunction.class);

    @Override
    public BufferedImage apply(BufferedImage image) {
        logger.debug("Adjusting image...");
        int h0 = image.getHeight();
        int w0 = image.getWidth();

        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (bandPassFilter((int) data[i] & 0xff, 127, 0, 13.5f, false, 2));
        }
        BufferedImage scaled = new BufferedImage(w0, h0, image.getType());
        Graphics2D graphics2D = scaled.createGraphics();
        graphics2D.drawImage(image, 0, 0, w0, h0, null);
        graphics2D.dispose();
        return image;
    }


    /**
     * Applies a band-pass filter to the input value.
     *
     * @param v          value to filter
     * @param fc         Defines the fCenter of the filter
     * @param dfc        Defines the fLow and fHigh by its distance from arg <code>fc</code>
     * @param gain       Gain value
     * @param attenuates Whether operation is attenuating
     * @param ncolors    number of colours.
     * @return output pixel value
     */
    public static int bandPassFilter(int v, float fc, float dfc, float gain, boolean attenuates,
                                     int ncolors) {
        if (v > 0) {

            if (ncolors > -1) {
                v = posterize(v, ncolors);
            }

            float affection;
            if (v > fc + dfc || v < (fc - dfc)) { // Area D
                affection = attenuates ? 0f : 1f;
            } else {
                affection = attenuates ? 1f : 0f;
            }
            return (int) Math.min(255f * (gain * affection * v / 255f), 255f);
        }
        return v;
    }

    /**
     * Performs posterization.
     *
     * @param input       Input value
     * @param n Number of colours to allow in output spectrum
     * @return Output value after transformation
     */
    public static int posterize(int input, int n) {
        float d = input - input % (255f / n);
        return Math.round(d);
    }

    /**
     * Applies gain.
     *
     * @param input    Input value
     * @param gain gain factor
     * @return Output value after transformation
     */
    public static int gain(int input, float gain) {
        if (input > 0 && gain > 0) {
            return (int) Math.min(255f * (gain * input / 255f), 255f);
        }
        return input;
    }

}
