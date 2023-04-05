package org.se.mac.blorksandbox.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.se.mac.blorksandbox.scanner.rest.LogicalFileValue;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;


class LogicalFilesSearchResultTest {

    @Test
    void values_whenNullArg_expectCreated() {
        LogicalFileValue[] a = null;
        LogicalFilesSearchResult searchResult = new LogicalFilesSearchResult(a);

        assertNotNull(searchResult);
        assertNull(searchResult.values());
    }

    @Test
    void values_whenEmptyArray_expectCreated() {
        LogicalFileValue[] a = new LogicalFileValue[0];
        LogicalFilesSearchResult searchResult = new LogicalFilesSearchResult(a);

        assertNotNull(searchResult);
        assertNotNull(searchResult.values());
        assertEquals(0, searchResult.values().length);
    }

    @Test
    void values_whenSingleValueArray_expectCreated() {
        LogicalFileValue val0 = new LogicalFileValue("a", "b", Instant.now(),
                0L, "c", Collections.emptyMap());
        LogicalFileValue[] a = new LogicalFileValue[]{val0};
        LogicalFilesSearchResult searchResult = new LogicalFilesSearchResult(a);

        assertNotNull(searchResult);
        assertNotNull(searchResult.values());
        assertEquals(1, searchResult.values().length);
    }
}