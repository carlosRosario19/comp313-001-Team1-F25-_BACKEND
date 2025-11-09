package com.centennial.gamepickd.util;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SecurityUtils {

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object principal = auth.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return jwt.getClaimAsString("sub");
        }

        return auth.getName(); // fallback (in case principal is not a Jwt)
    }

    public boolean hasRole(String roleName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) return false;

        Object authorities = jwt.getClaim("authorities");
        if (authorities instanceof String rolesString) {
            List<String> roles = List.of(rolesString.split(" "));
            return roles.contains(roleName);
        }
        return false;
    }
}
