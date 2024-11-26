package org.powerimo.secret.server.persistance.repositories;

import org.powerimo.secret.server.persistance.entities.SecretActionEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface SecretActionRepository extends CrudRepository<SecretActionEntity, UUID> {
    void deleteAllBySecretId(UUID secretId);

    @Modifying
    @Query(nativeQuery = true, value = """
delete
from secret_action
where secret_id in (select id from secret where expires_at < :expiresAt)""")
    int deleteExpired(@Param("expiresAt") Instant expiresAt);

    @Query(nativeQuery = true, value = "delete from secret_action where secret_id in (select id from secret where hit_count>=hit_limit)")
    @Modifying
    int deleteUsedSecretByHits();
}
