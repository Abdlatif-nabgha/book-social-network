package com.nabgha.book.auth.domain.repository;


import java.util.List;

public interface TokenGeneratorPort {
    String generateToken(Integer userId, String email, String fullName, List<String> roles);
}
