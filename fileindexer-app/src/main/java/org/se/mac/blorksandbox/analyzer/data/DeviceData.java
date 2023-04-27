package org.se.mac.blorksandbox.analyzer.data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Entity representing a physical device holding logical files.
 */
@Table("logicaldevice")
public class DeviceData {

    @PrimaryKey
    private UUID id;
    private String basePath;
    private Instant updated_date;
    private String title;
    private Map<String, String> properties;

    /**
     * Default constructor.
     *
     * @param id           Identifier for the entity
     * @param basePath     Device path (when applicable)
     * @param title        Device title
     * @param updated_date Creation/Update timestmp
     * @param properties   Custom properties for this entity
     */
    public DeviceData(UUID id, String basePath, String title, Instant updated_date, Map<String,
            String> properties) {
        this.id = id;
        this.basePath = basePath;
        this.title = title;
        this.updated_date = updated_date;
        this.properties = properties;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public Instant getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(Instant updated_date) {
        this.updated_date = updated_date;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}