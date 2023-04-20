package org.se.mac.blorksandbox.analyzer.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface SaveImageToDiskSupport {

    default void saveToLocalDisk(BufferedImage bufferedImage, final String location, String outputFileFormat) {
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
