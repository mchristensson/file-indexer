package org.se.mac.blorksandbox.scanner.rest;

import java.util.Map;

/**
 *
 * <p>
 * Example:
 * <pre>
 *     {
 *     "jobTitle": "DirectoryScanning",
 *     "settings" : {
 *         "devicePath": "c:/temp/misc",
 *         "urlType": "WIN_DRIVE_LETTER",
 *         "deviceId" : "7f800e14-47f0-4ca3-8010-499bd70cd569"
 *     }
 * }
 * </pre>
 * @param jobTitle
 * @param settings
 */
public record ScanEnqueueRequest(String jobTitle, Map<String, String> settings) {

}
