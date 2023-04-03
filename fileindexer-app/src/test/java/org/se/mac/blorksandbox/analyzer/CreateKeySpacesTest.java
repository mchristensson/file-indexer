package org.se.mac.blorksandbox.analyzer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepository;
import org.se.mac.blorksandbox.analyzer.repository.LogicalFileRepositoryImpl;
import org.se.mac.blorksandbox.scanner.ScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.DropKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.session.init.KeyspacePopulator;
import org.springframework.data.cassandra.core.cql.session.init.ResourceKeyspacePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@Import(CreateKeySpaces.class)
class CreateKeySpacesTest {

    @Test
    void getSchemaAction() {
        CreateKeySpaces createKeySpaces = new CreateKeySpaces();
        SchemaAction schemaAction = createKeySpaces.getSchemaAction();
        assertNotNull(schemaAction);
        assertEquals(schemaAction, SchemaAction.CREATE_IF_NOT_EXISTS);
    }

    @Test
    void getKeyspaceCreations() {
        CreateKeySpaces createKeySpaces = Mockito.mock(CreateKeySpaces.class);
        when(createKeySpaces.getKeyspaceCreations()).thenCallRealMethod();
        when(createKeySpaces.getKeyspaceName()).thenReturn("foo");

        List<CreateKeyspaceSpecification> specifications = createKeySpaces.getKeyspaceCreations();
        assertNotNull(specifications);
        assertEquals(1, specifications.size());
        assertEquals(CreateKeyspaceSpecification.class, specifications.get(0).getClass());
    }

    @Test
    void getKeyspaceDrops() {
        CreateKeySpaces createKeySpaces = Mockito.mock(CreateKeySpaces.class);
        when(createKeySpaces.getKeyspaceDrops()).thenCallRealMethod();
        when(createKeySpaces.getKeyspaceName()).thenReturn("foo");

        List<DropKeyspaceSpecification> keyspaceDrops = createKeySpaces.getKeyspaceDrops();
        assertNotNull(keyspaceDrops);
        assertEquals(1, keyspaceDrops.size());
    }

    @Test
    void keyspacePopulator() {
        CreateKeySpaces createKeySpaces = Mockito.mock(CreateKeySpaces.class);
        when(createKeySpaces.keyspacePopulator()).thenCallRealMethod();
        KeyspacePopulator keyspacePopulator = createKeySpaces.keyspacePopulator();
        assertNotNull(keyspacePopulator);
        assertEquals(ResourceKeyspacePopulator.class, keyspacePopulator.getClass());
    }

    @Test
    void keyspaceCleaner() {
        CreateKeySpaces createKeySpaces = Mockito.mock(CreateKeySpaces.class);
        when(createKeySpaces.keyspaceCleaner()).thenCallRealMethod();
        KeyspacePopulator keyspacePopulator = createKeySpaces.keyspaceCleaner();
        assertNotNull(keyspacePopulator);
        assertEquals(ResourceKeyspacePopulator.class, keyspacePopulator.getClass());
    }
}