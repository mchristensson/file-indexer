package org.se.mac.blorksandbox.analyzer;

import org.se.mac.blorksandbox.analyzer.data.SmallFileData;
import org.se.mac.blorksandbox.analyzer.image.ColormodeFunction;
import org.se.mac.blorksandbox.analyzer.image.ContrastFunction;
import org.se.mac.blorksandbox.analyzer.image.SaveFileFunction;
import org.se.mac.blorksandbox.scanner.rest.ImageTransformDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.UUID;

@Service
public class FileTransformationService {

    private static final Logger logger = LoggerFactory.getLogger(FileTransformationService.class);

    @Autowired
    private LogicalFileIndexService logicalFileIndexService;

    /**
     * Creates a copy of a file and returns the copied file's ID
     *
     * @param imageData           Image to copy from
     * @param transformDefinition Transformation to apply to the file
     * @param width               Width of image to copy from
     * @param height              Height of image to copy from
     * @return UUID of new file
     */
    public UUID transformImage(SmallFileData imageData, ImageTransformDefinition transformDefinition, int width, int height) throws IOException {
        logger.debug("Begin transforming image... [id={}]", imageData.getId());

        BufferedImage image = getImage(imageData, width, height);

        //TODO: Implement support for transforming file according to arg for transformDefinition

        ContrastFunction contrastFunction = new ContrastFunction();
        ColormodeFunction colormodeFunction = new ColormodeFunction();
        BufferedImage outputImage = colormodeFunction
                .andThen(contrastFunction)
                .apply(image);

        String outputFileFormat = "JPG";
        SmallFileData output = logicalFileIndexService.createSmallFile(imageData.getDeviceId(), null,
                ByteBuffer.wrap(getBytes(outputImage)), outputFileFormat, Instant.now());
        return output.getId();
    }

    /**
     * Extracts bytes from image
     *
     * @param image Image to extract data from
     * @return byte-array
     * @throws IOException If content is not a databuffer of type {@code DataBufferByte}
     */
    public static byte[] getBytes(BufferedImage image) throws IOException {
        DataBuffer dataBuffer = image.getRaster().getDataBuffer();
        if (dataBuffer instanceof DataBufferByte) {
            return ((DataBufferByte) dataBuffer).getData();
        } else {
            throw new IOException("Unsupported bytes");
        }
    }

    public static BufferedImage getImage(SmallFileData input, int width, int height) throws IOException {
        if (input.getBlob() == null) {
            throw new IOException("Empty data");
        }
        return getImage(input.getBlob().array(), width, height);
    }

    public static BufferedImage getImage(byte[] bytes, int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        image.setData(Raster.createRaster(image.getSampleModel(), new DataBufferByte(bytes, bytes.length), new Point()));
        return image;
    }
}
