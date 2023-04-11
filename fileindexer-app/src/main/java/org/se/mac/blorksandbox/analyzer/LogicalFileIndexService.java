package org.se.mac.blorksandbox.analyzer;

import org.se.mac.blorksandbox.analyzer.data.FileHashData;
import org.se.mac.blorksandbox.analyzer.data.FileMetaData;
import org.se.mac.blorksandbox.analyzer.data.SmallFileData;
import org.se.mac.blorksandbox.analyzer.repository.FileHashRepository;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepository;
import org.se.mac.blorksandbox.analyzer.repository.SmallFileDataRepository;
import org.se.mac.blorksandbox.analyzer.task.ImageHashGeneratorTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class LogicalFileIndexService {

    private static final Logger logger = LoggerFactory.getLogger(LogicalFileIndexService.class);

    @Autowired
    private LogicalFileRepository logicalFileRepository;

    @Autowired
    private FileHashRepository fileHashRepository;

    @Autowired
    private SmallFileDataRepository smallFileDataRepository;


    public Collection<FileHashData> getAllFileHashes() {
        List<FileHashData> result = new ArrayList<>();
        fileHashRepository.findAll().iterator().forEachRemaining(result::add);
        return result;
    }

    public Collection<FileMetaData> getAllFiles() {
        List<FileMetaData> result = new ArrayList<>();
        logicalFileRepository.findAll().iterator().forEachRemaining(result::add);
        return result;
    }

    public Optional<SmallFileData> getSmallFileById(UUID id) {
        return smallFileDataRepository.findById(id);
    }

    public int getFileHashComparison(Iterable<UUID> ids) {
        Iterator<FileHashData> iter = fileHashRepository.findAllById(ids).iterator();
        return ImageHashGeneratorTask.hammingDistance(iter.next().getHash(), iter.next().getHash());
    }

    /**
     * Creates a Hash-Of-File entity into corresponding keyspace
     *
     * @param deviceId   DeviceId where the hash-value's file (generated from) is located
     * @param timestamp  timestamp reflecting creation- or updated time
     * @param devicePath File path on the device where the hash-value's file (generated from) is located
     * @param hash       Hash value
     * @param scanTime   Analysis duration
     * @return
     */
    public FileHashData createFileHash(@Validated UUID deviceId, Instant timestamp, String devicePath, String hash, long scanTime) {
        FileHashData d = new FileHashData(UUID.randomUUID(), timestamp, deviceId, devicePath, hash, scanTime);
        return fileHashRepository.save(d);
    }

    /**
     * Creates a MetaData-Of-File entity into corresponding keyspace
     *
     * @param deviceId   DeviceId where the file is located
     * @param timestamp  timestamp reflecting creation- or updated time
     * @param devicePath File path on the device where the file is located
     * @param properties meta-data about the file
     * @param scanTime   Analysis duration
     * @return
     */
    public FileMetaData createFileMetaData(@Validated UUID deviceId, Instant timestamp, String devicePath, Map<String, String> properties, long scanTime) {
        FileMetaData d = new FileMetaData(UUID.randomUUID(), timestamp, deviceId, devicePath, properties, scanTime);
        return logicalFileRepository.save(d);
    }

    /**
     * Creates a File-With-Bytes entity into corresponding keyspace
     *
     * @param deviceId    DeviceId where the file was originally located
     * @param timestamp   timestamp reflecting creation- or updated time
     * @param devicePath  File path on the device where the file is located
     * @param data        file data
     * @param contentType File content-type
     * @return The created instance
     */
    public SmallFileData createSmallFile(@Validated UUID deviceId, Instant timestamp, String devicePath, ByteBuffer data, String contentType) {
        SmallFileData d = new SmallFileData(UUID.randomUUID(), timestamp, deviceId, devicePath, data, contentType);
        return smallFileDataRepository.save(d);
    }

    public void updateFileHashData(FileHashData fileHashData, Consumer<UUID> updateFunction, UUID smallFileDataId) {
        updateFunction.accept(smallFileDataId);
        fileHashRepository.save(fileHashData);
    }
}
