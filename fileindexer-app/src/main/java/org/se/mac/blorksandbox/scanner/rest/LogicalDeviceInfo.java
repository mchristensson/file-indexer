package org.se.mac.blorksandbox.scanner.rest;

import java.util.Map;

public record LogicalDeviceInfo(String id, String devicePath, String title, java.time.Instant date, Map<String, String> properties) {
}
