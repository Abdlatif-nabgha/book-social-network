package com.nabgha.book.auth.domain.repository;


public interface ActivationCodeGeneratorPort {
    String generate(int length);
}
