package org.se.mac.blorksandbox.analyzer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.se.mac.blorksandbox.analyzer.data.SmallFileData;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepositoryImpl;
import org.se.mac.blorksandbox.scanner.rest.ImageTransformDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {LogicalFileRepositoryImpl.class, FileTransformationService.class})
class FileTransformationServiceTest {

    @MockBean
    private LogicalFileIndexService logicalFileIndexService;

    @Autowired
    private FileTransformationService fileTransformationService;

    @AfterEach
    public void verifyMocksAfter() {
        verifyNoMoreInteractions(logicalFileIndexService);
    }

    @Test
    void transformImage_whenNoData_expectException() {
        SmallFileData imageData = mock(SmallFileData.class);
        ImageTransformDefinition transformDefinition = mock(ImageTransformDefinition.class);

        try {
            fileTransformationService.transformImage(imageData, transformDefinition, 128, 128);
            fail("Exception expected");
        } catch (IOException e) {
            assertEquals("Empty data", e.getMessage());
        }
        verify(imageData).getId();
        verify(imageData).getBlob();
        verifyNoMoreInteractions(transformDefinition, imageData);
    }


    @Test
    void transformImage_whenInvalidByteContent_expectException() {
        SmallFileData imageData = mock(SmallFileData.class);
        ByteBuffer blob = ByteBuffer.wrap("hello world".getBytes());
        when(imageData.getBlob()).thenReturn(blob);
        ImageTransformDefinition transformDefinition = mock(ImageTransformDefinition.class);

        try {
            UUID uuid = fileTransformationService.transformImage(imageData, transformDefinition, 128, 128);
            assertNotNull(uuid);
        } catch (RuntimeException e) {
            assertEquals("Incorrect scanline stride: 384", e.getMessage());
        } catch (IOException e) {
            fail("Unexpected exception");
        }
        verify(imageData).getId();
        verify(imageData, times(2)).getBlob();
        verifyNoMoreInteractions(transformDefinition, imageData);
    }

    @Test
    void transformImage_whenTestImage_expectUUID() throws IOException {

        //Use a test image as byte data for the input SmallFileData mock
        Path testFile1 = Path.of("src/test/resources/images/misc/10-krona.png");
        BufferedImage d = ImageIO.read(testFile1.toFile());
        byte[] imageBytes = FileTransformationService.getBytes(d);
        ByteBuffer bytebuffer = ByteBuffer.wrap(Arrays.copyOf(imageBytes, imageBytes.length));
        SmallFileData imageData = mock(SmallFileData.class);
        when(imageData.getId()).thenReturn(UUID.randomUUID());
        when(imageData.getBlob()).thenReturn(bytebuffer);
        when(imageData.getDeviceId()).thenReturn(UUID.randomUUID());

        //Mock an output
        SmallFileData mockResult = mock(SmallFileData.class);
        when(mockResult.getId()).thenReturn(UUID.randomUUID());
        when(logicalFileIndexService.createSmallFile(any(UUID.class), isNull(), any(), eq("JPG"), any(Instant.class))).thenReturn(mockResult);

        //Mock a transform
        ImageTransformDefinition transformDefinition = mock(ImageTransformDefinition.class);

        //Perform test
        UUID uuid = fileTransformationService.transformImage(imageData, transformDefinition, 250, 250);

        //Verify mocks
        verify(logicalFileIndexService).createSmallFile(any(UUID.class), isNull(), any(), eq("JPG"), any(Instant.class));
        verify(imageData).getId();
        verify(imageData).getDeviceId();
        verify(imageData, times(2)).getBlob();
        verifyNoMoreInteractions(transformDefinition, imageData);
    }


}