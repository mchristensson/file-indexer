package org.se.mac.blorksandbox.jobqueue.rest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Map;

/**
 * Data carrier (wrapper) for holding status values for specific tasks.
 */
public class QueueJobStatus {

    private final Long timestamp;

    /**
     * Returns task status data.
     *
     * @return Task status data
     */
    public QueueJobStatusEntry[] getData() {
        return data;
    }

    /**
     * Entry value holding status value for a specific task.
     */
    public static class QueueJobStatusEntry {
        private final long id;
        private final int status;
        private final String message;

        /**
         * Default constructor.
         *
         * @param key     Key
         * @param value   Value
         * @param message Custom message
         */
        public QueueJobStatusEntry(Long key, Integer value, String message) {
            this.id = key;
            this.status = value;
            this.message = message;
        }

        public long getId() {
            return id;
        }

        public int getStatus() {
            return status;
        }
    }

    private final QueueJobStatusEntry[] data;

    /**
     * Default constructor.
     *
     * @param data Meta-data about the status
     */
    public QueueJobStatus(Map<Long, Integer> data) {
        this.timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        Collection<QueueJobStatusEntry> d =
                data.entrySet().stream().map(element -> new QueueJobStatusEntry(element.getKey(),
                        element.getValue(), "todo")).toList();
        this.data = new QueueJobStatusEntry[d.size()];
        d.toArray(this.data);
    }

    public Long getTimestamp() {
        return timestamp;
    }

}
