package org.se.mac.blorksandbox.jobqueue.rest;

public class ScanRequestReceipt extends QueuedJobRequestReceipt {

    public ScanRequestReceipt(long id) {
        super(id);
    }

    public ScanRequestReceipt(long id, String message) {
        super(id, message);
    }
}
