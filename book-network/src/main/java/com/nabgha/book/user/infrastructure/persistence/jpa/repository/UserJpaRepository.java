package com.nabgha.book.user.infrastructure.persistence.jpa.repository;

import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}