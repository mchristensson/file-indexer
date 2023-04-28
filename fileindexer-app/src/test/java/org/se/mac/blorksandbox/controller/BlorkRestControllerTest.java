package org.se.mac.blorksandbox.controller;

import java.nio.ByteBuffer;
import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.se.mac.blorksandbox.analyzer.FileTransformationService;
import org.se.mac.blorksandbox.analyzer.LogicalFileIndexService;
import org.se.mac.blorksandbox.analyzer.data.FileHashData;
import org.se.mac.blorksandbox.analyzer.data.FileMetaData;
import org.se.mac.blorksandbox.analyzer.data.SmallFileData;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepositoryImpl;
import org.se.mac.blorksandbox.jobqueue.QueueService;
import org.se.mac.blorksandbox.jobqueue.job.DummyJob;
import org.se.mac.blorksandbox.jobqueue.rest.QueueJobStatus;
import org.se.mac.blorksandbox.jobqueue.rest.QueuedJobRequestReceipt;
import org.se.mac.blorksandbox.rest.LogicalFilesSearchResult;
import org.se.mac.blorksandbox.scanner.ScannerService;
import org.se.mac.blorksandbox.scanner.rest.ScanEnqueueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {LogicalFileRepositoryImpl.class, BlorkRestController.class})
class BlorkRestControllerTest {

    @MockBean
    private QueueService queueService;

    @MockBean
    private ScannerService scannerService;

    @MockBean
    private LogicalFileIndexService fileIndexService;

    @MockBean
    private FileTransformationService fileTransformationService;

    @MockBean
    private QueueJobRepository queueJobRepository;

    @MockBean
    private ApplicationContext applicationContext;

    @Autowired
    private BlorkRestController blorkRestController;

    @AfterEach
    public void verifyMocksAfter() {
        verifyNoMoreInteractions(queueService, scannerService, fileIndexService,
                fileTransformationService, queueJobRepository, applicationContext);
    }

    @Test
    void findAllJobsTitles_whenNotPresent_expectEmptyResult() {
        when(queueJobRepository.findAllTitles()).thenReturn(Collections.emptyList());
        ResponseEntity<List<String>> responseData = blorkRestController.findAllJobsTitles();

        assertNotNull(responseData);
        assertEquals(HttpStatus.OK, responseData.getStatusCode());

        List<String> responseBody = responseData.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isEmpty());

