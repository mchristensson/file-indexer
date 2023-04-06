package org.se.mac.blorksandbox.analyzer.repository;

import org.se.mac.blorksandbox.analyzer.data.FileMetaData;

import java.util.Optional;
import java.util.UUID;

/**
 * Replaces Cassandrarepo when in Junit5 tests
 */
public class LogicalFileRepositoryImpl implements LogicalFileRepository {

    @Override
    public <S extends FileMetaData> S save(S entity) {
        return null;
    }

    @Override
    public <S extends FileMetaData> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<FileMetaData> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(UUID uuid) {
        return false;
    }

    @Override
    public Iterable<FileMetaData> findAll() {
        return null;
    }

    @Override
    public Iterable<FileMetaData> findAllById(Iterable<UUID> uuids) {
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
    public void delete(FileMetaData entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {

    }

    @Override
    public void deleteAll(Iterable<? extends FileMetaData> entities) {

    }

    @Override
    public void deleteAll() {

    }
}
