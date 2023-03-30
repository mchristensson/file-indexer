package org.se.mac.blorksandbox.scanner.function;

import org.junit.jupiter.api.Test;
import org.se.mac.blorksandbox.scanner.model.UrlType;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class UriBuilderTest {

    /*

    public static URI buildURI() {
        //QueuedJob scanJob = new FileScannerJob("file:///opt/app/test-filestructure", UrlType.UNIX, scannerService);
        //QueuedJob scanJob = new FileScannerJob("file:///c:/temp", UrlType.WIN_DRIVE_LETTER, scannerService);
        return URI.create(path);
    }
     */

    private UriBuilder uriBuilder = new UriBuilder();

    @Test
    void apply_whenNullArgs_expectNull() {
        String path = null;
        UrlType type = null;

        URI output = uriBuilder.apply(path, type);
        assertNull(output);
    }

    @Test
    void apply_whenPathArgNull_expectNull() {
        String path = null;
        UrlType type = UrlType.UNIX;

        URI output = uriBuilder.apply(path, type);
        assertNull(output);
    }

    @Test
    void apply_whenTypeArgNull_expectNull() {
        String path = "";
        UrlType type = null;

        URI output = uriBuilder.apply(path, type);
        assertNull(output);
    }

    @Test
    void apply_whenTypeArgUndefined_expectNull() {
        String path = null;
        UrlType type = UrlType.UNDEFINED;

        URI output = uriBuilder.apply(path, type);
        assertNull(output);
    }

    @Test
    void apply_whenEmptyPath_expectNull() {
        String path = "";
        UrlType type = UrlType.UNIX;

        URI output = uriBuilder.apply(path, type);
        assertNull(output);
    }

    @Test
    void apply_whenDirectoryPath_expectAbsoluteFileProtocolPath() {
        String path = "apa";
        UrlType type = UrlType.UNIX;

        URI output = uriBuilder.apply(path, type);
        assertEquals("file:///apa", output.toString());
    }

    @Test
    void apply_whenMultiLevelDirectoryPath_expectAbsoluteFileProtocolPath() {
        String path = "apa/def/";
        UrlType type = UrlType.UNIX;

        URI output = uriBuilder.apply(path, type);
        assertEquals("file:///apa/def/", output.toString());
    }

    @Test
    void apply_whenMultiLevelFilePath_expectAbsoluteFileProtocolPath() {
        String path = "apa/def/myfile.txt";
        UrlType type = UrlType.UNIX;

        URI output = uriBuilder.apply(path, type);
        assertEquals("file:///apa/def/myfile.txt", output.toString());
        //assertEquals("file:///abc/def/myfile.txt", output.toString());
    }

    @Test
    void apply_whenTypeUnix_expectAbsoluteFileProtocolPath() {
        String path = "apa/def/myfile.txt";
        UrlType type = UrlType.UNIX;

        URI output = uriBuilder.apply(path, type);
        assertEquals("file:///apa/def/myfile.txt", output.toString());
    }

    @Test
    void apply_whenTypeWinUnc_expectAbsoluteFileProtocolPath() {
        String path = "apa/def/myfile.txt";
        UrlType type = UrlType.WIN_UNC;

        URI output = uriBuilder.apply(path, type);
        assertEquals("apa/def/myfile.txt", output.toString());
    }

    @Test
    void apply_whenTypeWinDriveLetter_expectAbsoluteFileProtocolPath() {
        String path = "c:/apa/def/myfile.txt";
        UrlType type = UrlType.WIN_DRIVE_LETTER;

        URI output = uriBuilder.apply(path, type);
        assertEquals("file:///c:/apa/def/myfile.txt", output.toString());
    }


}