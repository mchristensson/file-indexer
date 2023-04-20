package org.se.mac.blorksandbox.analyzer.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.function.Function;

public class GenerateConvValueFunction implements Function<BufferedImage, BufferedImage>, SaveImageToDiskSupport {

    private static final Logger logger = LoggerFactory.getLogger(GenerateConvValueFunction.class);

    private final Iterable<int[]> masks;

    public GenerateConvValueFunction(Iterable<int[]> masks) {
        this.masks = masks;
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
            byte[] maskOutput = getStats(bufferedImage.getColorModel(), bufferedImage.getRaster(), mask);
            logger.debug("MaxPoolingOutput: [mask={}, max_pooling.output={}]", Arrays.toString(mask),  Arrays.toString(maskOutput));

        }

        return bufferedImage;
    }

    /**
     * @param input        Input data to select from
     * @param x            horizontal coordinate in the 2D data
     * @param y            vertical coordinate of the 2D data
     * @param windowWidth  Width of window (mask)
     * @param windowHeight Height of window (mask)
     * @param maxX         Max horizontal index
     * @param maxY         Max vertical index
     * @return An output array with the selected data (assumed representing 2D data)
     * @see #getPixels(int[], int, int, int, int, int, int)
     */
    public static byte[] getPixelsByte(byte[] input, int x, int y, int windowWidth, int windowHeight, int maxX, int maxY) {
        if (input.length == 2) {
            return input;
        }
        byte[] output = new byte[windowWidth * windowHeight];
        int i = 0;
        for (int maskY = 0; maskY < windowHeight; maskY++) {
            for (int maskX = 0; maskX < windowWidth; maskX++) {
                if ((y + maskY) >= maxY) { //overflowY
                    output[i++] = 0; //set to zero, then...
                    continue; //continue in next cell
                }
                int index = (maxX * maskY) + (y * maxX + x) + maskX;
                if ((x + windowWidth) > maxX || index >= input.length) { //overflowX
                    output[i++] = 0; //set to zero, then...
                    break; //continue with next row
                }

                output[i++] = input[index]; // assign output value

            }
        }
        return output;
    }

    /**
     * Extracts a window of data from an array assumed representing 2D data
     *
     * @param input        Input data to select from
     * @param x            horizontal coordinate in the 2D data
     * @param y            vertical coordinate of the 2D data
     * @param windowWidth  Width of window (mask)
     * @param windowHeight Height of window (mask)
     * @param maxX         Max horizontal index
     * @param maxY         Max vertical index
     * @return An output array with the selected data (assumed representing 2D data)
     */
    public static int[] getPixels(int[] input, int x, int y, int windowWidth, int windowHeight, int maxX, int maxY) {
        int[] output = new int[windowWidth * windowHeight];
        int i = 0;
        for (int maskY = 0; maskY < windowHeight; maskY++) {
            for (int maskX = 0; maskX < windowWidth; maskX++) {
                if ((y + maskY) >= maxY) { //overflowY
                    output[i++] = 0; //set to zero, then...
                    continue; //continue in next cell
                }
                //output[i++] = (byte) ((maxX * maskY) + (y * maxX + x) + maskX); // assign output value
                output[i++] = input[((maxX * maskY) + (y * maxX + x) + maskX)]; // assign output value
                if ((x + windowWidth) > maxX) { //overflowX
                    output[i++] = 0; //set to zero, then...
                    break; //continue with next row
                }
            }
        }
        return output;
    }

    /**
     * @param input
     * @param w
     * @param h
     * @param maskLength
     * @param minX
     * @param minY
     * @return
     * @see #maxPoolingOnce(int[], int, int, int)
     */
    public static int[] maxPoolingUntil(int[] input, int w, int h, int maskLength, int minX, int minY) {
        int wi = w;
        int hi = h;
        byte divisor = 1;
        do {
            input = maxPoolingOnce(input, wi, hi, maskLength);
            divisor <<= 1;
            wi = w / divisor;
            hi = h / divisor;
        } while (hi > minY && wi > minX);
        return input;
    }

    /**
     * @param input
     * @param w
     * @param h
     * @param maskLength
     * @param minX
     * @param minY
     * @return
     * @see #maxPoolingOnce(int[], int, int, int)
     */
    public static byte[] maxPoolingUntilByte(byte[] input, int w, int h, int maskLength, int minX, int minY) {
        int wi = w;
        int hi = h;
        byte divisor = 1;
        do {
            input = maxPoolingOnceByte(input, wi, hi, maskLength);
            divisor <<= 1;
            wi = w / divisor;
            hi = h / divisor;
        } while (hi > minY && wi > minX);
        return input;
    }

    public static byte[] maxPoolingOnceByte(byte[] input, int w, int h, int maskLength) {
        int s = w * h / maskLength;
        if (s <= 0 || input.length < 2) {
            return input;
        }
        int bytesPointer = 0;
        byte[] output = new byte[s];
        for (int y = 0; y < h; y += maskLength) {
            for (int x = 0; x < w; x += maskLength) {
                byte[] data = getPixelsByte(input, x, y, maskLength, maskLength, w, h);
                //find max
                byte max = data[0];
                for (int i = 1; i < data.length; i++) {
                    if (data[i] > max) {
                        max = data[i];
                    }
                }
                output[bytesPointer++] = max; // max occurring value
            }
        }
        return output;
    }

    /**
     * Resolves the max-values by convolving a mask on a input array (assumed to represent 2D-data bounded by the arguments {@code w} and {@code h}.
     *
     * @param input      Input data
     * @param w          2D array width
     * @param h          2D array height
     * @param maskLength mask size
     * @return An array of all max-values for each masking iteration. If the input size or bounds is less than 2x2 the input is returned without processing.
     */
    public static int[] maxPoolingOnce(int[] input, int w, int h, int maskLength) {
        int s = w * h / maskLength;
        if (s <= 0 || input.length < 2) {
            return input;
        }
        int bytesPointer = 0;
        int[] output = new int[s];
        for (int y = 0; y < h; y += maskLength) {
            for (int x = 0; x < w; x += maskLength) {
                int[] data = getPixels(input, x, y, maskLength, maskLength, w, h);
                output[bytesPointer++] = (byte) (Arrays.stream(data).max().orElse(0)); // max occurring value
            }
        }
        return output;
    }


    public byte[] getStats(final ColorModel colorModel, final WritableRaster rasterInput, final int[] mask) {
        // Filtering -
        byte[] output = filterRasterRaw((DataBufferByte) rasterInput.getDataBuffer(), rasterInput.getWidth(), rasterInput.getHeight(), mask);
        //Pooling
        return maxPoolingUntilByte(output, rasterInput.getWidth(), rasterInput.getHeight(), 2, 1, 1);
    }

    public BufferedImage getRGB(final ColorModel colorModel, final WritableRaster rasterInput, final int[] mask) {
        logger.debug("Input Image size: [w={}, h={}]", rasterInput.getWidth(), +rasterInput.getHeight());
        int maskSize = (int) Math.sqrt(mask.length);

        // Filtering -
        /*
        int w1 = rasterInput.getWidth() / maskSize;
        int h1 = rasterInput.getHeight() / maskSize;
        WritableRaster rasterFiltered = colorModel.createCompatibleWritableRaster(w1, h1);
        */
        byte[] output = filterRasterRaw((DataBufferByte) rasterInput.getDataBuffer(), rasterInput.getWidth(), rasterInput.getHeight(), mask);
        //byte[] poolingOutput = poolUntil(output, rasterInput.getWidth(), rasterInput.getHeight());

        byte[] maxPoolingOutput = maxPoolingUntilByte(output, rasterInput.getWidth(), rasterInput.getHeight(), 2, 1, 1);
        logger.debug("MaxPoolingOutput: [input= {}, mask={}, max_pooling.output={}]", Arrays.toString(output), Arrays.toString(mask), Arrays.toString(maxPoolingOutput));


        return new BufferedImage(colorModel, rasterInput, false, null);

    }

    /*
    public static byte[] poolUntil(byte[] input, final int width, final int height) {
        double w = width;
        double h = height;

        int maskEdge = 2;
        byte[] data = input;
        while (h > maskEdge && w > maskEdge) {
            w = Math.ceil(w / 2);
            h = Math.ceil(h / 2);
            logger.debug("poolUntil... [w={}, h={}", w, h);
            int ww = (int) Math.ceil(w);
            int hh = (int) Math.ceil(h);
            data = maxPoolingOnce(data, ww, hh, maskEdge);
        }
        return data;
    }

     */


    public static byte[] maxPoolingOnce(byte[] input, int w, int h, int maskEdgeLength) {
        byte[] outputBytes = new byte[w * h / maskEdgeLength];
        int bytesPointer = 0;
        for (int row = 0; row < h; row += maskEdgeLength) {
            int rowOffset = row * w;
            for (int col = 0; col < w; col += maskEdgeLength) {
                int[] pixels = new int[maskEdgeLength * 2];
                int pixelsPointer = 0;
                for (int maskYOffset = 0; maskYOffset < maskEdgeLength; maskYOffset++) {
                    int maskOffsetY = (rowOffset + maskYOffset);
                    for (int maskXOffset = 0; maskXOffset < maskEdgeLength; maskXOffset++) {

                        var idx = maskOffsetY + col + maskXOffset;
                        var val = idx < input.length ? input[idx] : 0;
                        pixels[pixelsPointer++] = val;
                    }
                }
                int maskingOutput = (Arrays.stream(pixels).max().orElse(0));
                outputBytes[bytesPointer++] = (byte) maskingOutput;
            }

        }
        logger.debug("maxPoolingOnce... output={}", outputBytes);
        return outputBytes;
    }

    public static byte matchMask(int[] output, int[] mask) {
        final int threshold = 100;

        float sum = 0;
        for (int i = 0; i < mask.length; i++) {
            int a = output[i];
            int b = mask[i];
            sum += ((a == b) || (a > threshold && b > 0) || (a < threshold && b == 0)) ? 1 : -1;
        }
        //Negative values can be truncated to ZERO (ReLU)
        float avg = sum < 0 ? 0 : sum / mask.length;
        return (byte) (255 * avg);
    }

    /**
     * Checks that tha mask is ok
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

    private byte[] filterRasterRaw(DataBufferByte dataBuffer, int w, int h, int[] mask) {
        int maskEdge = (int) Math.sqrt(mask.length);
        byte[] outputBytes = new byte[w * h / mask.length];
        int bytesPointer = 0;
        for (int row = 0; row < h; row += maskEdge) {
            for (int col = 0; col < w; col += maskEdge) {
                //Extract pixels matching the mask
                int[] pixels = new int[mask.length];
                int pixelsPointer = 0;
                for (int maskYOffset = 0; maskYOffset < maskEdge; maskYOffset++) {
                    for (int maskXOffset = 0; maskXOffset < maskEdge; maskXOffset++) {
                        int p = dataBuffer.getElem(row * (h + maskYOffset) + (col + maskXOffset));
                        pixels[pixelsPointer++] = p;
                    }
                }
                byte maskingOutput = matchMask(pixels, mask);

                if (bytesPointer > outputBytes.length - 1){
                    //Lets truncate this data
                    return outputBytes;
                }
                outputBytes[bytesPointer++] = maskingOutput;

            }
        }
        return outputBytes;
    }

}
