package org.powerimo.secret.server.persistance.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.powerimo.secret.api.models.SecretInfo;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "secret")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecretEntity {
    @Id
    private UUID id;
    private String code;
    private String data;
    private Instant expiresAt;
    private Long hitCount;
    private Long hitLimit;
    private String registrarHost;
    private String registrarAgent;
    private Instant createdAt;
    private Instant updatedAt;
    private String linkPassword;

    public SecretInfo dto() {
        return SecretInfo.builder()
                .expiresAt(expiresAt)
                .hitCount(hitCount)
                .hitLimit(hitLimit)
                .code(code)
                .createdAt(createdAt)
                .build();
    }
}
