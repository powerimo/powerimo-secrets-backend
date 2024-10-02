package org.powerimo.secret.server.persistance.repositories;

import org.powerimo.secret.server.persistance.entities.SecretEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SecretRepository extends CrudRepository<SecretEntity, UUID> {

    Optional<SecretEntity> findByCode(String code);

    @Query(nativeQuery = true, value = "delete from secret where expires_at < :expiresAt")
    @Modifying
    int deleteExpired(@Param("expiresAt") Instant expiresAt);

    @Query(nativeQuery = true, value = "delete from secret where hit_count>=hit_limit")
    @Modifying
    int deleteUsedSecretByHits();
}
