package com.nabgha.book.auth.infrastructure.persistence.jpa.adapter;

import com.nabgha.book.auth.domain.model.ActivationToken;
import com.nabgha.book.auth.domain.repository.ActivationTokenRepository;
import com.nabgha.book.auth.infrastructure.persistence.jpa.entity.ActivationTokenEntity;
import com.nabgha.book.auth.infrastructure.persistence.jpa.mapper.ActivationTokenJpaMapper;
import com.nabgha.book.auth.infrastructure.persistence.jpa.repository.ActivationTokenJpaRepository;
import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import com.nabgha.book.user.infrastructure.persistence.jpa.repository.UserJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional(readOnly = true)
public class ActivationTokenRepositoryAdapter implements ActivationTokenRepository {

    private final ActivationTokenJpaRepository tokenJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ActivationTokenJpaMapper mapper;

    public ActivationTokenRepositoryAdapter(ActivationTokenJpaRepository tokenJpaRepository,
                                            UserJpaRepository userJpaRepository,
                                            ActivationTokenJpaMapper mapper) {
        this.tokenJpaRepository = tokenJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ActivationToken save(ActivationToken token) {
        UserEntity user = userJpaRepository.findById(token.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found with id: " + token.getUserId()));
        ActivationTokenEntity entity = mapper.toEntity(token, user);
        ActivationTokenEntity saved = tokenJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    public Optional<ActivationToken> findByCode(String code) {
        return tokenJpaRepository.findByToken(code).map(mapper::toDomain);
    }
}