package org.powerimo.secret.server.models;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBrowserInfo {
    private String remoteHost;
    private String userAgent;
    private String browserName;
    private String browserVersion;
}
