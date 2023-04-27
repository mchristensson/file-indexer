package org.se.mac.blorksandbox.analyzer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import org.se.mac.blorksandbox.analyzer.data.DeviceData;
import org.se.mac.blorksandbox.analyzer.data.FileHashData;
import org.se.mac.blorksandbox.analyzer.data.FileMetaData;
import org.se.mac.blorksandbox.analyzer.data.SmallFileData;
import org.se.mac.blorksandbox.analyzer.repository.DeviceRepository;
import org.se.mac.blorksandbox.analyzer.repository.FileHashRepository;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepository;
import org.se.mac.blorksandbox.analyzer.repository.SmallFileDataRepository;
import org.se.mac.blorksandbox.analyzer.task.ImageHashGeneratorTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Service with ability to manage and extract file meta-data.
 */
@Service
public class LogicalFileIndexService {

    private static final Logger logger = LoggerFactory.getLogger(LogicalFileIndexService.class);

    @Autowired
    private LogicalFileRepository logicalFileRepository;

    @Autowired
    private FileHashRepository fileHashRepository;

    @Autowired
    private SmallFileDataRepository smallFileDataRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    /**
     * Returns all {@link FileHashData} entities.
     *
     * @return all fileHashData entities
     */
    public Collection<FileHashData> getAllFileHashes() {
        List<FileHashData> result = new ArrayList<>();
        fileHashRepository.findAll().iterator().forEachRemaining(result::add);
        return result;
    }

    /**
     * Returns all {@link FileMetaData} entities.
     *
     * @return all filemetadata entities.
     */
    public Collection<FileMetaData> getAllFiles() {
        List<FileMetaData> result = new ArrayList<>();
        logicalFileRepository.findAll().iterator().forEachRemaining(result::add);
        return result;
    }

    /**
     * Returns all {@link DeviceData} entities.
     *
     * @return all device entities
     */
    public List<DeviceData> getAllDevices() {
        List<DeviceData> result = new ArrayList<>();
        deviceRepository.findAll().iterator().forEachRemaining(result::add);
        return result;
    }

    /**
     * Checks whether device is present in repository.
     *
     * @param deviceId Id of the device to check
     * @return true if device is present in repository, otherwise false
     */
    public boolean isDevicePresent(UUID deviceId) {
        return deviceRepository.findById(deviceId).isPresent();
    }

    public Optional<SmallFileData> getSmallFileById(UUID id) {
        return smallFileDataRepository.findById(id);
    }

    /**
     * Searches for the images corresponding to the argument ID's and perform Hamming Distance
     * calculation between them.
     *
     * @param ids Iterable holding at least two (2) ids.
     * @return The hamming distance
     */
    public int getFileHashComparison(Iterable<UUID> ids) {
        List<FileHashData> result = new ArrayList<>();
        fileHashRepository.findAllById(ids).iterator().forEachRemaining(result::add);
        if (result.size() < 2) {
            throw new NoSuchElementException(String.format(
                    "Not enough data for comparison (expected at least 2, actual was '%s')",
                    result.size()));

        }
        return ImageHashGeneratorTask.hammingDistance(result.get(0).getHash(),
                result.get(1).getHash());
    }

    /**
     * Creates a Hash-Of-File entity into corresponding keyspace.
     *
     * @param deviceId   DeviceId where the hash-value's file (generated from) is located
     * @param timestamp  timestamp reflecting creation- or updated time
     * @param devicePath File path on the device where the hash-value's file (generated from) is
     *                   located
     * @param hash       Hash value
     * @param scanTime   Analysis duration
     * @return Entity created
     */
    public FileHashData createFileHash(@Validated
                                       UUID deviceId, Instant timestamp, String devicePath,
                                       String hash, long scanTime) {
        FileHashData d = new FileHashData(UUID.randomUUID(), timestamp, deviceId, devicePath, hash,
                scanTime);
        return fileHashRepository.save(d);
    }

    /**
     * Creates a MetaData-Of-File entity into corresponding keyspace.
     *
     * @param deviceId   DeviceId where the file is located
     * @param timestamp  timestamp reflecting creation- or updated time
     * @param devicePath File path on the device where the file is located
     * @param properties meta-data about the file
     * @param scanTime   Analysis duration
     * @return New instance of FileMetaData
     */
    public FileMetaData createFileMetaData(@Validated
                                           UUID deviceId, Instant timestamp, String devicePath,
                                           Map<String, String> properties, long scanTime) {
        FileMetaData d = new FileMetaData(UUID.randomUUID(), timestamp, deviceId, devicePath,
                properties, scanTime);
        return logicalFileRepository.save(d);
    }

    /**
     * Creates a File-With-Bytes entity into corresponding keyspace.
     *
     * @param deviceId    DeviceId where the file was originally located
     * @param devicePath  File path on the device where the file is located
     * @param data        file data
     * @param contentType File content-type
     * @param timestamp   timestamp reflecting creation- or updated time
     * @return The created instance
     */
    public SmallFileData createSmallFile(@Validated
                                         UUID deviceId, String devicePath, ByteBuffer data,
                                         String contentType, Instant timestamp) {
        SmallFileData d = new SmallFileData(UUID.randomUUID(), timestamp, deviceId, devicePath,
                data, contentType);
        return smallFileDataRepository.save(d);
    }

    /**
     * Read file data from disk a store as new entity with image binary data.
     *
     * @param deviceId         DeviceId where the file was originally located
     * @param devicePath       File path on the device where the file is located
     * @param filePath         Path to read file from
     * @param outputFileFormat Output file format
     * @return UUID for the created entity
     * @throws IOException If source file read operation fail
     */
    public UUID createSmallFile(@Validated
                                UUID deviceId, Path devicePath, String filePath,
                                String outputFileFormat) throws IOException {
        InputStream in = null;

        try {
            in = new FileInputStream(filePath);
            ByteBuffer byteBuffer = ByteBuffer.wrap(in.readAllBytes());
            SmallFileData smallFileData = createSmallFile(deviceId, devicePath.toString(),
                    byteBuffer, outputFileFormat, Instant.ofEpochMilli(System.currentTimeMillis()));
            if (smallFileData == null) {
                throw new NullPointerException("Data was not generated");
            }
            return smallFileData.getId();

        } catch (IOException e) {
            logger.error("Could not read from file in order to store it", e);
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("Could not close input-stream", e);
                }
            }
        }
    }

    public void updateFileHashData(FileHashData fileHashData, Consumer<UUID> updateFunction,
                                   UUID smallFileDataId) {
        updateFunction.accept(smallFileDataId);
        fileHashRepository.save(fileHashData);
    }

    /**
     * Creates a new device entity.
     *
     * @param devicePath Path information about the device
     * @param title      title for the device
     * @param properties meta-data about the device
     * @return The newly created device
     */
    public DeviceData createDevice(String devicePath, String title,
                                   Map<String, String> properties) {
        DeviceData d = new DeviceData(UUID.randomUUID(), devicePath, title,
                Instant.ofEpochMilli(System.currentTimeMillis()), properties);
        return deviceRepository.save(d);
    }
}
