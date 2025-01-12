package org.powerimo.secret.api.models;

import lombok.*;

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
    private String password;
}
