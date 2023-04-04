package org.se.mac.blorksandbox.analyzer.task;

import com.drew.lang.annotations.NotNull;
import org.se.mac.blorksandbox.analyzer.image.ContrastFunction;
import org.se.mac.blorksandbox.analyzer.image.GenerateChecksumFunction;
import org.se.mac.blorksandbox.analyzer.image.SaveFileFunction;
import org.se.mac.blorksandbox.analyzer.image.ScaleFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.IOException;
import java.nio.file.Path;


/**
 * Testing functionality mentioned in https://www.hackerfactor.com/blog/index.php?/archives/432-Looks-Like-It.html
 */
public class ImageHashGeneratorTask implements FileAnalyzerTask<String> {

    private static final Logger logger = LoggerFactory.getLogger(ImageHashGeneratorTask.class);
    private final String outputFileFormat;
    private final int checksumThreshold;
    private final boolean grayscale;

    private final int maxPixels;

    public ImageHashGeneratorTask(@NotNull String outputFileFormat, int checksumThreshold, boolean grayscale, int maxPixels) {
        this.outputFileFormat = outputFileFormat;
        this.checksumThreshold = checksumThreshold;
        this.grayscale = grayscale;
        this.maxPixels = maxPixels;
    }

    @Override
    public String apply(Path p) throws Exception {
        logger.debug("Reading from file... {}", p);
        BufferedImage image = ImageIO.read(p.toFile());

        String[] formatNames = ImageIO.getReaderFormatNames();

        GenerateChecksumFunction generateChecksumFunction = new GenerateChecksumFunction(checksumThreshold);
        SaveFileFunction saveFileFunction = new SaveFileFunction(p.getFileName().toString(), outputFileFormat);
        ScaleFunction scaleFunction = new ScaleFunction(maxPixels, grayscale);
        ContrastFunction contrastFunction = new ContrastFunction();
        String checksum = contrastFunction
                .andThen(scaleFunction)
                .andThen(saveFileFunction)
                .andThen(generateChecksumFunction)
                .apply(image);
        logger.debug("Saved to file... [outputfile={}, checksum={}]", saveFileFunction.getOutputFile(), checksum);
        return checksum;
    }




    /*
    Reduce color. The image is reduced to a grayscale just to further simplify the number of computations.
Compute the DCT. The DCT separates the image into a collection of frequencies and scalars. While JPEG uses an 8x8 DCT, this algorithm uses a 32x32 DCT.
Reduce the DCT. While the DCT is 32x32, just keep the top-left 8x8. Those represent the lowest frequencies in the picture.
Compute the average value. Like the Average Hash, compute the mean DCT value (using only the 8x8 DCT low-frequency values and excluding the first term since the DC coefficient can be significantly different from the other values and will throw off the average). Thanks to David Starkweather for the added information about pHash. He wrote: "the dct hash is based on the low 2D DCT coefficients starting at the second from lowest, leaving out the first DC term. This excludes completely flat image information (i.e. solid colors) from being included in the hash description."
Further reduce the DCT. This is the magic step. Set the 64 hash bits to 0 or 1 depending on whether each of the 64 DCT values is above or below the average value. The result doesn't tell us the actual low frequencies; it just tells us the very-rough relative scale of the frequencies to the mean. The result will not vary as long as the overall structure of the image remains the same; this can survive gamma and color histogram adjustments without a problem.
Construct the hash. Set the 64 bits into a 64-bit integer. The order does not matter, just as long as you are consistent. To see what this fingerprint looks like, simply set the values (this uses +255 and -255 based on whether the bits are 1 or 0) and convert from the 32x32 DCT (with zeros for the high frequencies) back into the 32x32 image:
 = 8a0303f6df3ec8cd
At first glance, this might look like some random blobs... but look closer. There is a dark ring around her head and the dark horizontal line in the background (right side of the picture) appears as a dark spot.
As with the Average Hash, pHash values can be compared using the same Hamming distance algorithm. (Just compare each bit position and count the number of differences.)
     */


    //BLOCKHASH

    //DIFFERENCE HASH

    //MEDIAN HASH

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
        logger.debug("Hammin distance... [a={}, b={}, distance={}]", a,b,dist);
        return dist;
    }
}
