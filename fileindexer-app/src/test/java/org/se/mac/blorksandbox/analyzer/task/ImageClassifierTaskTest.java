package org.se.mac.blorksandbox.analyzer.task;

import org.junit.jupiter.api.Test;
import org.se.mac.blorksandbox.analyzer.image.GenerateConvValueFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ImageClassifierTaskTest {

    private static final Logger logger = LoggerFactory.getLogger(ImageClassifierTaskTest.class);

    @Test
    void apply_whenCurrentPathIsDirectory_expectException() {

        ImageClassifierTask task = new ImageClassifierTask(UUID.randomUUID().toString(), "JPG", 90);
        try {
            //when(task.apply(any(Path.class))).thenCallRealMethod();
            Path testFile = Path.of("target/test-filestructure/copyrighted/catsanddogs/train/cats/cat_95.jpg");
            task.apply(testFile);

        } catch (Exception e) {
            e.printStackTrace();
            assertEquals(e.getClass(), FileNotFoundException.class);
        }

    }


}