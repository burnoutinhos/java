package com.burnoutinhos.burnoutinhos_api.config;

import com.burnoutinhos.burnoutinhos_api.exceptions.ConversionErrorException;
import com.burnoutinhos.burnoutinhos_api.exceptions.UserNotAuthorizedException;
import com.burnoutinhos.burnoutinhos_api.model.AppUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtil {

    public static Long extractUserIdFromToken() {
        Authentication auth =
            SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new UserNotAuthorizedException("Not authenticated");
        }

        Object principal = auth.getPrincipal();

        if (!(principal instanceof AppUser)) {
            throw new ConversionErrorException("Couldn't convert to user");
        }

        AppUser user = (AppUser) principal;
        return user.getId();
    }

    public static AppUser extractUserFromToken() {
        Authentication auth =
            SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new UserNotAuthorizedException("Not authenticated");
        }

        Object principal = auth.getPrincipal();

        if (!(principal instanceof AppUser)) {
            throw new ConversionErrorException("Couldn't convert to user");
        }

        AppUser user = (AppUser) principal;
        return user;
    }
}
