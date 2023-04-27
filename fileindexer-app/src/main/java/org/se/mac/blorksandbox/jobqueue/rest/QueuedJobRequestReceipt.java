package org.se.mac.blorksandbox.jobqueue.rest;

/**
 * Data carrier for the ack-value from backend when enqueuing a task.
 */
public class QueuedJobRequestReceipt {

    private final long id;
    private String errorMessage;
    private String message;

    public QueuedJobRequestReceipt(long id) {
        this.id = id;
    }

    /**
     * Default constructor.
     *
     * @param id      ID for the task
     * @param message Custom message
     */
    public QueuedJobRequestReceipt(long id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
