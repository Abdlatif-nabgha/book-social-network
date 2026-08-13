package com.nabgha.book.auth.domain.exception;

/**
 @auther abdlatif-nabgha
**/public class ActivationTokenNotFoundException extends RuntimeException {
  public ActivationTokenNotFoundException(String message) {
    super(message);
  }
}
