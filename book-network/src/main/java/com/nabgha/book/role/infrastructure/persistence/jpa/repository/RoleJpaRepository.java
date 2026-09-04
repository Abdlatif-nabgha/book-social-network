package com.nabgha.book.role.infrastructure.persistence.jpa.repository;


import com.nabgha.book.role.domain.model.Role;
import com.nabgha.book.role.infrastructure.persistence.jpa.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByName(String name);
}
