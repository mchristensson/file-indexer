package org.se.mac.blorksandbox.analyzer.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {LogicalFileRepositoryImpl.class})
class LogicalFileRepositoryTests {

    @MockBean
    private LogicalFileRepository service;

    @AfterEach
    public void verifyMocksAfter() {
        verifyNoMoreInteractions(service);
    }

    @Test
    void count() {
        when(service.count()).thenReturn(34L);
        long count = service.count();
        assertEquals(34L, count);

        verify(service).count();
    }


}
