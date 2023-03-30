package org.se.mac.blorksandbox.scanner.rest;

import java.util.List;

public class LogicalFileValues {

    private List<LogicalFileValue> names;

    public List<LogicalFileValue> getNames() {
        return names;
    }

    public void setNames(List<LogicalFileValue> names) {
        this.names = names;
    }

    public void add(LogicalFileValue value) {
        this.names.add(value);
    }

    public static class LogicalFileValue {
        private String name;

        public LogicalFileValue(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
