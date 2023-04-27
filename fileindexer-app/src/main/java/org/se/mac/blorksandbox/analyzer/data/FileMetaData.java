package org.se.mac.blorksandbox.analyzer.data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 *  Entity representing a file's meta-data.
 */
@Table
public class FileMetaData {

    @PrimaryKey
    private UUID id;
    private String devicePath;
    private UUID deviceId;
    private Instant updated_date;
    private long scanTime;
    private Map<String, String> properties;

    /**
     * Default constructor.
     *
     * @param id           Identifier for the entity
     * @param updated_date Creation/Update timestamp
     * @param deviceId     ID representing the distinct device where this file was indexed.
     * @param devicePath   Device path where this file was indexed.
     * @param properties   Custom properties for this entity
     * @param scanTime     Duration for processing the file's meta-data
     */
    public FileMetaData(UUID id, Instant updated_date, UUID deviceId, String devicePath,
                        Map<String, String> properties, long scanTime) {
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
