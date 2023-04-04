package org.se.mac.blorksandbox.analyzer.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

public class SaveFileFunction implements Function<BufferedImage, BufferedImage> {
    private static final Logger logger = LoggerFactory.getLogger(SaveFileFunction.class);
    private final String outputFileName;
    private final String outputFileFormat;
    private String outputFile;

    public SaveFileFunction(String outputFileName, String outputFileFormat) {
        this.outputFileName = outputFileName;
        this.outputFileFormat = outputFileFormat;
    }

    @Override
    public BufferedImage apply(BufferedImage bufferedImage) {
        File f = new File("./target/output_" + System.currentTimeMillis() + this.outputFileName);
        logger.debug("Saving to file... [path={}]", f.getAbsolutePath());
        try {
            ImageIO.write(bufferedImage, outputFileFormat, f);
            this.outputFile = f.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bufferedImage;
    }

    public String getOutputFile() {
        return outputFile;
    }
}
