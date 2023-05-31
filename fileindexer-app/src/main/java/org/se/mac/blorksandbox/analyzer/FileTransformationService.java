package org.se.mac.blorksandbox.analyzer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.UUID;
import org.se.mac.blorksandbox.analyzer.data.SmallFileData;
import org.se.mac.blorksandbox.analyzer.image.ColormodeFunction;
import org.se.mac.blorksandbox.analyzer.image.ContrastFunction;
import org.se.mac.blorksandbox.analyzer.image.SaveImageToDiskSupport;
import org.se.mac.blorksandbox.scanner.rest.ImageTransformDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service with ability to transform a file's content.
 */
@Service
public class FileTransformationService {

    private static final Logger logger = LoggerFactory.getLogger(FileTransformationService.class);

    @Autowired
    private LogicalFileIndexService logicalFileIndexService;

    /**
     * Creates a copy of a file and returns the copied file's ID.
     *
     * @param imageData           Image to copy from
     * @param transformDefinition Transformation to apply to the file
     * @param width               Width of image to copy from
     * @param height              Height of image to copy from
     * @return UUID of new file
     */
    public UUID transformImage(SmallFileData imageData,
                               ImageTransformDefinition transformDefinition, int width,
                               int height) throws IOException {
        logger.debug("Begin transforming image... [id={}]", imageData.getId());
        BufferedImage image = SaveImageToDiskSupport.getImage(imageData.getBlob(), width, height);

        //TODO: Implement support for transforming file according to arg for transformDefinition

        ContrastFunction contrastFunction = new ContrastFunction();
        ColormodeFunction colormodeFunction = new ColormodeFunction(BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage outputImage = colormodeFunction.andThen(contrastFunction).apply(image);

        String outputFileFormat = "JPG";
        SmallFileData output = logicalFileIndexService.createSmallFile(imageData.getDeviceId(),
                null, ByteBuffer.wrap(SaveImageToDiskSupport.getBytes(outputImage)), outputFileFormat, Instant.now());
        return output.getId();
    }

}
