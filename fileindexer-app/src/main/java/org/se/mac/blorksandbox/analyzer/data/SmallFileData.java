package org.se.mac.blorksandbox.analyzer.data;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Entity representing a file with small binary content enclosed.
 */
@Table
public class SmallFileData {

    @PrimaryKey
    private UUID id;
    private String devicePath;
    private UUID deviceId;
    private Instant updated_date;
    private ByteBuffer blob;
    private String contentType;

    /**
     * Default constructor.
     *
     * @param id           Identifier for the entity
     * @param updated_date Creation/Update timestamp
     * @param deviceId     ID representing the distinct device where this file was indexed.
     * @param devicePath   Device path where this file was indexed.
     * @param blob         File's binary content
     * @param contentType  File content-type
     */
    public SmallFileData(UUID id, Instant updated_date, UUID deviceId, String devicePath,
                         ByteBuffer blob, String contentType) {
        this.id = id;
        this.updated_date = updated_date;
        this.deviceId = deviceId;
        this.devicePath = devicePath;
        this.blob = blob;
        this.contentType = contentType;
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

    public ByteBuffer getBlob() {
        return blob;
    }

    public void setBlob(ByteBuffer blob) {
        this.blob = blob;
    }
}
