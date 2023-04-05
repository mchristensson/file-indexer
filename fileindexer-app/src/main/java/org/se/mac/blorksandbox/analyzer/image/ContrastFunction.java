package org.se.mac.blorksandbox.analyzer.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.function.Function;

public class ContrastFunction implements Function<BufferedImage, BufferedImage> {
    private static final Logger logger = LoggerFactory.getLogger(ContrastFunction.class);

    @Override
    public BufferedImage apply(BufferedImage image) {
        logger.debug("Adjusting image...");
        int h0 = image.getHeight();
        int w0 = image.getWidth();

        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (bandPassFilter((int) data[i] & 0xff, 127,0, 13.5f, false, 2));
        }
        BufferedImage scaled = new BufferedImage(w0, h0, image.getType());
        Graphics2D graphics2D = scaled.createGraphics();
        graphics2D.drawImage(image, 0, 0, w0, h0, null);
        graphics2D.dispose();
        return image;
    }


    /**
     * @param v          value to filter
     * @param fc         Defines the fCenter of the filter
     * @param dfc        Defines the fLow and fHigh by its distance from arg <code>fc</code>
     * @param gain
     * @param attenuates
     * @param nColors
     * @return
     */
    public static int bandPassFilter(int v, float fc, float dfc, float gain, boolean attenuates, int nColors) {
        if (v > 0) {

            if (nColors > -1) {
                v = posterize(v, nColors);
            }

            float affection;
            if (v > fc+dfc || v < (fc-dfc)) { // Area D
                affection = attenuates? 0f : 1f;
            } else {
                affection = attenuates? 1f : 0f;
            }
            return (int) Math.min(
                    255f * (gain * affection * v / 255f),
                    255f);
        }
        return v;
    }

    public static int posterize(int v, int nColors) {
        float d = v - v % (255f / nColors);
        return Math.round(d);
    }

    public static int  gain(int v, float gain) {
        if (v > 0 && gain > 0) {
            return (int) Math.min(255f * (gain * v / 255f), 255f);
        }
        return v;
    }

}
