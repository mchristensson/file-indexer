package org.se.mac.blorksandbox.jobqueue.rest;

public class QueuedJobRequestReceipt {

    private final long id;
    private String errorMessage;
    private String message;

    public QueuedJobRequestReceipt(long id) {
        this.id = id;
    }
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
