package org.se.mac.blorksandbox.analyzer;

import org.se.mac.blorksandbox.analyzer.data.LogicalFileData;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LogicalFileIndexService {
    @Autowired
    private LogicalFileRepository repository;

    public Collection<LogicalFileData> getAll() {
        List<LogicalFileData> result = new ArrayList<>();
        repository.findAll().iterator().forEachRemaining(result::add);
        return result;
    }

    public void add(UUID deviceId, String devicePath, Map<String, String> properties) {
        LogicalFileData d = new LogicalFileData(UUID.randomUUID(), deviceId, devicePath, properties);
        repository.save(d);
    }
}
