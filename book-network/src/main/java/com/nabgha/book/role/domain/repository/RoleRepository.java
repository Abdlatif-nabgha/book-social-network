package com.nabgha.book.role.domain.repository;

import com.nabgha.book.role.domain.model.Role;

import java.util.Optional;

public interface RoleRepository {
    Role save(Role role);
    Optional<Role> findByName(String name);
}