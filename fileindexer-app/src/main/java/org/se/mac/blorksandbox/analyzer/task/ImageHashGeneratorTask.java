package org.se.mac.blorksandbox.analyzer.task;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.se.mac.blorksandbox.analyzer.image.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Testing functionality mentioned in
 * <a href="https://www.hackerfactor.com/blog/index.php?/archives/432-Looks-Like-It.html">...</a>.
 */
public class ImageHashGeneratorTask extends AbstractImageAnalyzerTask {

    private static final Logger logger = LoggerFactory.getLogger(ImageHashGeneratorTask.class);
    private final String outputFileFormat;
    private final int checksumThreshold;
    private final int maxPixels;

    /**
     * Default constructor.
     *
     * @param procId            Process identifier for this task
     * @param outputFileFormat  Output file format (the saved file)
     * @param checksumThreshold Threshold value used when generating checksum
     * @param maxPixels         When scaling the image this value will be the maximum edge length
     *                          (width OR height)
     */
    public ImageHashGeneratorTask(String procId, String outputFileFormat, int checksumThreshold,
                                  int maxPixels) {
        super(procId);
        this.outputFileFormat = outputFileFormat;
        this.checksumThreshold = checksumThreshold;
        this.maxPixels = maxPixels;
    }

    @Override
    public String apply(Path p) throws Exception {
        logger.debug("Reading from file... {}", p);
        setOutputFileName(p.getFileName().toString());
        BufferedImage image = readImageFromDisk(p);

        String[] formatNames = ImageIO.getReaderFormatNames();
        logger.debug("Available format names: {}", formatNames);

        GenerateChecksumFunction generateChecksumFunction = new GenerateChecksumFunction(
                checksumThreshold);
        SaveFileFunction saveFileFunction = new SaveFileFunction(outputFileUrlSupplier,
                outputFileFormat);
        ScaleFunction scaleFunction = new ScaleFunction(maxPixels, BufferedImage.TYPE_BYTE_GRAY);
        ContrastFunction contrastFunction = new ContrastFunction();
        ColormodeFunction colormodeFunction = new ColormodeFunction(BufferedImage.TYPE_BYTE_GRAY);
        String checksum = colormodeFunction.andThen(contrastFunction).andThen(scaleFunction)
                .andThen(saveFileFunction).andThen(generateChecksumFunction).apply(image);
        logger.debug("Saved to file... [outputfile={}, checksum={}]", outputFileUrlSupplier.get(),
                checksum);

        if (doAfter != null) {
            doAfter.accept(this.outputFileUrlSupplier.get());
        }
        //this.deleteFile(this.outputFileUrlSupplier);

        return checksum;
    }


}
