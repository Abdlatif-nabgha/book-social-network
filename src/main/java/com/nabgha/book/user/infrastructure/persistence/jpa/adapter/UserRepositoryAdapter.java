package com.nabgha.book.user.infrastructure.persistence.jpa.adapter;

import com.nabgha.book.role.infrastructure.persistence.jpa.entity.RoleEntity;
import com.nabgha.book.role.infrastructure.persistence.jpa.repository.RoleJpaRepository;
import com.nabgha.book.user.domain.model.Email;
import com.nabgha.book.user.domain.model.User;
import com.nabgha.book.user.domain.repository.UserRepository;
import com.nabgha.book.user.infrastructure.persistence.jpa.entity.UserEntity;
import com.nabgha.book.user.infrastructure.persistence.jpa.mapper.UserJpaMapper;
import com.nabgha.book.user.infrastructure.persistence.jpa.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserJpaMapper userJpaMapper;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository,
                                 RoleJpaRepository roleJpaRepository,
                                 UserJpaMapper userJpaMapper) {
        this.userJpaRepository = userJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
        this.userJpaMapper = userJpaMapper;
    }

    @Override
    public User save(User user) {
        List<RoleEntity> roles = user.getRoleNames().stream()
                .map(name -> roleJpaRepository.findByName(name)
                        .orElseThrow(() -> new IllegalStateException("Role not found: " + name)))
                .toList();
        UserEntity entity = userJpaMapper.toEntity(user, roles);
        UserEntity saved = userJpaRepository.save(entity);
        return userJpaMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userJpaRepository.findById(id).map(userJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return userJpaRepository.findByEmail(email.getValue()).map(userJpaMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userJpaRepository.existsByEmail(email.getValue());
    }

    @Override
    public long countAll() {
        return userJpaRepository.count();
    }
}