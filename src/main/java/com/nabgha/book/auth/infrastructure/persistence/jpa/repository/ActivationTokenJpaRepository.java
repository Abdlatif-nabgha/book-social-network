package com.nabgha.book.auth.infrastructure.persistence.jpa.repository;

import com.nabgha.book.auth.infrastructure.persistence.jpa.entity.ActivationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivationTokenJpaRepository extends JpaRepository<ActivationTokenEntity, Integer> {
    Optional<ActivationTokenEntity> findByToken(String token);
}