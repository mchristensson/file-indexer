package org.se.mac.blorksandbox.scanner.rest;

/**
 * Value representation of the result of a hash comparison between two image entities.
 *
 * @param result       Result value
 * @param message      Custom message
 * @param errorMessage Custom errorMessage (when applicable)
 */
public record CompareHashPairResponse(Integer result, String message, String errorMessage) {

}
