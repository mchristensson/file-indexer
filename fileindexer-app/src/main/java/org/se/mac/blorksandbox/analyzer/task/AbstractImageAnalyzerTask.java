package org.se.mac.blorksandbox.analyzer.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractImageAnalyzerTask implements FileAnalyzerTask<String> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractImageAnalyzerTask.class);

    protected Consumer<String> doAfter;
    private boolean debugMode;
    protected String outputFileName;
    private String procId;

    public AbstractImageAnalyzerTask(String procId) {
        this.procId = procId == null? UUID.randomUUID().toString() : procId;
    }

    public static BufferedImage getImage(Path p) throws IOException {
        File f = p.toFile();
        if (!f.exists()) {
            throw new IOException("File does not exist");
        } else if (!f.canRead()) {
            throw new IOException("Cannot read from file");
        }
        BufferedImage image = ImageIO.read(f);
        if (image == null) {
            throw new IOException("Invalid file content");
        }
        return image;
    }

    @Override
    public void setDoAfter(Consumer<String> filePathConsumer) {
        this.doAfter = filePathConsumer;
    }

    protected final Supplier<String> outputFileUrlSupplier = () -> {
        if (debugMode) {
            return ("./target/output_" + procId + "_" + this.outputFileName);
        } else {
            String tmpDir = System.getProperty("java.io.tmpdir");
            tmpDir += tmpDir.endsWith("/") ? "" : "/";
            return (tmpDir + procId + "/" + this.outputFileName);
        }
    };

    protected void deleteFile(Supplier<String> outputFileUrlSupplier) {
        logger.debug("Try to delete the file");
        File f = new File(outputFileUrlSupplier.get());
        if (!f.exists()) {
            logger.warn("File does not exist");
        } else if (f.isDirectory()) {
            logger.warn("File is a directory");
        } else if (f.delete()) {
            logger.info("File deleted");
        } else {
            logger.error("File could not be deleted");
        }
    }

    /**
     * Hash from each image and count the number of bit positions that are different.
     *
     * @param a Hash from image A
     * @param b Hash from image B
     * @return number of bit positions that are different
     */
    public static int hammingDistance(long a, long b) {
        int dist = 0;

        // The ^ operators sets to 1 only the bits that are different
        for (long val = a ^ b; val > 0; ++dist) {
            // We then count the bit set to 1 using the Peter Wegner way
            val = val & (val - 1); // Set to zero val's lowest-order 1
        }

        // Return the number of differing bits
        logger.debug("Hammin distance... [a={}, b={}, distance={}]", a, b, dist);
        return dist;
    }

    /**
     * Hash from two String with null check
     *
     * @param a first value
     * @param b second value
     * @return calculated hamming distance
     * @see #hammingDistance(long, long)
     */
    public static int hammingDistance(String a, String b) {
        if (a != null && b != null) {
            long r1 = Long.parseUnsignedLong(a, 16);
            long r2 = Long.parseUnsignedLong(b, 16);
            return hammingDistance(r1, r2);
        } else {
            return -1;
        }
    }

    public AbstractImageAnalyzerTask setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }

}
