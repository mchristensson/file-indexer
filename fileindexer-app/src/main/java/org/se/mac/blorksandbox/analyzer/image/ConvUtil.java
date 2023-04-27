package org.se.mac.blorksandbox.analyzer.image;

import java.awt.image.DataBufferByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for transforming and extracting data from {@link java.awt.image.BufferedImage}.
 */
public class ConvUtil {

    private static final Logger logger = LoggerFactory.getLogger(ConvUtil.class);

    /**
     * Filters a subset (window) of an image using a mask.
     * The matching rate is calculated as the average value match.
     * Negative average is being truncated to ZERO (ReLU)
     *
     * @param input ex { 10, 10, 30, 30}
     * @param mask  Acting function that defines the values to be included in the sum
     * @return Average matching value. Ex. { 1, 2, 3, 4} => 10 / 4 => 2.5f
     */
    public static float averageByMask(int[] input, int[] mask) {
        final int threshold = 100;
        float sum = 0;
        for (int i = 0; i < mask.length && i < input.length; i++) {
            int a = input[i];
            int b = mask[i];
            sum += ((a == b) || (a > threshold && b > 0) || (a < threshold && b == 0)) ? 1 : -1;
        }
        //
        float avg = sum < 0 ? 0 : sum / mask.length;
        //logger.debug("input {} => AVG: {}", Arrays.toString(input), avg);
        return avg;
    }

    /**
     * Extracts a subset (window) of a byte-array (treated as a 2d-array.
     *
     * @param dataBuffer Data input source to extract from
     * @param w          2d-array horizontal bound
     * @param h          2d-array vertical bound
     * @param mask       mask definition to extract and filter data through
     * @return average
     * @see #averageByMask(int[], int[]) for more details on computation of the extracted window
     * data.
     */
    public static float[] averageByMask(DataBufferByte dataBuffer, int w, int h, int[] mask) {
        assert mask != null && (w * h) > 0 && mask.length > 1 && mask.length <= (w * h);
        int maskEdge = (int) Math.sqrt(mask.length);
        float[] outputBytes = new float[w * h / mask.length];
        int bytesPointer = 0;
        for (int row = 0; row < h; row += maskEdge) {
            for (int col = 0; col < w; col += maskEdge) {
                //Extract pixels matching the mask
                int[] pixels = new int[mask.length];
                int pixelsPointer = 0;
                for (int maskyoffset = 0; maskyoffset < maskEdge; maskyoffset++) {
                    for (int maskxoffset = 0; maskxoffset < maskEdge; maskxoffset++) {
                        int p = dataBuffer.getElem(row * (h + maskyoffset) + (col + maskxoffset));
                        pixels[pixelsPointer++] = p;
                    }
                }
                float maskingOutput = averageByMask(pixels, mask);

                if (bytesPointer > outputBytes.length - 1) {
                    //Lets truncate this data
                    return outputBytes;
                }
                outputBytes[bytesPointer++] = maskingOutput;
            }
        }
        return outputBytes;
    }

    /**
     * Extracts a window of data from an array assumed representing 2D data.
     *
     * @param input        Input data to extract from
     * @param x            horizontal coordinate in the 2D data
     * @param y            vertical coordinate of the 2D data
     * @param windowWidth  Width of window (mask)
     * @param windowHeight Height of window (mask)
     * @param maxX         Max horizontal index
     * @param maxY         Max vertical index
     * @return An output array with the selected data (assumed representing 2D data)
     */
    public static float[] getPixels(float[] input, int x, int y, int windowWidth,
                                    int windowHeight, int maxX, int maxY) {
        if (input.length == 2) {
            return input;
        }
        float[] output = new float[windowWidth * windowHeight];
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
     * Performs maxPooling.
     *
     * @param input      Input data
     * @param w          2D array width
     * @param h          2D array height
     * @param maskLength mask size
     * @return An array of all max-values for each masking iteration. If the input size or bounds
     * is less than 2x2 the input is returned without processing.
     */
    public static float[] maxPooling(float[] input, int w, int h, int maskLength) {
        int s = w * h / maskLength;
        if (s <= 0 || input.length < 2) {
            return input;
        }
        int bytesPointer = 0;
        float[] output = new float[s];
        for (int y = 0; y < h; y += maskLength) {
            for (int x = 0; x < w; x += maskLength) {
                float[] data = getPixels(input, x, y, maskLength, maskLength, w, h);
                //find max
                float max = data[0];
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
     * Performs maxPooling until a minimum 1D-vector remains.
     *
     * @param input      Input data
     * @param w          2D array width
     * @param h          2D array height
     * @param maskLength mask size
     * @param minX       Horizontal lower threshold value (non-inclusive) to continue process
     * @param minY       Vertical lower threshold value (non-inclusive) to continue process
     * @return An array of all max-values for each masking iteration. If the input size or bounds
     * is less than 2x2 the input is returned without processing.
     * @see #maxPooling(float[], int, int, int, int, int)
     */
    protected static float[] maxPooling(float[] input, int w, int h, int maskLength, int minX,
                                        int minY) {
        int wi = w;
        int hi = h;
        byte divisor = 1;
        do {
            input = maxPooling(input, wi, hi, maskLength);
            divisor <<= 1;
            wi = w / divisor;
            hi = h / divisor;
        } while (hi > minY && wi > minX);
        return input;
    }
}
