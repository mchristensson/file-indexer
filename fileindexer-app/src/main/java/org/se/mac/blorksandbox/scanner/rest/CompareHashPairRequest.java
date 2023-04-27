package org.se.mac.blorksandbox.scanner.rest;

/**
 * Data carrier when requesting a hash comparison between two image entities.
 *
 * @param idA ID for en image entity
 * @param idB ID for en image entity
 */
public record CompareHashPairRequest(String idA, String idB) {
}

