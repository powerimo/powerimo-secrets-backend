package org.powerimo.secret.server.persistance.entities;

import jakarta.persistence.*;
import lombok.*;
import org.powerimo.secret.api.enums.SecretActionEnum;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "secret_action")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecretActionEntity {
    @Id
    private UUID id;
    private UUID secretId;

    @Enumerated(EnumType.STRING)
    private SecretActionEnum action;

    private String details;
    private String agent;
    private String host;
    private Instant actionTime;
}
