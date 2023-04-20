package org.se.mac.blorksandbox.analyzer.image;

import org.assertj.core.util.FloatComparator;
import org.junit.jupiter.api.Test;

import java.awt.image.DataBufferByte;

import static org.junit.jupiter.api.Assertions.*;
import static org.se.mac.blorksandbox.analyzer.image.GenerateConvValueFunction.*;

class GenerateConvValueFunctionTest {

    @Test
    void matchMaskFloat_whenEmptyInputAndMask_expectZero() {
        int[] mask = new int[0];
        int[] input = new int[0];
        float output = averageByMask(input, mask);
        assertTrue(Float.isNaN(output));
    }

    @Test
    void matchMaskFloat_whenZeroInputAndEmptyMask_expectUndetermined() {
        int[] mask = new int[]{0};
        int[] input = new int[0];
        float output = averageByMask(input, mask);
        assertEquals(0, output);
    }

    @Test
    void matchMaskFloat_whenZeroMaskAndEmptyInput_expectNan() {
        int[] mask = new int[0];
        int[] input = new int[]{0};
        float output = averageByMask(input, mask);
        assertTrue(Float.isNaN(output));
    }

    @Test
    void matchMaskFloat_whenZero_expectMatch() {
        int[] mask = new int[]{0};
        int[] input = new int[]{0};
        float output = averageByMask(input, mask);
        assertEquals(1, output);
    }

    @Test
    void matchMaskFloat_whenNonZeroValueBelowThresholdZeroMask_expectMatch() {
        int[] mask = new int[]{0};
        int[] input = new int[]{99};
        float output = averageByMask(input, mask);
        assertEquals(1, output);
    }

    @Test
    void matchMaskFloat_whenNonZeroValueAboveThresholdZeroMask_expectUndetermined() {
        int[] mask = new int[]{0};
        int[] input = new int[]{101};
        float output = averageByMask(input, mask);
        assertEquals(0, output);
    }


    @Test
    void matchMaskFloat_whenNonZeroValueBelowThreshold_expectUndetermined() {
        int[] mask = new int[]{1};
        int[] input = new int[]{99};
        float output = averageByMask(input, mask);
        assertEquals(0, output);
    }

    @Test
    void matchMaskFloat_whenNonZeroValueAboveThreshold_expectMatch() {
        int[] mask = new int[]{1};
        int[] input = new int[]{101};
        float output = averageByMask(input, mask);
        assertEquals(1, output);
    }

    @Test
    void matchMaskFloat_whenNonZeroValue_expectNonMatchValue() {
        int[] mask = new int[]{1};
        int[] input = new int[]{128};
        float output = averageByMask(input, mask);
        assertEquals(1, output);
    }

    @Test
    void matchMaskFloat_when2x1SingleMatch_expectUndetermined() {
        int[] mask = new int[]{1, 0};
        int[] input = new int[]{128, 128};
        float output = averageByMask(input, mask);
        assertEquals(0, output);
    }

    @Test
    void matchMaskFloat_when2x1DoubleMatch_expectMatch() {
        int[] mask = new int[]{1, 1};
        int[] input = new int[]{128, 128};
        float output = averageByMask(input, mask);
        assertEquals(1, output);
    }

    @Test
    void matchMaskFloat_when3x1ThreeMatches_expectFullMatch() {
        int[] mask = new int[]{1, 1, 1}; // MATCH, MATCH, MATCH
        int[] input = new int[]{128, 128, 128};
        float output = averageByMask(input, mask);
        FloatComparator comparator = new FloatComparator(0.01f);
        assertEquals(0, comparator.compareNonNull(1.0f, output), "Values expected to be close enough");
    }

    @Test
    void matchMaskFloat_when3x1TwoMatch_expectPartialMatch() {
        int[] mask = new int[]{1, 1, 0}; // MATCH, MATCH, NO_MATCH
        int[] input = new int[]{128, 128, 128};
        float output = averageByMask(input, mask);
        FloatComparator comparator = new FloatComparator(0.01f);
        assertEquals(0, comparator.compareNonNull(0.33f, output), "Values expected to be close enough");
    }

    @Test
    void matchMaskFloat_when4x1SingleMatch_expectPartialMatch() {
        int[] mask = new int[]{1, 1, 0, 1}; // MATCH, MATCH, NO_MATCH, MATCH
        int[] input = new int[]{128, 128, 128, 128};
        float output = averageByMask(input, mask);
        assertEquals(0.5f, output);
    }

