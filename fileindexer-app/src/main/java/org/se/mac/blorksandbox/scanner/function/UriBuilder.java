package org.se.mac.blorksandbox.scanner.function;

import org.se.mac.blorksandbox.scanner.model.UrlType;

import java.net.URI;
import java.util.Objects;
import java.util.function.BiFunction;

public class UriBuilder implements BiFunction<String, UrlType, URI> {

    @Override
    public URI apply(String s, UrlType urlType) {
        if (s == null || "".equals(s) || urlType == null) {
            return null;
        }
        switch (urlType) {
            case UNIX:
            case WIN_DRIVE_LETTER:
            case WIN:
                return URI.create("file:///" + s.trim());
            case WIN_UNC:
                return URI.create(s.trim());
            default:
                return null;
        }
    }

}
