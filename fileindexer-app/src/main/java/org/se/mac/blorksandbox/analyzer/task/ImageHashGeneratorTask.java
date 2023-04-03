package org.se.mac.blorksandbox.analyzer.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class ImageHashGeneratorTask implements FileAnalyzerTask<String> {

    private static final Logger logger = LoggerFactory.getLogger(ImageHashGeneratorTask.class);

    @Override
    public String apply(Path t) throws Exception {
        int maxPixels = 8;
        reduceSize(t, maxPixels, true);
        return "hello world";
    }

    //AVERAGE HASH

    /**
     * Reduce size. Like Average Hash, pHash starts with a small image. However, the image is
     * larger than 8x8; 32x32 is a good size. This is really done to simplify the DCT computation
     * and not because it is needed to reduce the high frequencies.
     */
    public static void reduceSize(Path p, int maxPixels, boolean grayscale) throws IOException {
        logger.debug("Reading from file... {}", p);
        BufferedImage image = ImageIO.read(p.toFile());

        int h = image.getHeight();
        int w = image.getWidth();
        logger.info("Original Size [width={}, height={}, type={}, grayscale={}]", h, w, image.getType(), grayscale);
        if (h >= w) {
            //Limit Height primarily
            h = h * maxPixels / h;
            w = w * maxPixels / h;
        } else {
            // Limit width
            h = h * maxPixels / w;
            w = w * maxPixels / w;
        }
        logger.debug("     New Size [width={}, height={}]", h, w);


        BufferedImage scaled = new BufferedImage(w, h, grayscale ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = scaled.createGraphics();
        graphics2D.drawImage(image, 0, 0, w, h, null);

        int[] rgbArray = new int[w * h];
        Integer avg = getRGB(scaled.getColorModel(), scaled.getRaster(), w, h, rgbArray);
        for (int i = 0; i < rgbArray.length; i++) {
//TODO: https://www.hackerfactor.com/blog/index.php?/archives/432-Looks-Like-It.html
        }


        graphics2D.dispose();

        File f = new File("./target/output_" + System.currentTimeMillis() + p.getFileName());
        logger.debug("Saving to file... {}", f.getAbsolutePath());
        ImageIO.write(scaled, "JPG", f);
    }


    public static Integer getRGB(ColorModel colorModel, WritableRaster raster, int w, int h, final int[] rgbArray) {
        int off;
        Object data;
        int nbands = raster.getNumBands();
        int dataType = raster.getDataBuffer().getDataType();
        switch (dataType) {
            case DataBuffer.TYPE_BYTE:
                data = new byte[nbands];
                break;
            case DataBuffer.TYPE_USHORT:
                data = new short[nbands];
                break;
            case DataBuffer.TYPE_INT:
                data = new int[nbands];
                break;
            case DataBuffer.TYPE_FLOAT:
                data = new float[nbands];
                break;
            case DataBuffer.TYPE_DOUBLE:
                data = new double[nbands];
                break;
            default:
                throw new IllegalArgumentException("Unknown data buffer type: " +
                        dataType);
        }

        int sum = 0;
        for (int y = 0; y < h; y++) {
            off = y * w;
            for (int x = 0; x < w; x++) {
                int val =
                        colorModel.getRGB(raster.getDataElements(x,
                                y,
                                data)) & 0xff;
                rgbArray[off++] = val;
                sum += val;
            }
        }
        return sum / (w * h);

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
    public static float hammingDistance(String a, String b) {
        return 0.0f;
    }
}
