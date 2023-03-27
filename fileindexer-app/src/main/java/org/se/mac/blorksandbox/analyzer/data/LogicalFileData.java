package org.se.mac.blorksandbox.analyzer.data;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Map;
import java.util.UUID;

@Table
public class LogicalFileData {

    @PrimaryKey
    private UUID id;

    private Map<String, String> properties;

    public LogicalFileData(UUID id, Map<String, String> properties) {
        this.id = id;
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
}
