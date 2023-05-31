package org.se.mac.blorksandbox.scanner.function;

import java.net.URI;
import java.util.function.BiFunction;
import org.se.mac.blorksandbox.scanner.model.UrlType;

/**
 * Builds up a URI from a local path (as string) and a {@link UrlType}.
 */
public class UriBuilder implements BiFunction<String, UrlType, URI> {

    @Override
    public URI apply(String s, UrlType urlType) {
        if (s == null || "".equals(s) || urlType == null) {
            return null;
        }
        return switch (urlType) {
            case UNIX, WIN_DRIVE_LETTER, WIN -> URI.create("file:///" + s.trim());
            case WIN_UNC -> URI.create(s.trim());
            default -> null;
        };
    }

}
