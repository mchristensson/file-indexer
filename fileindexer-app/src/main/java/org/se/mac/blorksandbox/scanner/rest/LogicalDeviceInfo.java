package org.se.mac.blorksandbox.scanner.rest;

import java.util.Map;

/**
 * Value representation of a logical device.
 *
 * @param id         ID for the entity
 * @param devicePath Absolte path definition for device (optional)
 * @param title      Title for the device
 * @param date       timestamp (create/update) for the entity
 * @param properties Custom detailed settings further defining the entity
 */
public record LogicalDeviceInfo(String id, String devicePath, String title, java.time.Instant date,
                                Map<String, String> properties) {
}