    @Test
    void matchMaskFloat_when3x1SingleMatchd_expectNomatch() {
        int[] mask = new int[]{1, 0, 0}; // MATCH, NO_MATCH, NO_MATCH
        int[] input = new int[]{128, 128, 128};
        float output = averageByMask(input, mask);
        assertEquals(0, output);
    }

    @Test
    void matchMaskFloat_when3x1DoubleMismatch_expectNomatch() {
        int[] mask = new int[]{1, 1, 0}; // Two Negative matches will result in ZERO
        int[] input = new int[]{99, 99, 99};
        float output = averageByMask(input, mask);
        assertEquals(0, output);
    }

    @Test
    void matchMaskFloat_when4x1DoubleMismatch_expectNomatch() {
        int[] mask = new int[]{0, 0, 0, 1}; // MATCH, MATCH, MATCH, NO_MATCH
        int[] input = new int[]{99, 99, 99, 99};
        float output = averageByMask(input, mask);
        FloatComparator comparator = new FloatComparator(0.01f);
        assertEquals(0, comparator.compareNonNull(0.5f, output), "Values expected to be close enough");
    }

    @Test
    void matchMaskFloat_when3x1DoubleMatch_expectPartialMatch() {
        int[] mask = new int[]{1, 1, 1}; // Three Negative matches will result in ZERO
        int[] input = new int[]{99, 99, 99};
        float output = averageByMask(input, mask);
        assertEquals(0, output);
    }

    @Test
    void filterRasterRaw_whenNullDatabuffer_expectAssertionError() {
        DataBufferByte databuffer = null;
        int[] mask = new int[0];
        int w = 0;
        int h = 0;
        try {
            ConvUtil.averageByMask(databuffer, w, h, mask);
            fail("Exception expected");
        } catch (Throwable e) {
            e.printStackTrace();
            assertEquals(AssertionError.class, e.getClass());
        }
    }

    @Test
    void filterRasterRaw_whenMinimumDataBufferEmptyMask_expectAssertionError() {
        byte[] data = new byte[]{0};
        DataBufferByte databuffer = new DataBufferByte(data, 1);
        int[] mask = new int[0];
        int w = 0;
        int h = 0;
        try {
            ConvUtil.averageByMask(databuffer, w, h, mask);
            fail("Exception expected");
        } catch (Throwable e) {
            assertEquals(AssertionError.class, e.getClass());
        }
    }

    @Test
    void filterRasterRaw_whenMinimumDataBuffer_expectEmptyOutput() {
        byte[] data = new byte[]{0};
        DataBufferByte databuffer = new DataBufferByte(data, 1);
        int[] mask = new int[]{0, 0};
        int w = 0;
        int h = 0;
        try {
            ConvUtil.averageByMask(databuffer, w, h, mask);
            fail("Exception expected");
        } catch (Throwable e) {
            assertEquals(AssertionError.class, e.getClass());
        }

    }

    @Test
    void filterRasterRaw_whenSingleValueMask_expectException() {
        byte[] data = new byte[]{0};
        DataBufferByte databuffer = new DataBufferByte(data, 1);
        int[] mask = new int[]{0};
        int w = 0;
        int h = 0;
        try {
            ConvUtil.averageByMask(databuffer, w, h, mask);
            fail("Exception expected");
        } catch (Throwable e) {
            assertEquals(AssertionError.class, e.getClass());
        }
    }

    @Test
    void filterRasterRaw_when3x1DataZeroWZeroH_expectException() {
        byte[] data = new byte[]{0, 0, 1};
        DataBufferByte databuffer = new DataBufferByte(data, 1);
        int[] mask = new int[]{0};
        int w = 0;
        int h = 0;
        try {
            ConvUtil.averageByMask(databuffer, w, h, mask);
            fail("Exception expected");
        } catch (Throwable e) {
            assertEquals(AssertionError.class, e.getClass());
        }
    }

    @Test
    void filterRasterRaw_when3x1DataMaskTooLarge_expectMatchingArray() {
        byte[] data = new byte[]{0, 127, 1}; //GRAY, WHITE, BLACK
        DataBufferByte databuffer = new DataBufferByte(data, 1);
        int[] mask = new int[]{0, 0};
        int w = 3;
        int h = 1;
        float[] output = ConvUtil.averageByMask(databuffer, w, h, mask);
        assertFloatArrayEquals(new float[]{1}, output);
    }


