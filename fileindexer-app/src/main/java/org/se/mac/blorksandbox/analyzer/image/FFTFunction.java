package org.se.mac.blorksandbox.analyzer.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;
import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.transform.FastFourierTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FFTFunction implements Function<BufferedImage, BufferedImage> {
    private static final Logger logger = LoggerFactory.getLogger(FFTFunction.class);
    private final boolean inverse;

    public FFTFunction(boolean inverse) {
        this.inverse = inverse;
    }
    @Override
    public BufferedImage apply(BufferedImage image) {
        logger.debug("Adjusting image...");
        int h0 = image.getHeight();
        int w0 = image.getWidth();
        if (h0 != w0) {
            throw new RuntimeException("Image needs to have square proportions. Found: w="+w0 + ", h=" + h0);
        }
        double[][] pixels = new double[w0][h0];
        for (int i = 0; i < w0; i++) {
            for (int j = 0; j < h0; j++) {
                int rgb = image.getRGB(i, j);
                int gray = (rgb >> 16) & 0xFF;  // Extarct grayscale part
                pixels[i][j] = gray;
            }
        }

        Complex[][] output = transformFreqDomain(pixels, h0, w0);
        BufferedImage outputImage = new BufferedImage(w0, h0, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < w0; i++) {
            for (int j = 0; j < h0; j++) {
                if (inverse) {
                    int magnitude = (int) Math.round(output[i][j].getReal());
                    //magnitude = Math.max(magnitude, 0);
                    //int color = new Color(magnitude, magnitude, magnitude).getRGB();
                    int color = (magnitude << 16) | (magnitude << 8) | magnitude;
                    outputImage.setRGB(i, j, color);
                } else {
                    int magnitude = (int) output[i][j].abs();
                    int color = (magnitude << 16) | (magnitude << 8) | magnitude;
                    outputImage.setRGB(i, j, color);
                }

            }
        }


        return outputImage;
    }

    /**
     *
     * @param pixels
     * @param height
     * @param width
     * @return
     */
    private Complex[][] transformFreqDomain(double[][] pixels, int height, int width) {
        FastFourierTransformer transformer = new FastFourierTransformer();
        Complex[][] data = new Complex[width][height];
        for (int i = 0; i < width; i++) {
            data[i] = inverse? transformer.inversetransform(pixels[i]) : transformer.transform(pixels[i]);
        }
        for (int j = 0; j < height; j++) {
            Complex[] column = new Complex[width];
            for (int i = 0; i < width; i++) {
                column[i] = data[i][j];
            }
            column = inverse? transformer.inversetransform(column) : transformer.transform(column);
            for (int i = 0; i < width; i++) {
                data[i][j] = column[i];
            }
        }
        return data;
    }

}
