package org.se.mac.blorksandbox.analyzer.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ImageHashGeneratorTaskTest {

    @Test
    void apply() {
        FileAnalyzerTask task = new ImageHashGeneratorTask("JPG", 128, true, 8);
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
        assertEquals("foo", result);

    }

    @Test
    void apply_whenTestFilesDices() throws Exception {
        FileAnalyzerTask<String> task = new ImageHashGeneratorTask("JPG", 16, true, 8);


        Path testFile1 = Path.of("./src/test/resources/images/misc/dice-2.png");
        String result1 = task.apply(testFile1);
        Path testFile2 = Path.of("./src/test/resources/images/misc/dice-3.png");
        String result2 = task.apply(testFile2);
        Path testFile3 = Path.of("./src/test/resources/images/misc/dice-4.png");
        String result3 = task.apply(testFile3);

        System.out.println("Result1: " + result1);
        System.out.println("Result2: " + result2);
        System.out.println("Result3: " + result3);

        long r1 = Long.parseUnsignedLong(result1, 16);
        long r2 = Long.parseUnsignedLong(result2, 16);
        long r3 = Long.parseUnsignedLong(result3, 16);

        int diffR1R2 = ImageHashGeneratorTask.hammingDistance(r1,r2);
        int diffR1R3 = ImageHashGeneratorTask.hammingDistance(r1,r3);
        int diffR2R3 = ImageHashGeneratorTask.hammingDistance(r2,r3);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);


    }
    @Test
    void apply_whenTestFilesCoins() throws Exception {
        FileAnalyzerTask<String> task = new ImageHashGeneratorTask("JPG", 16, true, 8);


        Path testFile1 = Path.of("./src/test/resources/images/misc/1-krona.png");
        String result1 = task.apply(testFile1);
        Path testFile2 = Path.of("./src/test/resources/images/misc/2-krona.png");
        String result2 = task.apply(testFile2);
        Path testFile3 = Path.of("./src/test/resources/images/misc/5-krona.png");
        String result3 = task.apply(testFile3);

        System.out.println("Result1: " + result1);
        System.out.println("Result2: " + result2);
        System.out.println("Result3: " + result3);

        long r1 = Long.parseUnsignedLong(result1, 16);
        long r2 = Long.parseUnsignedLong(result2, 16);
        long r3 = Long.parseUnsignedLong(result3, 16);

        int diffR1R2 = ImageHashGeneratorTask.hammingDistance(r1,r2);
        int diffR1R3 = ImageHashGeneratorTask.hammingDistance(r1,r3);
        int diffR2R3 = ImageHashGeneratorTask.hammingDistance(r2,r3);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);


    }

}