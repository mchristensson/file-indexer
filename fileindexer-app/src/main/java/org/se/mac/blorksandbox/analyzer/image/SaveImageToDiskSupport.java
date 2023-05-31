package org.se.mac.blorksandbox.analyzer.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;
import javax.imageio.ImageIO;

/**
 * Defines a procedure able to write a {@link BufferedImage} to disk.
 */
public interface SaveImageToDiskSupport {

    /**
     * Saves a {@link BufferedImage} to disk.
     *
     * @param bufferedImage    Image to save.
     * @param location         Path
     * @param outputFileFormat Output file format (i.e. JPG)
     */
    default void saveToLocalDisk(BufferedImage bufferedImage, final String location,
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
    default void deleteFile(Supplier<String> outputFileUrlSupplier) throws IOException {
        System.out.printf("Try to delete the file");
        File f = new File(outputFileUrlSupplier.get());
        if (!f.exists()) {
            System.out.printf("File does not exist");
        } else if (f.isDirectory()) {
            System.out.printf("File is a directory");
        } else if (f.delete()) {
            System.out.printf("File deleted");
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
    default BufferedImage readImageFromDisk(Path path) throws IOException {
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

}
