package org.se.mac.blorksandbox.analyzer.repository;

import java.util.UUID;
import org.se.mac.blorksandbox.analyzer.data.FileMetaData;
import org.springframework.data.repository.CrudRepository;

/**
 * Data Repository holding entities of type {@link FileMetaData}.
 */
public interface LogicalFileRepository extends CrudRepository<FileMetaData, UUID> {

}
