package org.se.mac.blorksandbox.analyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.DropKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.core.cql.session.init.KeyspacePopulator;
import org.springframework.data.cassandra.core.cql.session.init.ResourceKeyspacePopulator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;

@Configuration
public class CreateKeySpaces extends AbstractCassandraConfiguration implements BeanClassLoaderAware {

    private static final Logger logger = LoggerFactory.getLogger(CreateKeySpaces.class);
    @Value("${spring.data.cassandra.keyspace-name}")
    private String keyspaceName;
    @Value("${spring.data.cassandra.contact-points}")
    private String contactPoints;
    @Value("${spring.data.cassandra.keyspace-action}")
    private String keyspaceAction;

    @Override
    protected String getContactPoints() {
        logger.debug("Returning property ['contact-points'={}]", this.contactPoints);
        return Objects.requireNonNullElse(this.contactPoints, "localhost");
    }

    @Override
    public SchemaAction getSchemaAction() {
        logger.debug("Returning property ['keyspace-action'={}]", this.keyspaceAction);
        if (this.keyspaceAction != null) {
            try {
                return SchemaAction.valueOf(this.keyspaceAction);
            } catch (IllegalArgumentException e) {
                logger.error("Unable to interpret configuration value for 'keyspace-action'", e);
                return SchemaAction.NONE;
            }
        }
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected String getKeyspaceName() {
        logger.debug("Returning property ['keyspace-name'={}]", this.keyspaceName);
        return keyspaceName;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        CreateKeyspaceSpecification specification = CreateKeyspaceSpecification.createKeyspace(getKeyspaceName())
                .ifNotExists()
                .with(KeyspaceOption.DURABLE_WRITES, true)
                .withSimpleReplication(1);
        return List.of(specification);
    }

    @Override
    protected List<DropKeyspaceSpecification> getKeyspaceDrops() {
        return List.of(DropKeyspaceSpecification.dropKeyspace(getKeyspaceName()));
    }

    @Override
    protected KeyspacePopulator keyspacePopulator() {
        return new ResourceKeyspacePopulator(new ClassPathResource("org/se/mac/fileindexer/schema/db-schema.cql"));
    }

    @Override
    protected KeyspacePopulator keyspaceCleaner() {
        return new ResourceKeyspacePopulator(new ClassPathResource("org/se/mac/fileindexer/schema/drop-create-schema.cql"));
    }
}