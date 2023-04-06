package org.se.mac.blorksandbox.analyzer.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;

public class SaveFileFunction implements Function<BufferedImage, BufferedImage> {
    private static final Logger logger = LoggerFactory.getLogger(SaveFileFunction.class);
    private Supplier<String> outputPathSupplier;
    private final String outputFileFormat;

    public SaveFileFunction(Supplier<String> outputPathSupplier, String outputFileFormat) {
        this.outputFileFormat = outputFileFormat;
        this.outputPathSupplier = outputPathSupplier;
    }

    @Override
    public BufferedImage apply(BufferedImage bufferedImage) {
        saveToLocalDisk(bufferedImage, outputPathSupplier.get());
        return bufferedImage;
    }

    private void saveToLocalDisk(BufferedImage bufferedImage, final String location) {
        File f = new File(location);
        if (!f.mkdirs()) {
            logger.debug("Directory was not created");
        }
        logger.debug("Saving to file... [path={}]", f.getAbsolutePath());
        try {
            ImageIO.write(bufferedImage, outputFileFormat, f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
