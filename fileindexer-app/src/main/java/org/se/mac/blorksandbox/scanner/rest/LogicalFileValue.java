package org.se.mac.blorksandbox.scanner.rest;

import java.util.Map;

/**
 * Value representation of a logical file.
 *
 * @param id         ID for the entity
 * @param devicePath Logical path on the device
 * @param date       timestamp (create/update) for the entity
 * @param scanTime   Data processing duration in millis
 * @param deviceId   ID for the device entity
 * @param properties Custom detailed settings further defining the request
 */
public record LogicalFileValue(String id, String devicePath, java.time.Instant date, long scanTime,
                               String deviceId, Map<String, String> properties) {

}