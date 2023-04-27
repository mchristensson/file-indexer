package org.se.mac.blorksandbox.analyzer.repository;

import java.util.UUID;
import org.se.mac.blorksandbox.analyzer.data.DeviceData;
import org.springframework.data.repository.CrudRepository;

/**
 * Data Repository holding entities of type {@link DeviceData}.
 */
public interface DeviceRepository extends CrudRepository<DeviceData, UUID> {
}