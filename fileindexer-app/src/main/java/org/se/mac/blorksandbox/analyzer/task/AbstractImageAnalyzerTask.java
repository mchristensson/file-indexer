package org.se.mac.blorksandbox.analyzer.task;

import java.nio.file.FileSystems;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.se.mac.blorksandbox.analyzer.image.SaveImageToDiskSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task for analysizing/processing an image.
 */
public abstract class AbstractImageAnalyzerTask implements FileAnalyzerTask<String> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractImageAnalyzerTask.class);

    protected Consumer<String> doAfter;
    private boolean debugMode;
    private String outputFileName;
    private String procId;

    public AbstractImageAnalyzerTask(String procId) {
        this.procId = procId == null ? UUID.randomUUID().toString() : procId;
    }

    @Override
    public void setDoAfter(Consumer<String> filePathConsumer) {
        this.doAfter = filePathConsumer;
    }

    protected final Supplier<String> outputFileUrlSupplier = () -> {
        if (debugMode) {
            return ("./target/output_" + procId + "_" + this.outputFileName);
        } else {
            return SaveImageToDiskSupport.getTmpOutputPath(getProcId(), this.outputFileName);
        }
    };

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
        logger.trace("Hamming distance... [a={}, b={}, distance={}]", a, b, dist);
        return dist;
    }

    /**
     * Hash from two String with null check.
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

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public String getProcId() {
        return this.procId;
    }

    protected void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }
}
