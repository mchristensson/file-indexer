package org.se.mac.blorksandbox.analyzer.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ContrastFunction implements Function<BufferedImage, BufferedImage> {
    private static final Logger logger = LoggerFactory.getLogger(ContrastFunction.class);

    @Override
    public BufferedImage apply(BufferedImage image) {
        int h0 = image.getHeight();
        int w0 = image.getWidth();

        //NOTE: This supports only black and white image!!!


//        DataBufferInt buf = (DataBufferInt) image.getRaster().getDataBuffer();

        //Extract image data

        //TODO: Contrast function not really in-place
        int sampleSize = 3;
        for (int yi = 0; yi < h0-2; yi++) {
            for (int xi = 0; xi < w0-2; xi++) {
                int[] arr = null;
                int[] pixelRow = image.getRaster().getPixels(xi, yi, sampleSize, sampleSize, arr);
                int avg = (int) IntStream.of(pixelRow).average().orElse(0);
                int f = (int)  (128 * Math.log10(avg + 1) / Math.log10(Integer.MAX_VALUE + 1));
                int ff = (int) Math.floor(256 *avg / (255 + 1));
               // int f = (int)  avg;
                Arrays.fill(pixelRow,  f);
                image.getRaster().setPixels(xi, yi, sampleSize, sampleSize, pixelRow);
            }
        }


        BufferedImage scaled = new BufferedImage(w0, h0, image.getType());
        Graphics2D graphics2D = scaled.createGraphics();
        graphics2D.drawImage(image, 0, 0, w0, h0, null);
        graphics2D.dispose();

        return image;
    }

}
