package org.se.mac.blorksandbox.analyzer.image;

import com.drew.lang.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.math.BigInteger;
import java.util.function.Function;

public class GenerateChecksumFunction implements Function<BufferedImage, String> {

    private static final Logger logger = LoggerFactory.getLogger(GenerateChecksumFunction.class);
    final int threshold;

    public GenerateChecksumFunction(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public String apply(BufferedImage bufferedImage) {
        int h = bufferedImage.getHeight();
        int w = bufferedImage.getWidth();
        if (h > 128 || w > 128) {
            logger.error("Image is too large. Checksum will not be generated [w={}, h={}]", w, h);
            return null;
        }
        int[] rgbArray = new int[w * h];
        logger.debug("Generating checksum... [w={}, h={}, wxh={}]", w, h, rgbArray.length);
        int avg = getRGB(bufferedImage.getColorModel(), bufferedImage.getRaster(), w, h, rgbArray);
        return createChecksum(rgbArray, avg);
    }

    public String createChecksum(@NotNull final int[] vals, final int threshold) {
        byte[] output = new byte[(vals.length - (vals.length % 8)) / 8];
        for (int b = 0; b < output.length; b++) {
            byte bNext = 0;
            int offset = b * 8;
            for (int j = offset; j < (8 + offset) && (j < vals.length); j++) {
                bNext <<= 1; // shift-left
                if (vals[j] > threshold) {
                    bNext |= 1; // bitwise or
                }
            }
            output[b] = bNext;
        }
        BigInteger val =  new BigInteger(1, output);
        logger.debug("Generated unsigned long [long={}]", val);
        return String.format("%0" + (output.length << 1) + "x",val);
    }

    public int getRGB(ColorModel colorModel, WritableRaster raster, int w, int h, final int[] rgbArray) {
        int off;
        Object data;
        int nbands = raster.getNumBands();
        int dataType = raster.getDataBuffer().getDataType();
        data = switch (dataType) {
            case DataBuffer.TYPE_BYTE -> new byte[nbands];
            case DataBuffer.TYPE_USHORT -> new short[nbands];
            case DataBuffer.TYPE_INT -> new int[nbands];
            case DataBuffer.TYPE_FLOAT -> new float[nbands];
            case DataBuffer.TYPE_DOUBLE -> new double[nbands];
            default -> throw new IllegalArgumentException("Unknown data buffer type: " +
                    dataType);
        };

        int sum = 0;
        for (int y = 0; y < h; y++) {
            off = y * w;
            for (int x = 0; x < w; x++) {
                int val =
                        colorModel.getRGB(raster.getDataElements(x,
                                y,
                                data)) & 0xff;
                rgbArray[off++] = val;
                sum += val;
            }
        }
        return sum / (w * h);

    }
}
