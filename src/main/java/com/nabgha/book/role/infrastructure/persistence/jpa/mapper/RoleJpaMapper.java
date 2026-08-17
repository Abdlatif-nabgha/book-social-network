package com.nabgha.book.role.infrastructure.persistence.jpa.mapper;

import com.nabgha.book.role.domain.model.Role;
import com.nabgha.book.role.infrastructure.persistence.jpa.entity.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleJpaMapper {

    public Role toDomain(RoleEntity role) {
        return Role.reconstitute(role.getId(), role.getName());
    }

    public RoleEntity toEntity(Role role) {
        return RoleEntity
                .builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}
