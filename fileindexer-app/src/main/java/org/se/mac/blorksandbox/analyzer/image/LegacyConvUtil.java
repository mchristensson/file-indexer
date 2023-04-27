package org.se.mac.blorksandbox.analyzer.image;

import java.awt.image.DataBufferByte;

/**
 * Utility class for convolution operations on {@link java.awt.image.BufferedImage}'s.
 */
@Deprecated
class LegacyConvUtil {

    /**
     * Convolution operation that builds an average-matrix from the input databuffer by sampling
     * pixel windows the same size as the input mask.
     *
     * @param dataBuffer Databuffer to use as input
     * @param w          Input image width
     * @param h          Input image height
     * @param mask       Convolution mask
     * @return Byte array representation
     * @see #averageMask(int[], int[])
     * @deprecated Use {@link ConvUtil#averageByMask(DataBufferByte, int, int, int[])} instead
     */
    public static byte[] filterRasterRaw(DataBufferByte dataBuffer, int w, int h, int[] mask) {
        int maskEdge = (int) Math.sqrt(mask.length);
        byte[] outputBytes = new byte[w * h / mask.length];
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
                byte maskingOutput = averageMask(pixels, mask);

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
     * Outputs the average from an array by sampling the values specified by the mask.
     *
     * @param input Input values to sample
     * @param mask  Mask for sampling of values
     * @return Byte value representing the average value.
     * @deprecated Use {@link ConvUtil#averageByMask(int[], int[])} instead
     */
    public static byte averageMask(int[] input, int[] mask) {
        final int threshold = 100;

        float sum = 0;
        for (int i = 0; i < mask.length; i++) {
            int a = input[i];
            int b = mask[i];
            sum += ((a == b) || (a > threshold && b > 0) || (a < threshold && b == 0)) ? 1 : -1;
        }
        //Negative values can be truncated to ZERO (ReLU)
        float avg = sum < 0 ? 0 : sum / mask.length;
        return (byte) (255 * avg);
    }
}
