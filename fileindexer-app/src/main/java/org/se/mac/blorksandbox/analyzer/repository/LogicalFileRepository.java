package org.se.mac.blorksandbox.analyzer.repository;

import org.se.mac.blorksandbox.analyzer.data.LogicalFileData;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface LogicalFileRepository extends CrudRepository<LogicalFileData, UUID> {
    LogicalFileData findBySuffix(String suffix);
}
