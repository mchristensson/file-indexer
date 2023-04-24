package org.se.mac.blorksandbox.analyzer.repository;

import org.se.mac.blorksandbox.analyzer.data.DeviceData;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DeviceRepository extends CrudRepository<DeviceData, UUID> {
}