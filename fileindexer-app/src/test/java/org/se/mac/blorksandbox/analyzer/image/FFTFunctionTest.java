package org.se.mac.blorksandbox.analyzer.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FFTFunctionTest {

    private BufferedImage squareImage;
    private BufferedImage nonSquareImage;
    private BufferedImage scalableImage;

    @BeforeEach
    public void loadTestImages() {
        Path testFile1 = Path.of("src/test/resources/images/misc/dice-3.png");
        Path testFile2 = Path.of("src/test/resources/images/misc/1-krona.png");
        Path testFile3 = Path.of("src/test/resources/images/misc/cats.jpg");
        try {
            this.squareImage = ImageIO.read(testFile1.toFile());
            this.nonSquareImage = ImageIO.read(testFile2.toFile());
            this.scalableImage = ImageIO.read(testFile3.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void apply_whenNonSquareImage_expectException() throws IOException {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            FFTFunction fftFunction = new FFTFunction(false);
            fftFunction.apply(nonSquareImage);
        });
        assertTrue(thrown.getMessage().contains("is not a power of"));
        assertTrue(thrown.getMessage().contains("consider padding for fix"));
    }

    @Test
    void apply_whenArgNotNull_expectImage() throws IOException {
        FFTFunction fftFunction = new FFTFunction(false);
        assertNotNull(fftFunction.apply(squareImage));
    }

    @Test
    void apply_whenArgNotNull_expectSavedImage() throws IOException {
        FFTFunction fftFunction = new FFTFunction(false);
        BufferedImage image = fftFunction.apply(squareImage);

        SaveFileFunction saveFileFunction = new SaveFileFunction(() -> ("./target/output_" +  "_" + "foosdjkadsk3.png"), "JPG");
        saveFileFunction.apply(image);

    }

    @Test
    void apply_whenScalableImage_expectSavedImage() throws IOException {
        ScaleFunction scaleFunction = new ScaleFunction(128, true, BufferedImage.TYPE_BYTE_GRAY);
        BufferedImage scaledImage = scaleFunction.apply(scalableImage);

        FFTFunction fftFunction = new FFTFunction(false);
        BufferedImage fftImage = fftFunction.apply(scaledImage);

        //ContrastFunction contrastFunction = new ContrastFunction();
        //BufferedImage contrastedFFTImage = contrastFunction.apply(fftImage);

        FFTFunction fftInverseFunction = new FFTFunction(true);
        BufferedImage fftInverseImage = fftInverseFunction.apply(fftImage);

        SaveFileFunction saveFileFunction = new SaveFileFunction(() -> ("./target/output_" +  "_" + "foosdjkadsk3_scclinv.png"), "JPG");
        saveFileFunction.apply(fftInverseImage);

    }
}