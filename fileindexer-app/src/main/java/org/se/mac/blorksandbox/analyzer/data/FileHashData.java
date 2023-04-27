package org.se.mac.blorksandbox.analyzer.data;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Entity representing a file's hash value.
 */
@Table
public class FileHashData {

    @PrimaryKey
    private UUID id;
    private String devicePath;
    private UUID deviceId;
    private Instant updated_date;
    private long scanTime;
    private String hash;
    private UUID smallFileDataId;

    /**
     * Default constructor.
     *
     * @param id           Identifier for the entity
     * @param updated_date Creation/Update timestmp
     * @param deviceId     ID representing the distinct device where this file was indexed.
     * @param devicePath   Device path where this file was indexed.
     * @param hash         Hash value representing the file
     * @param scanTime     Duration for precoessing the hash value
     */
    public FileHashData(UUID id, Instant updated_date, UUID deviceId, String devicePath,
                        String hash, long scanTime) {
        this.id = id;
        this.updated_date = updated_date;
        this.scanTime = scanTime;
        this.deviceId = deviceId;
        this.devicePath = devicePath;
        this.hash = hash;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public UUID getSmallFileDataId() {
        return smallFileDataId;
    }

    public void setSmallFileDataId(UUID smallFileDataId) {
        this.smallFileDataId = smallFileDataId;
    }
}
