package com.nabgha.book.auth.infrastructure.persistence.jpa.mapper;

import com.nabgha.book.auth.domain.model.ActivationToken;
import com.nabgha.book.auth.infrastructure.persistence.jpa.entity.ActivationTokenEntity;
import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class ActivationTokenJpaMapper {

    public ActivationToken toDomain(ActivationTokenEntity entity) {
        return ActivationToken.reconstitute(
                entity.getId(),
                entity.getToken(),
                entity.getUser().getId(),
                entity.getCreatedAt(),
                entity.getExpiredAt(),
                entity.getValidatedAt()

        );
    }

    public ActivationTokenEntity toEntity(ActivationToken token, UserEntity user) {
        return ActivationTokenEntity.builder()
                .id(token.getId())
                .token(token.getCode())
                .createdAt(token.getCreatedAt())
                .expiredAt(token.getExpiredAt())
                .validatedAt(token.getValidatedAt())
                .user(user)
                .build();
    }

}
