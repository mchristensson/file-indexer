package org.se.mac.blorksandbox.analyzer.task;

import com.drew.metadata.Directory;
import com.drew.metadata.iptc.IptcDirectory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageAnalyzerTaskTest {

    @Test
    void apply_whenNullPath_expectException() {
        ImageAnalyzerTask task = new ImageAnalyzerTask();
        try {
            task.apply(null);
            fail("Exception expected");
        } catch (Exception e) {
            assertEquals(e.getClass(), NullPointerException.class);
        }
    }

    @AfterEach
    public void verifyMocksAfter() {
    }

    @Test
    void apply_whenCurrentPathNoAccess_expectException() {
        ImageAnalyzerTask task = Mockito.mock(ImageAnalyzerTask.class);
        try {
            when(task.apply(any(Path.class))).thenCallRealMethod();
            Path p = Path.of("");
            task.apply(p);
            fail("Exception expected");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("read access to file"));
        }
        verifyNoMoreInteractions(task);
    }

    @Test
    void apply_whenCurrentPathIsDirectory_expectException() {
        ImageAnalyzerTask task = Mockito.mock(ImageAnalyzerTask.class);
        try {
            when(task.apply(any(Path.class))).thenCallRealMethod();
            Path testDirectory = Path.of("src/test/resources/images/misc");
            task.apply(testDirectory);
            fail("Exception expected");
        } catch (Exception e) {
            assertEquals(e.getClass(), FileNotFoundException.class);
        }
        verifyNoMoreInteractions(task);
    }

    @Test
    void apply_whenTestFile1_expecteRead() throws Exception {
        ImageAnalyzerTask task = Mockito.mock(ImageAnalyzerTask.class);

        when(task.apply(any(Path.class))).thenCallRealMethod();
        Path testFile1 = Path.of("src/test/resources/images/misc/Projekt_20220216_0002.png");
        Map<String, String> result = task.apply(testFile1);
        assertEquals(6, result.size());
        assertEquals("Projekt_20220216_0002.png", result.get(ImageAnalyzerTask.FILE_NAME), "Invalid file name");
        assertEquals("256", result.get(ImageAnalyzerTask.IMAGE_WIDTH), "Invalid width");
        assertEquals("192", result.get(ImageAnalyzerTask.IMAGE_HEIGHT), "Invalid height");
        assertEquals("2023-04-03T12:02:13.3427993Z", result.get(ImageAnalyzerTask.DATE_TIME), "Invalid datetime");
        assertEquals("image/png", result.get(ImageAnalyzerTask.MIME_TYPE), "Invalid mime-type");
        assertTrue(isNumeric(result.get(ImageAnalyzerTask.FILE_SIZE)), "Invalid file size");
        assertNull(result.get(ImageAnalyzerTask.MODEL));
        assertNull(result.get(ImageAnalyzerTask.SOFTWARE));
        assertNull(result.get(ImageAnalyzerTask.MAKE));
        assertNull(result.get(ImageAnalyzerTask.DATE_TIME_ORIGINAL), "Invalid date-time original");

        verifyNoMoreInteractions(task);
    }

    @Test
    void apply_whenTestFile2_expecteRead() throws Exception {
        ImageAnalyzerTask task = Mockito.mock(ImageAnalyzerTask.class);

        when(task.apply(any(Path.class))).thenCallRealMethod();
        Path testFile1 = Path.of("src/test/resources/images/misc/Projekt_20220605_0003.jpeg");
        Map<String, String> result = task.apply(testFile1);
        assertEquals(10, result.size());
        assertEquals("Projekt_20220605_0003.jpeg", result.get(ImageAnalyzerTask.FILE_NAME), "Invalid file name");
        assertEquals("256", result.get(ImageAnalyzerTask.IMAGE_WIDTH), "Invalid width");
        assertEquals("192", result.get(ImageAnalyzerTask.IMAGE_HEIGHT), "Invalid height");
        assertEquals("2023-04-03T13:59:26", result.get(ImageAnalyzerTask.DATE_TIME), "Invalid datetime");
        assertEquals("iPhone 12", result.get(ImageAnalyzerTask.MODEL), "Invalid model");
        assertEquals("Adobe Bridge 2022 (Macintosh)", result.get(ImageAnalyzerTask.SOFTWARE), "Invalid software");
        assertEquals("Apple", result.get(ImageAnalyzerTask.MAKE), "Invalid make");
        assertTrue(isNumeric(result.get(ImageAnalyzerTask.FILE_SIZE)), "Invalid file size");
        assertEquals("image/jpeg", result.get(ImageAnalyzerTask.MIME_TYPE), "Invalid mime-type");
        assertEquals("2022-06-05T15:36:54+02:00", result.get(ImageAnalyzerTask.DATE_TIME_ORIGINAL), "Invalid date-time original");

        verifyNoMoreInteractions(task);
    }

    @Test
    void apply_whenTestFile3_expecteRead() throws Exception {
        ImageAnalyzerTask task = Mockito.mock(ImageAnalyzerTask.class);

        when(task.apply(any(Path.class))).thenCallRealMethod();
        Path testFile1 = Path.of("src/test/resources/images/misc/Projekt_20220622_0004.jpeg");
        Map<String, String> result = task.apply(testFile1);
        assertEquals(8, result.size());
        assertNull(result.get(ImageAnalyzerTask.MODEL));
        assertNull(result.get(ImageAnalyzerTask.MAKE));
        assertEquals("Projekt_20220622_0004.jpeg", result.get(ImageAnalyzerTask.FILE_NAME), "Invalid file name");
        assertEquals("256", result.get(ImageAnalyzerTask.IMAGE_WIDTH), "Invalid width");
        assertEquals("232", result.get(ImageAnalyzerTask.IMAGE_HEIGHT), "Invalid height");
        assertEquals("Adobe Bridge 2022 (Macintosh)", result.get(ImageAnalyzerTask.SOFTWARE), "Invalid software");
        assertTrue(isNumeric(result.get(ImageAnalyzerTask.FILE_SIZE)), "Invalid file size");
        assertNotNull(result.get(ImageAnalyzerTask.DATE_TIME));

        verifyNoMoreInteractions(task);
    }


    @Test
    void parseDate_whenDateAndTimePresent_expectParsedZonedDateTime() {
        Directory dic = mock(IptcDirectory.class);
        when(dic.getString(IptcDirectory.TAG_DATE_CREATED)).thenReturn("20220605");
        when(dic.getString(IptcDirectory.TAG_TIME_CREATED)).thenReturn("153654+0200");
        Function<String, String> transferFunction = ImageAnalyzerTask.computeItpcDateTime(dic);
        String result = transferFunction.apply(null);
        assertEquals("2022-06-05T15:36:54+02:00", result);
    }

    @Test
    void parseDate_whenDateOnlyPresentA_expectParsedDate() {
        Directory dic = mock(IptcDirectory.class);
        when(dic.getString(IptcDirectory.TAG_DATE_CREATED)).thenReturn("20220605");
        when(dic.getString(IptcDirectory.TAG_TIME_CREATED)).thenReturn(null);
        Function<String, String> transferFunction = ImageAnalyzerTask.computeItpcDateTime(dic);
        String result = transferFunction.apply(null);
        assertEquals("2022-06-05", result);
    }

    @Test
    void parseDate_whenDateOnlyPresentB_expectParsedDate() {
        Directory dic = mock(IptcDirectory.class);
        when(dic.getString(IptcDirectory.TAG_DATE_CREATED)).thenReturn("20220605");
        when(dic.getString(IptcDirectory.TAG_TIME_CREATED)).thenReturn("");
        Function<String, String> transferFunction = ImageAnalyzerTask.computeItpcDateTime(dic);
        String result = transferFunction.apply(null);
        assertEquals("2022-06-05", result);
    }

    @Test
    void parseDate_whenTimeOnlyPresentA_expectParsedZonedDateTime() {
        Directory dic = mock(IptcDirectory.class);
        when(dic.getString(IptcDirectory.TAG_DATE_CREATED)).thenReturn(null);
        when(dic.getString(IptcDirectory.TAG_TIME_CREATED)).thenReturn("153654+0200");
        Function<String, String> transferFunction = ImageAnalyzerTask.computeItpcDateTime(dic);
        String result = transferFunction.apply(null);
        assertEquals("15:36:54", result);
    }

    @Test
    void parseDate_whenTimeOnlyPresentB_expectParsedZonedDateTime() {
        Directory dic = mock(IptcDirectory.class);
        when(dic.getString(IptcDirectory.TAG_DATE_CREATED)).thenReturn("");
        when(dic.getString(IptcDirectory.TAG_TIME_CREATED)).thenReturn("153654+0200");
        Function<String, String> transferFunction = ImageAnalyzerTask.computeItpcDateTime(dic);
        String result = transferFunction.apply(null);
        assertEquals("15:36:54", result);
    }

    private static boolean isNumeric(String strNum) {
        try {
            if (strNum != null) {
                Double.parseDouble(strNum);
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}