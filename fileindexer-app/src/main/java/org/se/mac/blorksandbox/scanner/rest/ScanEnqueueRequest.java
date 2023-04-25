package org.se.mac.blorksandbox.scanner.rest;

import java.util.Map;

public record ScanEnqueueRequest(String jobTitle, Map<String, String> properties) {

}
