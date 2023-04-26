package org.se.mac.blorksandbox.analyzer.task;

import org.se.mac.blorksandbox.analyzer.image.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


/**
 * Testing functionality mentioned in https://www.hackerfactor.com/blog/index.php?/archives/432-Looks-Like-It.html
 */
public class ImageClassifierTask extends AbstractImageAnalyzerTask {

    private static final Logger logger = LoggerFactory.getLogger(ImageClassifierTask.class);
    private final String outputFileFormat;
    private final int maxPixels;
    protected String outputScaledFileName;

    protected final Supplier<String> outputScaledFileUrlSupplier = () -> {
        if (isDebugMode()) {
            return ("./target/output_" + getProcId() + "_" + this.outputScaledFileName);
        } else {
            String tmpDir = System.getProperty("java.io.tmpdir");
            String sep = FileSystems.getDefault().getSeparator();
            tmpDir += tmpDir.endsWith(sep) ? "" : sep;
            return (tmpDir + getProcId() + sep + this.outputScaledFileName);
        }
    };

    protected void setOutputScaledFileName(String outputFileName) {
        this.outputScaledFileName = outputFileName;
    }

    /**
     * @param procId           Process identifier for this task
     * @param outputFileFormat Output file format (the saved file)
     * @param maxPixels        When scaling the image this value will be the maximum edge length (width OR height)
     */
    public ImageClassifierTask(String procId, String outputFileFormat, int maxPixels) {
        super(procId);
        this.outputFileFormat = outputFileFormat;
        this.maxPixels = maxPixels;
    }

    @Override
    public String apply(Path p) throws Exception {
        logger.debug("Reading from file... {}", p);
        logger.debug("Reading from file... {}", p.toFile().getAbsolutePath());

        setOutputFileName(p.getFileName().toString());
        setOutputScaledFileName(p.getFileName().toString() + ".scaled." + outputFileFormat);

        //Load image
        BufferedImage image = getImage(p);

        //Define masks
        List<int[]> masks = new ArrayList<>();
        masks.add(new int[]{
                1, 0,
                0, 1
        });
        masks.add(new int[]{
                0, 1,
                1, 1
        });
        masks.add(new int[]{
                1, 0,
                1, 0
        });
        masks.add(new int[]{
                0, 1,
                1, 0
        });


        //Define functions to apply
        GenerateConvValueFunction convValueFunction = new GenerateConvValueFunction(masks);
        SaveFileFunction saveFileFunction = new SaveFileFunction(outputFileUrlSupplier, outputFileFormat);
        SaveFileFunction saveScaledFileFunction = new SaveFileFunction(outputScaledFileUrlSupplier, outputFileFormat);
        ScaleFunction scaleFunction = new ScaleFunction(maxPixels, false);
        ContrastFunction contrastFunction = new ContrastFunction();
        ColormodeFunction colormodeFunction = new ColormodeFunction();
        colormodeFunction
                .andThen(contrastFunction)
                .andThen(scaleFunction)
                .andThen(saveScaledFileFunction)
                .andThen(convValueFunction)
                .andThen(saveFileFunction)
                .apply(image);
        logger.debug("Saved to file... [outputfile={}]", outputFileUrlSupplier.get());

        if (doAfter != null) {
            doAfter.accept(this.outputFileUrlSupplier.get());
        }
        //this.deleteFile(this.outputFileUrlSupplier);

        return "Hello world";
    }


}
