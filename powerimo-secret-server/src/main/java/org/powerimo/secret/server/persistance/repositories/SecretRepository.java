package org.powerimo.secret.server.persistance.repositories;

import org.powerimo.secret.server.persistance.entities.SecretEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface SecretRepository extends CrudRepository<SecretEntity, UUID> {

    Optional<SecretEntity> findByCode(String code);
}
