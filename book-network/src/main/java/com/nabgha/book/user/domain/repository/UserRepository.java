package com.nabgha.book.user.domain.repository;


import com.nabgha.book.user.domain.model.Email;
import com.nabgha.book.user.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Integer id);

    Optional<User> findByEmail(Email email);

    boolean existsByEmail(Email email);

    long countAll();
}
