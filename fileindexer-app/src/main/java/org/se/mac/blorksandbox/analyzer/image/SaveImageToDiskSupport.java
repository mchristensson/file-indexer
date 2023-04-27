package org.se.mac.blorksandbox.analyzer.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

}
