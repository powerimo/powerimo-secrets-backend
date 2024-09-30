package org.powerimo.secret.api;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecretRequest {
    private String secret;
    private Long ttl;
    private Long hitLimit;
}
