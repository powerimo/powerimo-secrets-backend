package org.powerimo.secret.server;

import jakarta.servlet.http.HttpServletRequest;
import org.powerimo.secret.server.models.UserBrowserInfo;

public class AppUtils {

    public static UserBrowserInfo extractUserBrowserInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String browser = null;
        String browserVersion = null;

        if (userAgent != null) {
            String[] userAgentParts = userAgent.split("/", 2);

            if (userAgentParts.length == 2) {
                browser = userAgentParts[0];
                browserVersion = userAgentParts[1];
            } else {
                browser = userAgent;
            }
        }

        return UserBrowserInfo.builder()
                .remoteHost(request.getRemoteAddr())
                .userAgent(userAgent)
                .browserName(browser)
                .browserVersion(browserVersion)
                .build();
    }

}
