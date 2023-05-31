package org.se.mac.blorksandbox.analyzer.task;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import org.se.mac.blorksandbox.analyzer.image.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FFT Stuff
 */
public class ImageFFTTask extends AbstractImageAnalyzerTask {

    private static final Logger logger = LoggerFactory.getLogger(ImageFFTTask.class);
    private final String outputFileFormat;
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
    public ImageFFTTask(String procId, String outputFileFormat, int checksumThreshold,
                        int maxPixels) {
        super(procId);
        this.outputFileFormat = outputFileFormat;
        this.maxPixels = maxPixels;
    }

    @Override
    public String apply(Path p) throws Exception {
        logger.debug("Reading from file... {}", p);
        setOutputFileName(p.getFileName().toString());
        // Läs in den 64x64 pixlar stora bilden
        BufferedImage image = SaveImageToDiskSupport.readImageFromDisk(p);

        // konvertera den till gråskala om den inte redan är det.
        ColormodeFunction colormodeFunction = new ColormodeFunction(BufferedImage.TYPE_BYTE_GRAY);

        // Skala upp bilden till en storlek som är en effekt av 2
        ScaleFunction scaleFunction = new ScaleFunction(maxPixels, false, BufferedImage.TYPE_BYTE_GRAY);

        //Konvertera den skalade bilden till en tvådimensionell matris av flyttal (float)
        //TODO:

        //Använd Fourier-transformen för att omvandla matrisen från tidsdomänen till frekvensdomänen. Apache Commons Math har stöd för detta med
        FFTFunction imageFFTTask = new FFTFunction(false);


        // Spara eller visa den genererade frekvensplanbilden.
        SaveFileFunction saveFileFunction = new SaveFileFunction(outputFileUrlSupplier,
                outputFileFormat);

        colormodeFunction.andThen(scaleFunction)
                .andThen(saveFileFunction).andThen(imageFFTTask).apply(image);
        logger.debug("Saved to file... [outputfile={}]", outputFileUrlSupplier.get());

        if (doAfter != null) {
            doAfter.accept(this.outputFileUrlSupplier.get());
        }
        SaveImageToDiskSupport.deleteFile(this.outputFileUrlSupplier);

        return "DONE";
    }


}
