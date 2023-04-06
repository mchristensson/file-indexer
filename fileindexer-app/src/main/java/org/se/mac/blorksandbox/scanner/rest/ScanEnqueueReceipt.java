package org.se.mac.blorksandbox.scanner.rest;

import org.se.mac.blorksandbox.jobqueue.rest.QueuedJobRequestReceipt;

//TODO: Make this a record instead
public class ScanEnqueueReceipt extends QueuedJobRequestReceipt {

    public ScanEnqueueReceipt(long id) {
        super(id);
    }

    public ScanEnqueueReceipt(long id, String message) {
        super(id, message);
    }
}
