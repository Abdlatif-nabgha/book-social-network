package com.nabgha.book.user.infrastructure.persistence.jpa.mapper;

import com.nabgha.book.role.infrastructure.persistence.jpa.entity.RoleEntity;
import com.nabgha.book.user.domain.model.Email;
import com.nabgha.book.user.domain.model.User;
import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserJpaMapper {

    public User toDomain(UserEntity entity) {
        List<String> roleNames = entity.getRoles() == null
                ? List.of()
                : entity.getRoles().stream().map(RoleEntity::getName).toList();

        return User.reconstitute(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBirthDate(),
                new Email(entity.getEmail()),
                entity.getPassword(),
                entity.isEnabled(),
                entity.isAccountLocked(),
                roleNames,
                entity.getCreationDate()
        );
    }

    public UserEntity toEntity(User user, List<RoleEntity> roles) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setBirthDate(user.getBirthDate());
        entity.setEmail(user.getEmail().getValue());
        entity.setPassword(user.getPassword());
        entity.setEnabled(user.isEnabled());
        entity.setAccountLocked(user.isAccountLocked());
        entity.setRoles(roles);
        return entity;
    }
}