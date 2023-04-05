package org.se.mac.blorksandbox.rest;

import org.se.mac.blorksandbox.scanner.rest.LogicalFileValue;

/**
 * Value object for search result holding file-info
 *
 * @param values search result entries
 */
public record LogicalFilesSearchResult(LogicalFileValue[] values) {

}
