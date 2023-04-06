package org.se.mac.blorksandbox.analyzer.repository;

import org.se.mac.blorksandbox.analyzer.data.SmallFileData;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SmallFileDataRepository extends CrudRepository<SmallFileData, UUID> {

}
