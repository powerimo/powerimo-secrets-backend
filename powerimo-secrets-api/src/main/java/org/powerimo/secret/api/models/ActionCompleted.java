package org.powerimo.secret.api.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
@Builder
public class ActionCompleted {
    private String action;
    private String status;
    private String message;
    private Instant timestamp;
}
