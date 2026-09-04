package com.nabgha.book.auth.domain.repository;


public interface AuthenticatorPort {
    Integer authenticate(String email, String password);
}
