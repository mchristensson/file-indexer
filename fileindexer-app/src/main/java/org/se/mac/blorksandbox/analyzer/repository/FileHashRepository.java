package org.se.mac.blorksandbox.analyzer.repository;

import org.se.mac.blorksandbox.analyzer.data.FileHashData;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface FileHashRepository extends CrudRepository<FileHashData, UUID> {

}
