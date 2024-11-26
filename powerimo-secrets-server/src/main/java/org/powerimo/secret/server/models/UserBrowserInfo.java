package org.powerimo.secret.server.models;

import jakarta.servlet.http.HttpServletRequest;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserBrowserInfo {
    private String remoteHost;
    private String userAgent;

    public UserBrowserInfo(HttpServletRequest request) {
        userAgent = request.getHeader("User-Agent");
        remoteHost = request.getRemoteHost();
    }
}
