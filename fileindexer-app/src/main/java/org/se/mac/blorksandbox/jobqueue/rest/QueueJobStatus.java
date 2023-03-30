package org.se.mac.blorksandbox.jobqueue.rest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

public class QueueJobStatus {

    private Long timestamp;

    public QueueJobStatusEntry[] getData() {
        return data;
    }

    static public class QueueJobStatusEntry {
        private long id;
        private int status;

        public QueueJobStatusEntry(Long key, Integer value) {
            this.id = id;
            this.status = value;
        }

        public long getId() {
            return id;
        }
        public int getStatus() {
            return status;
        }
    }

    private final QueueJobStatusEntry[] data;

    public QueueJobStatus(Map<Long, Integer> data) {
        this.timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        Collection<QueueJobStatusEntry> d = data.entrySet().stream()
                .map(element -> new QueueJobStatusEntry(element.getKey(), element.getValue()))
                .collect(Collectors.toList());
        this.data = new QueueJobStatusEntry[d.size()];
        d.toArray(this.data);
    }

    public Long getTimestamp() {
        return timestamp;
    }


}
