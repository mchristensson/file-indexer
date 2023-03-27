package org.se.mac.blorksandbox.analyzer.repository;

import org.se.mac.blorksandbox.analyzer.data.LogicalFileData;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

/**
 * Replaces Cassandrarepo when in Junit5 tests
 */
public class LogicalFileRepositoryImpl implements LogicalFileRepository {
    @Override
    public LogicalFileData findBySuffix(String suffix) {
        return null;
    }

    @Override
    public <S extends LogicalFileData> S save(S entity) {
        return null;
    }

    @Override
    public <S extends LogicalFileData> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<LogicalFileData> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(UUID uuid) {
        return false;
    }

    @Override
    public Iterable<LogicalFileData> findAll() {
        return null;
    }

    @Override
    public Iterable<LogicalFileData> findAllById(Iterable<UUID> uuids) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(UUID uuid) {

    }

    @Override
    public void delete(LogicalFileData entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {

    }

    @Override
    public void deleteAll(Iterable<? extends LogicalFileData> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
