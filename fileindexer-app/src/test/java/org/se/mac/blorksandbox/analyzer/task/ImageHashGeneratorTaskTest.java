package org.se.mac.blorksandbox.analyzer.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageHashGeneratorTaskTest {

    @Test
    void apply() {
        FileAnalyzerTask task = new ImageHashGeneratorTask();
        try {
            task.apply(null);
            fail("Exception expected");
        } catch (Exception e) {
            assertEquals(e.getClass(), NullPointerException.class);
        }
    }

    @Test
    void apply_whenTestFile1_expecteRead() throws Exception {
        FileAnalyzerTask<String> task = new ImageHashGeneratorTask();


        Path testFile1 = Path.of("./src/test/resources/images/misc/cats.jpg");
        String result = task.apply(testFile1);

        assertNotNull(result);
        assertEquals("foo", result);

    }

}