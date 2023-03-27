package org.se.mac.blorksandbox.analyzer.task;

import com.drew.imaging.FileType;
import com.drew.imaging.FileTypeDetector;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.gif.GifMetadataReader;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.png.PngMetadataReader;
import com.drew.imaging.tiff.TiffMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.file.FileSystemDirectory;
import com.drew.metadata.jpeg.JpegCommentDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.drew.metadata.photoshop.PhotoshopDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class DummyAnalyzerTask implements FileAnalyzerTask<Map<String, String>> {

    public static final String SOFTWARE = "Software";
    public static final String IMAGE_WIDTH = "Image-height";
    public static final String IMAGE_HEIGHT = "Image-width";
    public static final String MAKE = "Make";
    public static final String MODEL = "Model";
    public static final String DATE_TIME = "Date/Time";
    public static final String FILE_SIZE = "File Size";
    public static final String FILE_NAME = "File Name";

    @Override
    public Map<String, String> apply(Path path) throws Exception {
        return getMetaDataMap(path.toFile());
    }

    private Map<String, String> getMetaDataMap(File file) throws ImageProcessingException, IOException {
        Map<String, String> map = getMetaDataMap(getMetaData(file).getDirectories());
        map.putIfAbsent(FILE_NAME, file.getName());
        map.putIfAbsent(FILE_SIZE, String.valueOf(Files.size(Paths.get(file.toURI()))));
        return map;
    }

    private Map<String, String> getMetaDataMap(Iterable<Directory> directories) {
        final Map<String, String> map = new HashMap<String, String>();
        directories.forEach(d -> getMetaData(map, d));
        return map;
    }

    private void getMetaData(final Map<String, String> map, Directory dic) {
        dic.getTags().iterator();
        if (dic.getClass().isAssignableFrom(ExifIFD0Directory.class)) {
            map.computeIfAbsent(MAKE, f -> dic.getString(ExifIFD0Directory.TAG_MAKE));
            map.computeIfAbsent(MODEL, f -> dic.getString(ExifIFD0Directory.TAG_MODEL));
            map.computeIfAbsent(DATE_TIME, f -> dic.getString(ExifIFD0Directory.TAG_DATETIME));
            map.computeIfAbsent(SOFTWARE, f -> dic.getString(ExifIFD0Directory.TAG_SOFTWARE));
        }
        if (dic.getClass().isAssignableFrom(PhotoshopDirectory.class)) {

        }
        if (dic.getClass().isAssignableFrom(JpegDirectory.class)) {
            map.computeIfAbsent(IMAGE_HEIGHT, f -> dic.getString(JpegDirectory.TAG_COMPONENT_DATA_1));
            map.computeIfAbsent(IMAGE_WIDTH, f -> dic.getString(JpegDirectory.TAG_COMPONENT_DATA_3));
        }
        if (dic.getClass().isAssignableFrom(FileSystemDirectory.class)) {
            map.computeIfAbsent(FILE_NAME, f -> dic.getString(FileSystemDirectory.TAG_FILE_NAME));
            map.computeIfAbsent(FILE_SIZE, f -> dic.getString(FileSystemDirectory.TAG_FILE_SIZE));
        }
    }

    private Metadata getMetaData(File file) throws ImageProcessingException, IOException {
        InputStream in = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(in);
        return getMetaData(bis);
    }

    private Metadata getMetaData(BufferedInputStream inputStream) throws IOException, ImageProcessingException {
        FileType fileType = FileTypeDetector.detectFileType(inputStream);
        Metadata metadata;
        switch (fileType) {
            case Gif:
                metadata = GifMetadataReader.readMetadata(inputStream);
                break;
            case Jpeg:
                metadata = JpegMetadataReader.readMetadata(inputStream);
                break;
            case Png:
                metadata = PngMetadataReader.readMetadata(inputStream);
                break;
            case Tiff:
                metadata = TiffMetadataReader.readMetadata(inputStream);
                break;
            default:
                throw new ImageProcessingException("Unsupported filetype to retrieve meta-data from.");
        }
        return metadata;
    }

}

