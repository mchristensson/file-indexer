package org.se.mac.blorksandbox.jobqueue.rest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

public class QueueJobStatus {

    private Long timestamp;

    private Map<Long, Integer> data;

    public QueueJobStatus(Map<Long, Integer> data) {
        this.timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Map<Long, Integer> getData() {
        return data;
    }
}
