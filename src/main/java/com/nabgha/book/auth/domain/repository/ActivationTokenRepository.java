package com.nabgha.book.auth.domain.repository;


import com.nabgha.book.auth.domain.model.ActivationToken;

import java.util.Optional;

public interface ActivationTokenRepository {

    ActivationToken save(ActivationToken token);
    Optional<ActivationToken> findByCode(String code);
}