        verify(queueJobRepository).findAllTitles();
    }

    @Test
    void findAllJobsTitles_whenPresent_expectReturned() {
        when(queueJobRepository.findAllTitles()).thenReturn(Collections.singletonList("foo"));
        ResponseEntity<List<String>> responseData = blorkRestController.findAllJobsTitles();

        assertNotNull(responseData);
        assertEquals(HttpStatus.OK, responseData.getStatusCode());

        List<String> responseBody = responseData.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        assertEquals("foo", responseBody.get(0));

        verify(queueJobRepository).findAllTitles();
    }

    @Test
    void queueQueueJobStatus_whenNotPresent_expectEmptyResult() {
        when(queueService.getResult()).thenReturn(Collections.emptyMap());
        QueueJobStatus responseData = blorkRestController.queueQueueJobStatus();
        assertNotNull(responseData);
        assertEquals(0, responseData.getData().length);
        assertNotEquals(0L, responseData.getTimestamp());

        verify(queueService).getResult();
    }

    @Test
    void queueQueueJobStatus_whenPresent_expectEmptyResult() {
        Map<Long, Integer> map = new HashMap<>();
        map.put(10L, 20);
        map.put(11L, 30);
        when(queueService.getResult()).thenReturn(map);
        QueueJobStatus responseData = blorkRestController.queueQueueJobStatus();
        assertNotNull(responseData);
        assertEquals(2, responseData.getData().length);
        assertNotEquals(0L, responseData.getTimestamp());

        verify(queueService).getResult();
    }

    @Test
    void enqueue_whenRequestNull_expectException() {
        try {
            blorkRestController.enqueue(null);
            fail("Exception expected");
        } catch (RuntimeException e) {
            assertEquals(NullPointerException.class, e.getClass());
        }
    }

    @Test
    void enqueue_whenNullTitle_expectErrorMessage() {
        String title = null;
        Map<String, String> settingsMock = mock(HashMap.class);
        ScanEnqueueRequest requestMock = new ScanEnqueueRequest(title, settingsMock);

        QueuedJobRequestReceipt result = blorkRestController.enqueue(requestMock);
        assertNotNull(result);
        assertNull(result.getMessage());
        assertEquals("Job definition null could not be found.", result.getErrorMessage());
        assertEquals(0L, result.getId());
        verify(queueJobRepository).lookupByTitle(isNull());
        verifyNoMoreInteractions(settingsMock);
    }

    @Test
    void enqueue_whenJobTitleInvalid_expectErrorMessage() {
        String title = "foo";
        Map<String, String> settings = null;
        ScanEnqueueRequest requestMock = new ScanEnqueueRequest(title, settings);

        QueuedJobRequestReceipt result = blorkRestController.enqueue(requestMock);
        assertNotNull(result);
        assertNull(result.getMessage());
        assertEquals("Job definition foo could not be found.", result.getErrorMessage());
        assertEquals(0L, result.getId());

        verify(queueJobRepository).lookupByTitle("foo");
        assertNotNull(result);


    }


    @Test
    void enqueue_whenJobTitleInvalidReturnNull_expectErrorMessage() {
        String title = "foo";
        Map<String, String> settings = null;
        ScanEnqueueRequest requestMock = new ScanEnqueueRequest(title, settings);

        doReturn(Optional.empty()).when(queueJobRepository).lookupByTitle(anyString());

        QueuedJobRequestReceipt result = blorkRestController.enqueue(requestMock);
        assertNotNull(result);
        assertNull(result.getMessage());
        assertEquals("Job definition foo could not be found.", result.getErrorMessage());
        assertEquals(0L, result.getId());

        verify(queueJobRepository).lookupByTitle("foo");
        assertNotNull(result);
    }

    @Test
    void enqueue_whenJobTitleValid_expectMessage() {
        String title = "foo";
        Map<String, String> settings = null;
        ScanEnqueueRequest requestMock = new ScanEnqueueRequest(title, settings);

        Class<DummyJob> value = DummyJob.class;
        doReturn(Optional.ofNullable(value)).when(queueJobRepository).lookupByTitle(anyString());

        QueuedJobRequestReceipt result = blorkRestController.enqueue(requestMock);
        assertNotNull(result);
        assertNull(result.getErrorMessage());
        assertEquals("Job of type 'foo' was enqueued", result.getMessage());
        assertNotEquals(0L, result.getId());

        verify(queueJobRepository).lookupByTitle("foo");
        verify(queueService).enqueue(any(DummyJob.class));
        assertNotNull(result);
    }

    @Test
    void listLogicalFiles_whenNonePresent_expectEmptyResult() {
        LogicalFilesSearchResult result = blorkRestController.listLogicalFiles();
        assertNotNull(result);
        assertNotNull(result.values());
        assertEquals(0, result.values().length);
        verify(fileIndexService).getAllFiles();
    }

    @Test
    void listLogicalFiles_whenTwoEntitiesPresent_expectSearchResult() {
        FileMetaData mock1 = mock(FileMetaData.class);
        when(mock1.getId()).thenReturn(UUID.randomUUID());
        when(mock1.getDeviceId()).thenReturn(UUID.randomUUID());
        FileMetaData mock2 = mock(FileMetaData.class);
        when(mock2.getId()).thenReturn(UUID.randomUUID());
        when(mock2.getDeviceId()).thenReturn(UUID.randomUUID());
        Collection<FileMetaData> resultList = new ArrayList<>();
        resultList.add(mock1);
        resultList.add(mock2);
        when(fileIndexService.getAllFiles()).thenReturn(resultList);
        LogicalFilesSearchResult result = blorkRestController.listLogicalFiles();
        assertNotNull(result);
        assertNotNull(result.values());
        assertEquals(2, result.values().length);
        verify(fileIndexService).getAllFiles();
        verify(mock1).getId();
        verify(mock1).getDevicePath();
        verify(mock1).getUpdated_date();
        verify(mock1).getScanTime();
        verify(mock1).getDeviceId();
        verify(mock1).getProperties();
        verify(mock2).getId();
        verify(mock2).getDevicePath();
        verify(mock2).getUpdated_date();
        verify(mock2).getScanTime();
        verify(mock2).getDeviceId();
        verify(mock2).getProperties();
        verifyNoMoreInteractions(mock1, mock2);
    }

    @Test
    void listImageHash_whenEntityNotPresent_expectEmptyResult() {
        LogicalFilesSearchResult result = blorkRestController.listImageHash();
        assertNotNull(result);
        assertNotNull(result.values());

        verify(fileIndexService).getAllFileHashes();
    }

    @Test
    void listImageHash_whenTwoEntitiesPresent_expectEmptyResult() {
        FileHashData mock1 = mock(FileHashData.class);
        when(mock1.getId()).thenReturn(UUID.randomUUID());
        when(mock1.getDeviceId()).thenReturn(UUID.randomUUID());
        FileHashData mock2 = mock(FileHashData.class);
        when(mock2.getId()).thenReturn(UUID.randomUUID());
        when(mock2.getDeviceId()).thenReturn(UUID.randomUUID());
        Collection<FileHashData> resultList = new ArrayList<>();
        resultList.add(mock1);
        resultList.add(mock2);
        when(fileIndexService.getAllFileHashes()).thenReturn(resultList);
        LogicalFilesSearchResult result = blorkRestController.listImageHash();
        assertNotNull(result);
        assertNotNull(result.values());
        assertEquals(2, result.values().length);
        verify(fileIndexService).getAllFileHashes();
        verify(mock1).getId();
        verify(mock1).getDevicePath();
        verify(mock1).getUpdated_date();
        verify(mock1).getScanTime();
        verify(mock1).getDeviceId();
        verify(mock1).getHash();
        verify(mock1).getSmallFileDataId();
        verify(mock2).getId();
        verify(mock2).getDevicePath();
        verify(mock2).getUpdated_date();
        verify(mock2).getScanTime();
        verify(mock2).getDeviceId();
        verify(mock2).getHash();
        verify(mock2).getSmallFileDataId();
        verifyNoMoreInteractions(mock1, mock2);
    }

    @Test
    void imageById_whenNullArg_expectNullData() {
        String arg = null;
        byte[] result = blorkRestController.imageById(arg);
        assertNull(result);
    }

    @Test
    void imageById_whenNonUUID_expectNullData() {
        String arg = "foo";
        byte[] result = blorkRestController.imageById(arg);
        assertNull(result);
    }

    @Test
    void imageById_whenEntityNotPresent_expectNullData() {
        String arg = UUID.randomUUID().toString();
        byte[] result = blorkRestController.imageById(arg);
        assertNull(result);
        verify(fileIndexService).getSmallFileById(any(UUID.class));
    }

    @Test
    void imageById_whenEntityNotPresentB_expectNullData() {
        Optional<SmallFileData> opt = Optional.empty();
        doReturn(opt).when(fileIndexService).getSmallFileById(any(UUID.class));
        String arg = UUID.randomUUID().toString();
        byte[] result = blorkRestController.imageById(arg);
        assertNull(result);
        verify(fileIndexService).getSmallFileById(any(UUID.class));
    }

    @Test
    void imageById_whenEntityPresentNoContent_expectData() {
        SmallFileData smallFileDataMock = mock(SmallFileData.class);
        Optional<SmallFileData> opt = Optional.of(smallFileDataMock);
        doReturn(opt).when(fileIndexService).getSmallFileById(any(UUID.class));
        String arg = UUID.randomUUID().toString();
        byte[] result = blorkRestController.imageById(arg);
        assertNull(result);
        verify(fileIndexService).getSmallFileById(any(UUID.class));
        verify(smallFileDataMock).hasData();
        verifyNoMoreInteractions(smallFileDataMock);
    }

    @Test
    void imageById_whenEntityPresent_expectData() {
        SmallFileData smallFileDataMock = mock(SmallFileData.class);
        when(smallFileDataMock.getBlob()).thenReturn(ByteBuffer.wrap("helloworld".getBytes()));
        when(smallFileDataMock.hasData()).thenReturn(true);
        Optional<SmallFileData> opt = Optional.of(smallFileDataMock);
        doReturn(opt).when(fileIndexService).getSmallFileById(any(UUID.class));
        String arg = UUID.randomUUID().toString();
        byte[] result = blorkRestController.imageById(arg);
        assertNotNull(result);
        verify(fileIndexService).getSmallFileById(any(UUID.class));
        verify(smallFileDataMock).hasData();
        verify(smallFileDataMock).getBlob();
        verifyNoMoreInteractions(smallFileDataMock);
    }

    @Test
    void compareImageHash() {
        //TODO:
    }

    @Test
    void findAllJobsDevices() {
        //TODO:
    }

    @Test
    void addDevice() {
        //TODO:
    }

    @Test
    void transformImage() {
        //TODO:
    }
}