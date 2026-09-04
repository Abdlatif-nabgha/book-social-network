package com.nabgha.book.shared.config;


import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@NullMarked
public class ApplicationAuditAware implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (
                        authentication == null ||
                        !authentication.isAuthenticated() ||
                        authentication instanceof AnonymousAuthenticationToken
        ){
            return Optional.empty();
        }
        UserEntity userPrincipal = (UserEntity) authentication.getPrincipal();
        if (userPrincipal == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(userPrincipal.getId());
    }
}
