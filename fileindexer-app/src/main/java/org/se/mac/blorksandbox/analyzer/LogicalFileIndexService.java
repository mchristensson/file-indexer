package org.se.mac.blorksandbox.analyzer;

import com.drew.lang.annotations.NotNull;
import org.se.mac.blorksandbox.analyzer.data.FileHashData;
import org.se.mac.blorksandbox.analyzer.data.LogicalFileData;
import org.se.mac.blorksandbox.analyzer.repository.FileHashRepository;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepository;
import org.se.mac.blorksandbox.analyzer.task.ImageHashGeneratorTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.*;

@Service
public class LogicalFileIndexService {

    @Autowired
    private LogicalFileRepository logicalFileRepository;

    @Autowired
    private FileHashRepository fileHashRepository;

    public Collection<FileHashData> getAllFileHashes() {
        List<FileHashData> result = new ArrayList<>();
        fileHashRepository.findAll().iterator().forEachRemaining(result::add);
        return result;
    }

    public void addFileHash(@Validated @NotNull UUID deviceId, Instant timestamp, String devicePath, String hash, long scanTime) {
        FileHashData d = new FileHashData(UUID.randomUUID(), timestamp, deviceId, devicePath, hash, scanTime);
        fileHashRepository.save(d);
    }

    public Collection<LogicalFileData> getAllFiles() {
        List<LogicalFileData> result = new ArrayList<>();
        logicalFileRepository.findAll().iterator().forEachRemaining(result::add);
        return result;
    }

    public void addFile(@Validated @NotNull UUID deviceId, Instant timestamp, String devicePath, Map<String, String> properties, long scanTime) {
        LogicalFileData d = new LogicalFileData(UUID.randomUUID(), timestamp, deviceId, devicePath, properties, scanTime);
        logicalFileRepository.save(d);
    }

    public int compareFileHashes(Iterable<UUID> ids) {
        Iterator<FileHashData> iter = fileHashRepository.findAllById(ids).iterator();
        long r1 = Long.parseUnsignedLong(iter.next().getHash(), 16);
        long r2 = Long.parseUnsignedLong(iter.next().getHash(), 16);
        return ImageHashGeneratorTask.hammingDistance(r1, r2);
    }
}
