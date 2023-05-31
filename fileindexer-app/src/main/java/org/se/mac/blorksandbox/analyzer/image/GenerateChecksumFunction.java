package org.se.mac.blorksandbox.analyzer.image;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.math.BigInteger;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modifier tha produces a bhash checksum from an image.
 */
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
        logger.trace("Generating checksum... [w={}, h={}, wxh={}]", w, h, rgbArray.length);
        int avg = getPixels(bufferedImage.getColorModel(), bufferedImage.getRaster(), w, h, rgbArray);
        return createChecksum(rgbArray, avg);
    }

    /**
     * Finds a checksum representation of the input values.
     *
     * @param vals      Input values
     * @param threshold Threshold vale
     * @return Checksum representation
     */
    public String createChecksum(final int[] vals, final int threshold) {
        byte[] output = new byte[(vals.length - (vals.length % 8)) / 8];
        for (int b = 0; b < output.length; b++) {
            byte nextByte = 0;
            int offset = b * 8;
            for (int j = offset; j < (8 + offset) && (j < vals.length); j++) {
                nextByte <<= 1; // shift-left
                if (vals[j] > threshold) {
                    nextByte |= 1; // bitwise or
                }
            }
            output[b] = nextByte;
        }
        BigInteger val = new BigInteger(1, output);
        logger.debug("Generated unsigned long [long={}]", val);
        return String.format("%0" + (output.length << 1) + "x", val);
    }

    /**
     * Extracts pixel average value from image and builds output array.
     *
     * @param colorModel Color model that applies
     * @param raster     Image pixel data
     * @param w          Image width
     * @param h          Image height
     * @param rgbArray   output array
     * @return Calculated pixel average value
     */
    public int getPixels(ColorModel colorModel, WritableRaster raster, int w, int h,
                         final int[] rgbArray) {
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
            default -> throw new IllegalArgumentException("Unknown data buffer type: " + dataType);
        };

        int sum = 0;
        for (int y = 0; y < h; y++) {
            off = y * w;
            for (int x = 0; x < w; x++) {
                int val = colorModel.getRGB(raster.getDataElements(x, y, data)) & 0xff;
                rgbArray[off++] = val;
                sum += val;
            }
        }
        return sum / (w * h);

    }
}