    @Test
    void filterRasterRaw_when3x1MinimalMask_expectException() {
        byte[] data = new byte[]{0, 127, 1}; //GRAY, WHITE, BLACK
        DataBufferByte databuffer = new DataBufferByte(data, 1);
        int[] mask = new int[]{0};
        int w = 3;
        int h = 1;
        try {
            ConvUtil.averageByMask(databuffer, w, h, mask);
            fail("Exception expected");
        } catch (Throwable e) {
            assertEquals(AssertionError.class, e.getClass());
        }
    }

    @Test
    void filterRasterRaw_when3x2Data_expectFullMatchingArray() {
        byte[] data = new byte[]{0, 0, 0, 0, 0, 0};
        DataBufferByte databuffer = new DataBufferByte(data, 1);
        int[] mask = new int[]{0, 0};
        int w = 3;
        int h = 2;
        float[] output = ConvUtil.averageByMask(databuffer, w, h, mask);
        assertFloatArrayEquals(new float[]{1, 1, 1}, output);
    }

    @Test
    void filterRasterRaw_when3x2DiagData_expectPartialMatchingArray() {
        byte[] data = new byte[]{127, 127, 0, 0, -127, 0};

        DataBufferByte databuffer = new DataBufferByte(data, 1);
        int[] mask = new int[]{1, 0};
        int w = 3;
        int h = 2;
        float[] output = ConvUtil.averageByMask(databuffer, w, h, mask);
        assertFloatArrayEquals(new float[]{1, 1, 0}, output);
    }

    @Test
    void filterRasterRaw_when4x3DiagData_expectPartialMatchingArray() {
        byte[] data = new byte[]{
                127, 127, 0, 10,
                30, -127, 90, 10,
                0, 127, 127, 0
        };

        DataBufferByte databuffer = new DataBufferByte(data, 1);
        int[] mask = new int[]{1, 0};
        int w = 4;
        int h = 3;
        float[] output = ConvUtil.averageByMask(databuffer, w, h, mask);
        assertFloatArrayEquals(new float[]{1, 1, 0, 0}, output);
    }

