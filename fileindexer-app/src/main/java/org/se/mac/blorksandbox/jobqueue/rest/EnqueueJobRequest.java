package org.se.mac.blorksandbox.jobqueue.rest;

import java.util.Map;

public interface EnqueueJobRequest {
    String getJobTitle();

    void setJobTitle(String jobTitle);

    Map<String, String> getProperties();

    void setProperties(Map<String, String> properties);
}
