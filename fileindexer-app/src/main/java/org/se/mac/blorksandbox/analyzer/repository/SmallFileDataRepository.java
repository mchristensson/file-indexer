package org.se.mac.blorksandbox.analyzer.repository;

import java.util.UUID;
import org.se.mac.blorksandbox.analyzer.data.SmallFileData;
import org.springframework.data.repository.CrudRepository;

/**
 * Data Repository holding entities of type {@link SmallFileData}.
 */
public interface SmallFileDataRepository extends CrudRepository<SmallFileData, UUID> {

}
