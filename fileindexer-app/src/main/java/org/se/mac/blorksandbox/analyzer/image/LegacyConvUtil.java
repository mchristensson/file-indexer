package org.se.mac.blorksandbox.analyzer.image;

import java.awt.image.DataBufferByte;

class LegacyConvUtil {

    /**
     * @param dataBuffer
     * @param w
     * @param h
     * @param mask
     * @return
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
                for (int maskYOffset = 0; maskYOffset < maskEdge; maskYOffset++) {
                    for (int maskXOffset = 0; maskXOffset < maskEdge; maskXOffset++) {
                        int p = dataBuffer.getElem(row * (h + maskYOffset) + (col + maskXOffset));
                        pixels[pixelsPointer++] = p;
                    }
                }
                byte maskingOutput = matchMask(pixels, mask);

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
     * @param output
     * @param mask
     * @return
     * @deprecated Use {@link ConvUtil#averageByMask(int[], int[])} instead
     */
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
}
