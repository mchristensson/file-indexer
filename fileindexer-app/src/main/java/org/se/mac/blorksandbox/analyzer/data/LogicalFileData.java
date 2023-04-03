package org.se.mac.blorksandbox.analyzer.data;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Table
public class LogicalFileData {

    @PrimaryKey
    private UUID id;
    private String devicePath;
    private UUID deviceId;
    private Instant updated_date;
    private long scanTime;
    private Map<String, String> properties;

    public LogicalFileData(UUID id, Instant updated_date, UUID deviceId, String devicePath, Map<String, String> properties, long scanTime) {
        this.id = id;
        this.updated_date = updated_date;
        this.scanTime = scanTime;
        this.deviceId = deviceId;
        this.devicePath = devicePath;
        this.properties = properties;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public String getDevicePath() {
        return devicePath;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public Instant getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(Instant updated_date) {
        this.updated_date = updated_date;
    }

    public long getScanTime() {
        return scanTime;
    }
}
