package org.se.mac.blorksandbox.jobqueue.rest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Map;

public class QueueJobStatus {

    private final Long timestamp;

    public QueueJobStatusEntry[] getData() {
        return data;
    }

    static public class QueueJobStatusEntry {
        private final long id;
        private final int status;

        public QueueJobStatusEntry(Long key, Integer value) {
            this.id = key;
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
                .toList();
        this.data = new QueueJobStatusEntry[d.size()];
        d.toArray(this.data);
    }

    public Long getTimestamp() {
        return timestamp;
    }

}
