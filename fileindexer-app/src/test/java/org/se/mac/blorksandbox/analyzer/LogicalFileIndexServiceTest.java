package org.se.mac.blorksandbox.analyzer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.se.mac.blorksandbox.analyzer.data.FileHashData;
import org.se.mac.blorksandbox.analyzer.data.FileMetaData;
import org.se.mac.blorksandbox.analyzer.data.SmallFileData;
import org.se.mac.blorksandbox.analyzer.repository.FileHashRepository;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepository;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepositoryImpl;
import org.se.mac.blorksandbox.analyzer.repository.SmallFileDataRepository;
import org.se.mac.blorksandbox.scanner.rest.LogicalFileValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {LogicalFileRepositoryImpl.class, LogicalFileIndexService.class})
class LogicalFileIndexServiceTest {

    @MockBean
    private LogicalFileRepository repository;

    @MockBean
    private FileHashRepository fileHashRepository;

    @MockBean
    private SmallFileDataRepository smallFileDataRepository;

    @Autowired
    private LogicalFileIndexService logicalFileIndexService;

    @AfterEach
    public void verifyMocksAfter() {
        verifyNoMoreInteractions(repository, fileHashRepository, smallFileDataRepository);
    }

    @Test
    void getAllFiles_whenNoDataPresent_expectResult() {
        Collection<FileMetaData> mockResult = new ArrayList<>();
        when(logicalFileIndexService.getAllFiles()).thenReturn(mockResult);

        Collection<FileMetaData> searchResult = logicalFileIndexService.getAllFiles();

        assertEquals(0, searchResult.size());
        verify(repository).findAll();
    }

    @Test
    void getAllFiles_whenDataPresent_expectResult() {
        Collection<FileMetaData> mockResult = new ArrayList<>();
        FileMetaData mock1 = mock(FileMetaData.class);
        FileMetaData mock2 = mock(FileMetaData.class);
        mockResult.add(mock1);
        mockResult.add(mock2);
        when(logicalFileIndexService.getAllFiles()).thenReturn(mockResult);

        Collection<FileMetaData> searchResult = logicalFileIndexService.getAllFiles();

        assertEquals(2, searchResult.size());
        verify(repository).findAll();
        verifyNoMoreInteractions(mock1, mock2);
    }

    @Test
    void createFileMetaData_whenNullDeviceId_expectAdded() {
        Instant timestamp = Instant.now();

        logicalFileIndexService.createFileMetaData(null, timestamp, "c:/abc/def/ghi.pcx", null, 100L);

        verify(repository).save(any(FileMetaData.class));
    }

    @Test
    void createFileMetaData_whenNullMap_expectAdded() {
        UUID uuid = UUID.randomUUID();
        Instant timestamp = Instant.now();

        logicalFileIndexService.createFileMetaData(uuid, timestamp, "c:/abc/def/ghi.pcx", null, 100L);

        verify(repository).save(any(FileMetaData.class));
    }

    @Test
    void createFileMetaData_whenValidValues_expectAdded() {
        UUID uuid = UUID.randomUUID();
        Map<String, String> data = new HashMap<>();
        Instant timestamp = Instant.now();

        logicalFileIndexService.createFileMetaData(uuid, timestamp, "c:/abc/def/ghi.pcx", data, 100L);

        verify(repository).save(any(FileMetaData.class));
    }

    @Test
    void getAllFileHashes_whenNoDataPresent_expectResult() {
        Collection<FileHashData> mockResult = new ArrayList<>();
        when(logicalFileIndexService.getAllFileHashes()).thenReturn(mockResult);

        Collection<FileHashData> searchResult = logicalFileIndexService.getAllFileHashes();

        assertEquals(0, searchResult.size());
        verify(fileHashRepository).findAll();
    }

    @Test
    void getAllFileHashes_whenDataPresent_expectResult() {
        Collection<FileHashData> mockResult = new ArrayList<>();
        FileHashData mock1 = mock(FileHashData.class);
        FileHashData mock2 = mock(FileHashData.class);
        mockResult.add(mock1);
        mockResult.add(mock2);
        when(logicalFileIndexService.getAllFileHashes()).thenReturn(mockResult);

        Collection<FileHashData> searchResult = logicalFileIndexService.getAllFileHashes();

        assertEquals(2, searchResult.size());
        verify(fileHashRepository).findAll();
        verifyNoMoreInteractions(mock1, mock2);
    }

