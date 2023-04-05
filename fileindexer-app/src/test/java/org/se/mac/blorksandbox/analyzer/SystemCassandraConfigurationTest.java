package org.se.mac.blorksandbox.analyzer;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.DropKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.session.init.KeyspacePopulator;
import org.springframework.data.cassandra.core.cql.session.init.ResourceKeyspacePopulator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@Import(SystemCassandraConfiguration.class)
class SystemCassandraConfigurationTest {

    @Test
    void getSchemaAction() {
        SystemCassandraConfiguration systemCassandraConfiguration = new SystemCassandraConfiguration();

        SchemaAction schemaAction = systemCassandraConfiguration.getSchemaAction();

        assertNotNull(schemaAction);
        assertEquals(schemaAction, SchemaAction.CREATE_IF_NOT_EXISTS);
    }

    @Test
    void getKeyspaceCreations() {
        SystemCassandraConfiguration systemCassandraConfiguration = mock(SystemCassandraConfiguration.class);
        when(systemCassandraConfiguration.getKeyspaceCreations()).thenCallRealMethod();
        when(systemCassandraConfiguration.getKeyspaceName()).thenReturn("foo");

        List<CreateKeyspaceSpecification> specifications = systemCassandraConfiguration.getKeyspaceCreations();

        assertNotNull(specifications);
        assertEquals(1, specifications.size());
        assertEquals(CreateKeyspaceSpecification.class, specifications.get(0).getClass());

        verify(systemCassandraConfiguration).getKeyspaceCreations();
        verify(systemCassandraConfiguration).getKeyspaceName();
        verifyNoMoreInteractions(systemCassandraConfiguration);
    }

    @Test
    void getKeyspaceDrops() {
        SystemCassandraConfiguration systemCassandraConfiguration = mock(SystemCassandraConfiguration.class);
        when(systemCassandraConfiguration.getKeyspaceDrops()).thenCallRealMethod();
        when(systemCassandraConfiguration.getKeyspaceName()).thenReturn("foo");

        List<DropKeyspaceSpecification> keyspaceDrops = systemCassandraConfiguration.getKeyspaceDrops();

        assertNotNull(keyspaceDrops);
        assertEquals(1, keyspaceDrops.size());

        verify(systemCassandraConfiguration).getKeyspaceDrops();
        verify(systemCassandraConfiguration).getKeyspaceName();
        verifyNoMoreInteractions(systemCassandraConfiguration);
    }

    @Test
    void keyspacePopulator() {
        SystemCassandraConfiguration systemCassandraConfiguration = mock(SystemCassandraConfiguration.class);
        when(systemCassandraConfiguration.keyspacePopulator()).thenCallRealMethod();

        KeyspacePopulator keyspacePopulator = systemCassandraConfiguration.keyspacePopulator();

        assertNotNull(keyspacePopulator);
        assertEquals(ResourceKeyspacePopulator.class, keyspacePopulator.getClass());

        verify(systemCassandraConfiguration).keyspacePopulator();
        verifyNoMoreInteractions(systemCassandraConfiguration);
    }

    @Test
    void keyspaceCleaner() {
        SystemCassandraConfiguration systemCassandraConfiguration = mock(SystemCassandraConfiguration.class);
        when(systemCassandraConfiguration.keyspaceCleaner()).thenCallRealMethod();

        KeyspacePopulator keyspacePopulator = systemCassandraConfiguration.keyspaceCleaner();

        assertNotNull(keyspacePopulator);
        assertEquals(ResourceKeyspacePopulator.class, keyspacePopulator.getClass());

        verify(systemCassandraConfiguration).keyspaceCleaner();
        verifyNoMoreInteractions(systemCassandraConfiguration);
    }
}