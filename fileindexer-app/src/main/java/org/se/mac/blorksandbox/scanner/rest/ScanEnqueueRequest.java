package org.se.mac.blorksandbox.scanner.rest;

import java.util.Map;

/**
 * Data carrier when enqueuing a queued task.
 *
 * <p>Example:
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
 *
 * @param jobTitle Title of task name to invoke
 * @param settings Custom detailed settings further defining the request
 */
public record ScanEnqueueRequest(String jobTitle, Map<String, String> settings) {

}
