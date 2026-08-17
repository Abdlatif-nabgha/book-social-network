package com.nabgha.book.auth.domain.repository;


public interface PasswordEncoderPort {
    String encode(String rawPassword);
}