    private void assertFloatArrayEquals(float[] expected, float[] actual) {
        FloatComparator comparator = new FloatComparator(0.01f);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(0, comparator.compareNonNull(expected[i], actual[i]), String.format("Value mismatch at index %s [expected=%s, actual=%s]", i, expected[i], actual[i]));
        }
    }


    @Test
    void getRGB_TODO() {
        //TODO: Implement more tests
    }


    @Test
    void maxPoolingOnce_TODO() {
        //TODO: Implement more tests
    }

    /*
    @Test
    void maxPoolingOnce_whenMatrixSize3By3_expectOutput() {

        int[] data = new int[]{
                1, 0, 0,
                0, 2, 0,
                0, 0, 3
        };
        int[] output = maxPoolingOnce(data, 3, 3, 2);
        assertEquals(4, output.length);
        assertArrayEquals(new int[]{2, 0, 0, 3}, output);
    }

     */

    /*
    @Test
    void maxPoolingOnce_whenMatrixSize4By4Diag_expectOutput() {

        int[] data = new int[]{
                1, 0, 0, 0,
                0, 2, 0, 0,
                0, 0, 3, 0,
                0, 0, 0, 4,
        };
        int[] output = maxPoolingOnce(data, 2 * 2, 2 * 2, 2);
        assertEquals(8, output.length);
        assertArrayEquals(new int[]{2, 0, 0, 4, 0, 0, 0, 0}, output);
    }

     */

    /*


    @Test
    void maxPoolingOnce_whenMatrixSize4By4DiagDoneTwice_expectOutput() {

        int[] data = new int[]{
                1, 0, 0, 0,
                0, 2, 0, 0,
                0, 0, 3, 0,
                0, 0, 0, 4,
        };
        int[] output1 = maxPoolingOnce(data, 4, 4, 2);
        int[] output2 = maxPoolingOnce(output1, 2, 2, 2);
        assertEquals(2, output2.length);
        assertArrayEquals(new int[]{4, 0}, output2);


    }


     */
    /*
    @Test
    void maxPoolingOnce_whenMatrixSize16By16DiagDoneTwice_expectOutput() {

        int[] data = new int[]{
                1, 0, 0, 0, 1, 0, 0, 0,
                0, 2, 0, 0, 0, 2, 0, 0,
                0, 0, 3, 0, 0, 0, 3, 0,
                0, 0, 0, 4, 0, 0, 0, 4,
                1, 0, 0, 0, 1, 0, 0, 0,
                0, 2, 0, 0, 0, 2, 0, 0,
                0, 0, 3, 0, 0, 0, 3, 0,
                0, 0, 0, 4, 0, 0, 0, 4,
        };
        int[] output1 = maxPoolingOnce(data, 8, 8, 2);
        int[] output2 = maxPoolingOnce(output1, 8 / 2, 8 / 2, 2);
        int[] output3 = maxPoolingOnce(output2, 8 / 4, 8 / 4, 2);
        int[] output4 = maxPoolingOnce(output3, 1, 1, 2);
        assertEquals(2, output4.length);
        assertArrayEquals(new int[]{4, 0}, output4);
    }
*/

    @Test
    void maxPoolingUntil_TODO() {
        //TODO: Implement more tests
    }

    /*
    @Test
    void maxPoolingUntil_MatrixSize64_expectOutput() {

        int[] data = new int[]{
                1, 0, 0, 0, 1, 0, 0, 0,
                0, 2, 0, 0, 0, 2, 0, 0,
                0, 0, 3, 0, 0, 0, 3, 0,
                0, 0, 0, 4, 0, 0, 0, 4,
                1, 0, 0, 0, 1, 0, 0, 0,
                0, 2, 0, 0, 0, 2, 0, 0,
                0, 0, 3, 0, 0, 0, 3, 0,
                0, 0, 0, 4, 0, 0, 0, 4,
        };
        int[] output = maxPoolingUntil(data, 8, 8, 2, 1, 1);
        assertEquals(2, output.length);
        assertArrayEquals(new int[]{4, 0}, output);

    }
*/
    @Test
    void getPixels_TODO() {
        //TODO: Implement more tests
    }

    /*
    @Test
    void getPixels_whenWindowingShifted2_expectCorrectOutputArray() {

        int[] data = new int[]{
                1, 0, 0,
                0, 2, 0,
                0, 0, 3
        };

        //Extract upper-left (first) matrix
        int[] outputUpperLeft = getPixels(data, 0, 0, 2, 2, 3, 3);
        assertEquals(4, outputUpperLeft.length);
        assertArrayEquals(new int[]{1, 0, 0, 2}, outputUpperLeft);

        //Extract upper-right (second) matrix
        int[] outputUpperRight = getPixels(data, 2, 0, 2, 2, 3, 3);
        assertEquals(4, outputUpperRight.length);
        assertArrayEquals(new int[]{0, 0, 0, 0}, outputUpperRight);

        //Extract bottom-left (first) matrix
        int[] outputBottomLeft = getPixels(data, 0, 2, 2, 2, 3, 3);
        assertEquals(4, outputBottomLeft.length);
        assertArrayEquals(new int[]{0, 0, 0, 0}, outputBottomLeft);

        //Extract bottom-right (second) matrix
        int[] outputBottomRight = getPixels(data, 2, 2, 2, 2, 3, 3);
        assertEquals(4, outputBottomRight.length);
        assertArrayEquals(new int[]{3, 0, 0, 0}, outputBottomRight);
    }
*/

    /*
    @Test
    void getPixelsByte_whenWindowingShifted2_expectCorrectOutputArray() {

        byte[] data = new byte[]{
                1, 0, 0,
                0, 2, 0,
                0, 0, 3
        };

        //Extract upper-left (first) matrix
        byte[] outputUpperLeft = getPixelsByte(data, 0, 0, 2, 2, 3, 3);
        assertEquals(4, outputUpperLeft.length);
        assertArrayEquals(new byte[]{1, 0, 0, 2}, outputUpperLeft);

        //Extract upper-right (second) matrix
        byte[] outputUpperRight = getPixelsByte(data, 2, 0, 2, 2, 3, 3);
        assertEquals(4, outputUpperRight.length);
        assertArrayEquals(new byte[]{0, 0, 0, 0}, outputUpperRight);

        //Extract bottom-left (first) matrix
        byte[] outputBottomLeft = getPixelsByte(data, 0, 2, 2, 2, 3, 3);
        assertEquals(4, outputBottomLeft.length);
        assertArrayEquals(new byte[]{0, 0, 0, 0}, outputBottomLeft);

        //Extract bottom-right (second) matrix
        byte[] outputBottomRight = getPixelsByte(data, 2, 2, 2, 2, 3, 3);
        assertEquals(4, outputBottomRight.length);
        assertArrayEquals(new byte[]{3, 0, 0, 0}, outputBottomRight);
    }

     */
}