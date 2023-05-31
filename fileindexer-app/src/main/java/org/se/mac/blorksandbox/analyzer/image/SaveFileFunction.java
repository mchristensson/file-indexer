package org.se.mac.blorksandbox.analyzer.image;

import java.awt.image.BufferedImage;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modifier for {@link BufferedImage} that save the file and the pipe the image further.
 */
public class SaveFileFunction implements Function<BufferedImage, BufferedImage>,
        SaveImageToDiskSupport {
    private static final Logger logger = LoggerFactory.getLogger(SaveFileFunction.class);
    private final Supplier<String> outputPathSupplier;
    private final String outputFileFormat;

    /**
     * Default constructor.
     *
     * @param outputPathSupplier Suppler for where to store the output file
     * @param outputFileFormat   Output file format (i.e. JPG)
     */
    public SaveFileFunction(Supplier<String> outputPathSupplier, String outputFileFormat) {
        this.outputFileFormat = outputFileFormat;
        this.outputPathSupplier = outputPathSupplier;
    }

    @Override
    public BufferedImage apply(BufferedImage bufferedImage) {
        logger.trace("Saving file... [outputPath={}, format={}]", outputPathSupplier.get(),
                outputFileFormat);
        saveToLocalDisk(bufferedImage, outputPathSupplier.get(), outputFileFormat);
        return bufferedImage;
    }

}