    @Test
    void getFileHashComparison_whenNoDataPresent_expectException() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<UUID> inputIds = new ArrayList<>();
        inputIds.add(id1);
        inputIds.add(id2);
        when(fileHashRepository.findAllById(inputIds)).thenReturn(Collections.emptyList());

        try {
            logicalFileIndexService.getFileHashComparison(inputIds);
            fail("Exception expected");
        } catch (Exception e) {
            assertEquals(NoSuchElementException.class, e.getClass());
        }

        verify(fileHashRepository).findAllById(anyCollection());
    }

    @Test
    void getFileHashComparison_whenSinglePresent_expectException() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<UUID> inputIds = new ArrayList<>();
        inputIds.add(id1);
        inputIds.add(id2);
        FileHashData mock1 = mock(FileHashData.class);
        when(fileHashRepository.findAllById(inputIds)).thenReturn(Collections.singletonList(mock1));

        try {
            logicalFileIndexService.getFileHashComparison(inputIds);
            fail("Exception expected");
        } catch (Exception e) {
            assertEquals(NoSuchElementException.class, e.getClass());
        }

        verify(fileHashRepository).findAllById(anyCollection());
        verify(mock1).getHash();
        verifyNoMoreInteractions(mock1);
    }

    @Test
    void getFileHashComparison_whenPresentNoHashes_expectDefaultChecksum() {
        List<UUID> inputIds = new ArrayList<>();
        inputIds.add(UUID.randomUUID());
        inputIds.add(UUID.randomUUID());
        FileHashData mock1 = mock(FileHashData.class);
        FileHashData mock2 = mock(FileHashData.class);
        List<FileHashData> searchResult = new ArrayList<>();
        searchResult.add(mock1);
        searchResult.add(mock2);

        when(fileHashRepository.findAllById(inputIds)).thenReturn(searchResult);

        int result = logicalFileIndexService.getFileHashComparison(inputIds);
        assertEquals(-1, result);

        verify(fileHashRepository).findAllById(anyCollection());
        verify(mock1).getHash();
        verify(mock2).getHash();
        verifyNoMoreInteractions(mock1, mock2);
    }

    @Test
    void getFileHashComparison_whenPresentNoSecondHash_expectDefaultChecksum() {
        List<UUID> inputIds = new ArrayList<>();
        inputIds.add(UUID.randomUUID());
        inputIds.add(UUID.randomUUID());
        FileHashData mock1 = mock(FileHashData.class);
        when(mock1.getHash()).thenReturn("ff");
        FileHashData mock2 = mock(FileHashData.class);
        List<FileHashData> searchResult = new ArrayList<>();
        searchResult.add(mock1);
        searchResult.add(mock2);

        when(fileHashRepository.findAllById(inputIds)).thenReturn(searchResult);

        int result = logicalFileIndexService.getFileHashComparison(inputIds);
        assertEquals(-1, result);

        verify(fileHashRepository).findAllById(anyCollection());
        verify(mock1).getHash();
        verify(mock2).getHash();
        verifyNoMoreInteractions(mock1, mock2);
    }


    @Test
    void getFileHashComparison_whenPresent_expectChecksum() {
        List<UUID> inputIds = new ArrayList<>();
        inputIds.add(UUID.randomUUID());
        inputIds.add(UUID.randomUUID());
        FileHashData mock1 = mock(FileHashData.class);
        when(mock1.getHash()).thenReturn("ff");
        FileHashData mock2 = mock(FileHashData.class);
        when(mock2.getHash()).thenReturn("af");
        List<FileHashData> searchResult = new ArrayList<>();
        searchResult.add(mock1);
        searchResult.add(mock2);

        when(fileHashRepository.findAllById(inputIds)).thenReturn(searchResult);

        int result = logicalFileIndexService.getFileHashComparison(inputIds);
        assertEquals(2, result);

        verify(fileHashRepository).findAllById(anyCollection());
        verify(mock1).getHash();
        verify(mock2).getHash();
        verifyNoMoreInteractions(mock1, mock2);
    }

    @Test
    void getSmallFileById_whenNoDataPresent_expectResult() {
        Collection<FileHashData> mockResult = new ArrayList<>();
        when(logicalFileIndexService.getAllFileHashes()).thenReturn(mockResult);

        Optional<SmallFileData> searchResult = logicalFileIndexService.getSmallFileById(UUID.randomUUID());

        assertFalse(searchResult.isPresent());
        verify(smallFileDataRepository).findById(any(UUID.class));
    }

    @Test
    void getSmallFileById_whenDataPresent_expectResult() {
        SmallFileData mock1 = mock(SmallFileData.class);
        when(logicalFileIndexService.getSmallFileById(any(UUID.class)))
                .thenReturn(Optional.of(mock1));

        Optional<SmallFileData> searchResult = logicalFileIndexService.getSmallFileById(UUID.randomUUID());
        assertTrue(searchResult.isPresent());

        verify(smallFileDataRepository).findById(any(UUID.class));
        verifyNoMoreInteractions(mock1);
    }

    @Test
    void createSmallFile_whenNullDeviceId_expectAdded() {
        Instant timestamp = Instant.now();
        SmallFileData mock = mock(SmallFileData.class);
        when(smallFileDataRepository.save(any(SmallFileData.class))).thenReturn(mock);

        SmallFileData result = logicalFileIndexService.createSmallFile(null, timestamp, "c:/abc/def/ghi.pcx", null, "image/pcx");
        assertNotNull(result);
        verify(smallFileDataRepository).save(any(SmallFileData.class));
    }

    @Test
    void createSmallFile_whenNullMap_expectAdded() {
        UUID uuid = UUID.randomUUID();
        Instant timestamp = Instant.now();
        SmallFileData mock = mock(SmallFileData.class);
        when(smallFileDataRepository.save(any(SmallFileData.class))).thenReturn(mock);

        SmallFileData result = logicalFileIndexService.createSmallFile(uuid, timestamp, "c:/abc/def/ghi.pcx", null, "image/pcx");
        assertNotNull(result);
        verify(smallFileDataRepository).save(any(SmallFileData.class));
    }

    @Test
    void createSmallFile_whenValidValues_expectAdded() {
        UUID uuid = UUID.randomUUID();
        Instant timestamp = Instant.now();
        SmallFileData mock = mock(SmallFileData.class);
        when(smallFileDataRepository.save(any(SmallFileData.class))).thenReturn(mock);
        byte[] data = "foo".getBytes(Charset.defaultCharset());

        SmallFileData result =  logicalFileIndexService.createSmallFile(uuid, timestamp, "c:/abc/def/ghi.pcx", ByteBuffer.wrap(data), "iamge/pcx");
        assertNotNull(result);
        verify(smallFileDataRepository).save(any(SmallFileData.class));
    }

    @Test
    void createFileHash_whenNullDeviceId_expectAdded() {
        Instant timestamp = Instant.now();
        FileHashData mock = mock(FileHashData.class);
        when(fileHashRepository.save(any(FileHashData.class))).thenReturn(mock);

        FileHashData result = logicalFileIndexService.createFileHash(null, timestamp, "c:/abc/def/ghi.pcx", null, 100L);
        assertNotNull(result);
        verify(fileHashRepository).save(any(FileHashData.class));
    }

    @Test
    void createFileHash_whenNullMap_expectAdded() {
        UUID uuid = UUID.randomUUID();
        Instant timestamp = Instant.now();
        FileHashData mock = mock(FileHashData.class);
        when(fileHashRepository.save(any(FileHashData.class))).thenReturn(mock);

        FileHashData result = logicalFileIndexService.createFileHash(uuid, timestamp, "c:/abc/def/ghi.pcx", "15f53f", 100L);
        assertNotNull(result);
        verify(fileHashRepository).save(any(FileHashData.class));
    }

    @Test
    void createFileHash_whenValidValues_expectAdded() {
        UUID uuid = UUID.randomUUID();
        Instant timestamp = Instant.now();
        FileHashData mock = mock(FileHashData.class);
        when(fileHashRepository.save(any(FileHashData.class))).thenReturn(mock);

        FileHashData result = logicalFileIndexService.createFileHash(uuid, timestamp, "c:/abc/def/ghi.pcx", "15f53f", 100L);
        assertNotNull(result);
        verify(fileHashRepository).save(any(FileHashData.class));
    }
}