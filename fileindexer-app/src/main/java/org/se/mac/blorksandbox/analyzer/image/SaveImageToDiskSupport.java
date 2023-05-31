package org.se.mac.blorksandbox.analyzer.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;
import javax.imageio.ImageIO;

/**
 * Defines a procedure able to write a {@link BufferedImage} to disk.
 */
public class SaveImageToDiskSupport {

    /**
     * Saves a {@link BufferedImage} to disk.
     *
     * @param bufferedImage    Image to save.
     * @param location         Path
     * @param outputFileFormat Output file format (i.e. JPG)
     */
    public static void saveToLocalDisk(BufferedImage bufferedImage, final String location,
                                       String outputFileFormat) {
        File f = new File(location);
        if (!f.mkdirs()) {
            System.out.println("Directory was not created");
        }
        System.out.printf("Saving to file... [path=%s]%n", f.getAbsolutePath());
        try {
            ImageIO.write(bufferedImage, outputFileFormat, f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a file from disk.
     *
     * @param outputFileUrlSupplier Url supplier describing the location of the file
     */
    public static void deleteFile(Supplier<String> outputFileUrlSupplier) throws IOException {
        System.out.print("Try to delete the file");
        File f = new File(outputFileUrlSupplier.get());
        if (!f.exists()) {
            System.out.print("File does not exist");
        } else if (f.isDirectory()) {
            System.out.print("File is a directory");
        } else if (f.delete()) {
            System.out.print("File deleted");
        } else {
            throw new IOException("File could not be deleted");
        }
    }


    /**
     * Wrapping function for accessing a {@link BufferedImage} from disk with fast thrown
     * {@link IOException}'s upon errors.
     *
     * @param path Path reference for the file
     * @return The {@link BufferedImage} instance requested
     * @throws IOException Upon access related exceptions
     */
    public static BufferedImage readImageFromDisk(Path path) throws IOException {
        File f = path.toFile();
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

    /**
     * Returns a {@link BufferedImage} representation of a byte array assumed to represent an image.
     *
     * @param data   Byte content
     * @param width  Image output width
     * @param height Image output height
     * @return Image representation as {@link BufferedImage}
     * @throws IOException If transformation fail
     */
    public static BufferedImage getImage(byte[] data, int width, int height) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        image.setData(
                Raster.createRaster(image.getSampleModel(), new DataBufferByte(data, data.length),
                        new Point()));
        return image;
    }

    /**
     * @param blob   Blob with content
     * @param width  Image output width
     * @param height Image output height
     * @return Image representation as {@link BufferedImage}
     * @throws IOException If transformation fail
     * @see SaveImageToDiskSupport#getImage(byte[], int, int)
     */
    public static BufferedImage getImage(ByteBuffer blob, int width, int height) throws IOException {
        if (blob == null) {
            throw new IOException("Empty data");
        }
        return getImage(blob.array(), width, height);
    }

    /**
     * Extracts bytes from image.
     *
     * @param image Image to extract data from
     * @return byte-array
     * @throws IOException If content is not a databuffer of type {@code DataBufferByte}
     */
    public static byte[] getBytes(BufferedImage image) throws IOException {
        DataBuffer dataBuffer = image.getRaster().getDataBuffer();
        if (dataBuffer instanceof DataBufferByte) {
            return ((DataBufferByte) dataBuffer).getData();
        } else {
            throw new IOException("Unsupported bytes");
        }
    }

    public static String getTmpOutputPath(String pathDesignator, String fileName) {
        String tmpDir = System.getProperty("java.io.tmpdir");
        String sep = FileSystems.getDefault().getSeparator();
        tmpDir += tmpDir.endsWith(sep) ? "" : sep;
        return (tmpDir + pathDesignator + sep + fileName);
    }

    /**
     * Copy a file to tmp directory
     *
     * @param is           InputStream to use as source
     * @param fileName     Filename in the target directory (i.e. "foo.png")
     * @param deleteOnExit Whether the target file should be deleted on exit
     * @return The target {@link Path}
     * @throws IOException If copy operation fails.
     */
    public static Path copyToTmp(InputStream is, String fileName, boolean deleteOnExit) throws IOException {
        File tmpdir = new File(System.getProperty("java.io.tmpdir"));
        Path targetPath = Path.of(tmpdir.getPath(), fileName);
        Files.copy(is, targetPath);
        if (deleteOnExit) {
            targetPath.toFile().deleteOnExit();
        }
        return targetPath;
    }
}
