package org.powerimo.secret.server.persistance.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

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
    private Integer hitCount;
    private Integer hitLimit;
    private String registrarHost;
    private String registrarBrowserName;
    private String registrarBrowserVersion;
    private Instant createdAt;
    private Instant updatedAt;
}
