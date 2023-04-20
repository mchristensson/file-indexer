package org.se.mac.blorksandbox.analyzer.image;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.se.mac.blorksandbox.analyzer.image.GenerateConvValueFunction.*;

class GenerateConvValueFunctionTest {


    @Test
    void getRGB_TODO() {
        //TODO: Implement more tests
    }


    @Test
    void maxPoolingOnce_TODO() {
        //TODO: Implement more tests
    }

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


    @Test
    void maxPoolingUntil_TODO() {
        //TODO: Implement more tests
    }

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

    @Test
    void getPixels_TODO() {
        //TODO: Implement more tests
    }

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
}