package org.powerimo.secret.server;

import jakarta.servlet.http.HttpServletRequest;
import org.powerimo.secret.server.models.UserBrowserInfo;

import java.time.Instant;
import java.time.ZoneOffset;

public class AppUtils {

    public static UserBrowserInfo extractUserBrowserInfo(HttpServletRequest request) {
        return new UserBrowserInfo(request);
    }

    public static Instant nowUtc() {
        return Instant.now().atOffset(ZoneOffset.UTC).toInstant();
    }

}
