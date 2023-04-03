package org.se.mac.blorksandbox.scanner.rest;

import java.util.Map;

public record LogicalFileValue(String id, String devicePath, java.time.Instant date, long scanTime, String deviceId, Map<String, String> properties) {

}