package org.se.mac.blorksandbox.analyzer.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.se.mac.blorksandbox.analyzer.image.ContrastFunction;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ImageHashGeneratorTaskTest {

    @Test
    void apply() {
        FileAnalyzerTask<String> task = new ImageHashGeneratorTask("JPG", 128, true, 8);
        try {
            task.apply(null);
            fail("Exception expected");
        } catch (Exception e) {
            assertEquals(e.getClass(), NullPointerException.class);
        }
    }

    @Test
    void apply_whenTestFile1_expecteRead() throws Exception {
        FileAnalyzerTask<String> task = new ImageHashGeneratorTask("JPG", 128, true, 8);
        Path testFile1 = Path.of("./src/test/resources/images/misc/cats.jpg");
        String result = task.apply(testFile1);

        assertNotNull(result);
        assertEquals("000000024efe", result);
    }

    @Test
    void posterize_when2ColorsExpectedColor_expectColor() {
        int input = 60;
        int nColors = 2;
        int output = ContrastFunction.posterize(input,nColors);
        assertEquals(0, output);
    }

    @Test
    void posterize_when4ColorsExpectedColor_expectColor() {
        int input = 60;
        int nColors = 4;
        int output = ContrastFunction.posterize(input,nColors);
        assertEquals(0, output);
    }

    @Test
    void posterize_when8ColorsExpectedColor_expectColor() {
        int input = 60;
        int nColors = 8;
        int output = ContrastFunction.posterize(input,nColors);
        assertEquals(32, output);
    }

    @Test
    void posterize_when16ColorsExpectedColor_expectColor() {
        int input = 60;
        int nColors = 16;
        int output = ContrastFunction.posterize(input,nColors);
        assertEquals(48, output);
    }

    @Test
    void apply_whenTestFilesDices_expectHammingDistance() throws Exception {
        FileAnalyzerTask<String> task = new ImageHashGeneratorTask("JPG", 16, true, 8);

        Path testFile1 = Path.of("./src/test/resources/images/misc/dice-2.png");
        String result1 = task.apply(testFile1);
        assertNotNull(result1);
        Path testFile2 = Path.of("./src/test/resources/images/misc/dice-3.png");
        String result2 = task.apply(testFile2);
        assertNotNull(result2);
        Path testFile3 = Path.of("./src/test/resources/images/misc/dice-4.png");
        String result3 = task.apply(testFile3);
        assertNotNull(result3);

        long r1 = Long.parseUnsignedLong(result1, 16);
        long r2 = Long.parseUnsignedLong(result2, 16);
        long r3 = Long.parseUnsignedLong(result3, 16);

        int diffR1R2 = ImageHashGeneratorTask.hammingDistance(r1, r2);
        assertEquals(1,diffR1R2, "Invalid difference value between images");
        int diffR1R3 = ImageHashGeneratorTask.hammingDistance(r1, r3);
        assertEquals(2,diffR1R3, "Invalid difference value between images");
        int diffR2R3 = ImageHashGeneratorTask.hammingDistance(r2, r3);
        assertEquals(1,diffR2R3, "Invalid difference value between images");
    }

    @Test
    void apply_whenTestFilesCoins_expectHammingDistance() throws Exception {
        FileAnalyzerTask<String> task = new ImageHashGeneratorTask("JPG", 16, true, 8);

        Path testFile1 = Path.of("./src/test/resources/images/misc/1-krona.png");
        String result1 = task.apply(testFile1);
        assertNotNull(result1);
        Path testFile2 = Path.of("./src/test/resources/images/misc/2-krona.png");
        String result2 = task.apply(testFile2);
        assertNotNull(result2);
        Path testFile3 = Path.of("./src/test/resources/images/misc/5-krona.png");
        String result3 = task.apply(testFile3);
        assertNotNull(result3);

        long r1 = Long.parseUnsignedLong(result1, 16);
        long r2 = Long.parseUnsignedLong(result2, 16);
        long r3 = Long.parseUnsignedLong(result3, 16);

        int diffR1R2 = ImageHashGeneratorTask.hammingDistance(r1, r2);
        assertEquals(13,diffR1R2, "Invalid difference value between images");
        int diffR1R3 = ImageHashGeneratorTask.hammingDistance(r1, r3);
        assertEquals(22,diffR1R3, "Invalid difference value between images");
        int diffR2R3 = ImageHashGeneratorTask.hammingDistance(r2, r3);
        assertEquals(13,diffR2R3, "Invalid difference value between images");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 60, 120, 200, 253, 254, 255})
    void gain_whenGain1_expectNoDiff(int input) {
        float g = 1.0f;
        int output = ContrastFunction.gain(input, g);
        assertEquals(input, output);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 60, 120, 200, 253, 254, 255})
    void gain_whenRangeAndGain2_expectDoubledOrMax(int input) {
        float g = 2.0f;
        int output = ContrastFunction.gain(input, g);
        assertEquals(Math.min(input * 2, 255), output);
    }

    @Test
    void passfilter_whenFc200Dfc0_expectTriangel() {
        float fc = 200f;
        int[] data = IntStream.range(0, 255)
                .filter(x -> x % 20 == 0).toArray();

        int[] output = IntStream.of(data)
                .map(input -> ContrastFunction.bandPassFilter(input, fc, 0, 1f, true, -1))
                .toArray();

        assertEquals(0, output[0]);
        assertEquals(0, output[1]);
        assertEquals(0, output[2]);
        assertEquals(0, output[3]);
        assertEquals(0, output[4]);
        assertEquals(0, output[5]);
        assertEquals(0, output[6]);
        assertEquals(0, output[7]);
        assertEquals(0, output[8]);
        assertEquals(0, output[9]);
        assertEquals(200, output[10]);
        assertEquals(0, output[11]);
        assertEquals(0, output[12]);
    }


    @Test
    void passfilter_whenFc100Dfc20_expectBump() {
        float fc = 100f;
        float dfc = 20f;
        int[] data = IntStream.range(0, 255)
                .filter(x -> x % 20 == 0).toArray();

        int[] output = IntStream.of(data)
                .map(input -> ContrastFunction.bandPassFilter(input, fc, dfc, 1f, true, -1)).toArray();

        assertEquals(0, output[0]);
        assertEquals(0, output[1]);
        assertEquals(0, output[2]);
        assertEquals(0, output[3]);
        assertEquals(80, output[4]);
        assertEquals(100, output[5]);
        assertEquals(120, output[6]);
        assertEquals(0, output[7]);
        assertEquals(0, output[8]);
        assertEquals(0, output[9]);
        assertEquals(0, output[10]);
        assertEquals(0, output[11]);
        assertEquals(0, output[12]);
    }

}