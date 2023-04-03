package org.se.mac.blorksandbox.analyzer;

import com.drew.lang.annotations.NotNull;
import org.se.mac.blorksandbox.analyzer.data.LogicalFileData;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
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

    public void add(@Validated @NotNull UUID deviceId, Instant timestamp, String devicePath, Map<String, String> properties, long scanTime) {
        LogicalFileData d = new LogicalFileData(UUID.randomUUID(), timestamp, deviceId, devicePath, properties, scanTime);
        repository.save(d);
    }
}
