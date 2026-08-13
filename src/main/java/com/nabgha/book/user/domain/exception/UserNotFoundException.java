package com.nabgha.book.user.domain.exception;

/**
 @auther abdlatif-nabgha
**/public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String message) {
    super(message);
  }
}
