package com.nabgha.book.role.infrastructure.persistence.jpa.adapter;

import com.nabgha.book.role.domain.model.Role;
import com.nabgha.book.role.domain.repository.RoleRepository;
import com.nabgha.book.role.infrastructure.persistence.jpa.entity.RoleEntity;
import com.nabgha.book.role.infrastructure.persistence.jpa.mapper.RoleJpaMapper;
import com.nabgha.book.role.infrastructure.persistence.jpa.repository.RoleJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;
    private final RoleJpaMapper roleJpaMapper;

    public RoleRepositoryAdapter(RoleJpaRepository roleJpaRepository, RoleJpaMapper roleJpaMapper) {
        this.roleJpaRepository = roleJpaRepository;
        this.roleJpaMapper = roleJpaMapper;
    }

    @Override
    @Transactional
    public Role save(Role role) {
        RoleEntity entity = roleJpaMapper.toEntity(role);
        RoleEntity saved = roleJpaRepository.save(entity);
        return roleJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name).map(roleJpaMapper::toDomain);
    }
}