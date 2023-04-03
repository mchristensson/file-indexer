package org.se.mac.blorksandbox.analyzer.task;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.*;
import com.drew.metadata.file.FileSystemDirectory;
import com.drew.metadata.file.FileTypeDirectory;
import com.drew.metadata.iptc.IptcDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.drew.metadata.photoshop.PhotoshopDirectory;
import com.drew.metadata.png.PngDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;


public class ImageAnalyzerTask implements FileAnalyzerTask<Map<String, String>> {

    private static final Logger logger = LoggerFactory.getLogger(ImageAnalyzerTask.class);
    public static final String SOFTWARE = "Software";
    public static final String IMAGE_WIDTH = "Image-width";
    public static final String IMAGE_HEIGHT = "Image-height";
    public static final String MAKE = "Make";
    public static final String MODEL = "Model";
    public static final String DATE_TIME = "Date/Time";
    public static final String FILE_SIZE = "File Size";
    public static final String FILE_NAME = "File Name";
    public static final String MIME_TYPE = "mime-type";
    public static final String DATE_TIME_ORIGINAL = "Date/Time Original";

    @Override
    public Map<String, String> apply(Path path) throws Exception {
        File f = path.toFile();
        if (!f.canRead()) {
            throw new IOException("Not granted read access to file");
        }
        return getMetaDataMap(f);
    }

    private Map<String, String> getMetaDataMap(File file) throws ImageProcessingException, IOException {
        Metadata md = getMetaData(file);
        Map<String, String> map = getMetaDataMap(md.getDirectories());
        map.putIfAbsent(FILE_NAME, file.getName());

        Path f = Paths.get(file.toURI());
        BasicFileAttributes attrs = Files.readAttributes(f, BasicFileAttributes.class);
        map.putIfAbsent(FILE_SIZE, String.valueOf(attrs.size()));
        map.putIfAbsent(DATE_TIME, attrs.creationTime().toString());
        return map;
    }

    private Map<String, String> getMetaDataMap(Iterable<Directory> directories) {
        final Map<String, String> map = new HashMap<>();
        directories.forEach(d -> getMetaData(map, d));
        return map;
    }

    private void getMetaData(final Map<String, String> map, Directory dic) {

        if (dic.getClass().isAssignableFrom(PngDirectory.class)) {
            map.computeIfAbsent(IMAGE_WIDTH, f -> dic.getString(PngDirectory.TAG_IMAGE_WIDTH));
            map.computeIfAbsent(IMAGE_HEIGHT, f -> dic.getString(PngDirectory.TAG_IMAGE_HEIGHT));

        }

        if (dic.getClass().isAssignableFrom(ExifIFD0Directory.class)) {
            map.computeIfAbsent(MAKE, f -> dic.getString(ExifIFD0Directory.TAG_MAKE));
            map.computeIfAbsent(MODEL, f -> dic.getString(ExifIFD0Directory.TAG_MODEL));
            map.computeIfAbsent(DATE_TIME, computeExifLocalDateTime(dic));
            map.computeIfAbsent(SOFTWARE, f -> dic.getString(ExifIFD0Directory.TAG_SOFTWARE));

        } else if (dic.getClass().isAssignableFrom(ExifImageDirectory.class)) {
            map.computeIfAbsent(DATE_TIME_ORIGINAL, computeExifZonedDateTime(dic));

        } else if (dic.getClass().isAssignableFrom(ExifSubIFDDirectory.class)) {
            map.computeIfAbsent(DATE_TIME_ORIGINAL, computeExifZonedDateTime(dic));

        }

        if (dic.getClass().isAssignableFrom(ExifThumbnailDirectory.class)) {
            logger.warn("Non supported ExifDescriptorBase");
        }

        if (dic.getClass().isAssignableFrom(IptcDirectory.class)) {
            map.computeIfAbsent(DATE_TIME_ORIGINAL, computeItpcDateTime(dic));
        }

        if (dic.getClass().isAssignableFrom(PhotoshopDirectory.class)) {
            logger.warn("Non supported PhotoshopDirectory");
        }

        if (dic.getClass().isAssignableFrom(JpegDirectory.class)) {
            map.computeIfAbsent(IMAGE_HEIGHT, f -> dic.getString(JpegDirectory.TAG_IMAGE_HEIGHT));
            map.computeIfAbsent(IMAGE_WIDTH, f -> dic.getString(JpegDirectory.TAG_IMAGE_WIDTH));
        }

        if (dic.getClass().isAssignableFrom(FileSystemDirectory.class)) {
            map.computeIfAbsent(FILE_NAME, f -> dic.getString(FileSystemDirectory.TAG_FILE_NAME));
            map.computeIfAbsent(FILE_SIZE, f -> dic.getString(FileSystemDirectory.TAG_FILE_SIZE));
        } else {
            logger.debug("Not assignable from FileSystemDirectory");
        }

        if (dic.getClass().isAssignableFrom(FileTypeDirectory.class)) {
            map.computeIfAbsent(MIME_TYPE, f -> dic.getString(FileTypeDirectory.TAG_DETECTED_FILE_MIME_TYPE));
        }
    }

    private static Function<String, String> computeExifLocalDateTime(Directory dic) {
        return f -> {
            String s = dic.getString(ExifDirectoryBase.TAG_DATETIME);
            if (s != null && !"".equals(s.trim())) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
                return LocalDateTime
                        .parse(s.trim(), dateTimeFormatter)
                        .format(DateTimeFormatter.ISO_DATE_TIME);
            }
            return null;
        };
    }

    private static Function<String, String> computeExifZonedDateTime(Directory dic) {
        return f -> {

            String s = dic.getString(ExifDirectoryBase.TAG_DATETIME_ORIGINAL);
            String z = dic.getString(ExifDirectoryBase.TAG_TIME_ZONE_ORIGINAL);
            int n = -1;
            if (z != null) {
                String ns = "";
                for (char c : z.toCharArray()) {
                    if (Character.isDigit(c)) {
                        ns += c;
                    }
                }
                if (ns.length() > 0) {
                    n = Integer.parseInt(ns);
                }
            }

            if (s != null && !"".equals(s.trim())) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
                return LocalDateTime
                        .parse(s.trim(), dateTimeFormatter)
                        .atOffset(n > -1? ZoneOffset.ofHours(2) : ZoneOffset.UTC )
                        .format(DateTimeFormatter.ISO_DATE_TIME);
            }
            return null;

        };
    }

    public static Function<String, String> computeItpcDateTime(Directory dic) {
        return f -> {
            String a = dic.getString(IptcDirectory.TAG_DATE_CREATED);
            String b = dic.getString(IptcDirectory.TAG_TIME_CREATED);
            if (a == null && null == b) {
                return null;
            } else if (b == null || "".equals(b.trim())) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                return LocalDate
                        .parse((a).trim(), dateTimeFormatter)
                        .format(DateTimeFormatter.ISO_DATE);
            } else if (a == null || "".equals(a.trim())) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HHmmssZ");
                return LocalTime
                        .parse((b).trim(), dateTimeFormatter)
                        .format(DateTimeFormatter.ISO_TIME);
            } else {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmssZ");
                return ZonedDateTime
                        .parse((a + " " + b).trim(), dateTimeFormatter)
                        .format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
            }
        };
    }

    private Metadata getMetaData(File file) throws ImageProcessingException, IOException {
        InputStream in = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(in);
        return ImageMetadataReader.readMetadata(bis);
    }

}

