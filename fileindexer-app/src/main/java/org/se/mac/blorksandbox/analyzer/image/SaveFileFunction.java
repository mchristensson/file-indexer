package org.se.mac.blorksandbox.analyzer.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;

public class SaveFileFunction implements Function<BufferedImage, BufferedImage>, SaveImageToDiskSupport {
    private static final Logger logger = LoggerFactory.getLogger(SaveFileFunction.class);
    private Supplier<String> outputPathSupplier;
    private final String outputFileFormat;

    public SaveFileFunction(Supplier<String> outputPathSupplier, String outputFileFormat) {
        this.outputFileFormat = outputFileFormat;
        this.outputPathSupplier = outputPathSupplier;
    }

    @Override
    public BufferedImage apply(BufferedImage bufferedImage) {
        saveToLocalDisk(bufferedImage, outputPathSupplier.get(), outputFileFormat);
        return bufferedImage;
    }

}
