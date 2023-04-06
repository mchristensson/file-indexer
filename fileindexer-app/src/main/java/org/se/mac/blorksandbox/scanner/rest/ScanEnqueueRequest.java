package org.se.mac.blorksandbox.scanner.rest;

import org.se.mac.blorksandbox.jobqueue.rest.EnqueueJobRequest;
import org.se.mac.blorksandbox.scanner.model.UrlType;

import java.util.UUID;

//TODO: Make this a record instead
public class ScanEnqueueRequest implements EnqueueJobRequest {
    private String path;
    private String type;
    private String deviceId;

    public String getPath() {
        return this.path;
    }

    public UrlType getUrlType() {
        try {
            return UrlType.valueOf(this.type);
        } catch (IllegalArgumentException e) {
            return UrlType.UNDEFINED;
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getDeviceIdAsUUID() {
        return UUID.fromString(this.deviceId);
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
