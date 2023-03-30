package org.se.mac.blorksandbox.scanner.rest;

import org.se.mac.blorksandbox.jobqueue.rest.QueuedJobRequestReceipt;

public class ScanEnqueueReceipt extends QueuedJobRequestReceipt {

    public ScanEnqueueReceipt(long id) {
        super(id);
    }

    public ScanEnqueueReceipt(long id, String message) {
        super(id, message);
    }
}
