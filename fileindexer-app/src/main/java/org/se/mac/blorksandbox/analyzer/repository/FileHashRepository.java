package org.se.mac.blorksandbox.analyzer.repository;

import java.util.UUID;
import org.se.mac.blorksandbox.analyzer.data.FileHashData;
import org.springframework.data.repository.CrudRepository;

/**
 * Data Repository holding entities of type {@link FileHashData}.
 */
public interface FileHashRepository extends CrudRepository<FileHashData, UUID> {

}
