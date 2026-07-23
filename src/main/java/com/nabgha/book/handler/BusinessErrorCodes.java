package com.nabgha.book.handler;


import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum BusinessErrorCodes {

    NO_CODE(0, NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, BAD_REQUEST, "New password does not match"),
    ACCOUNT_LOCKED(302, FORBIDDEN, "Account locked"),
    ACCOUNT_DISABLED(303, FORBIDDEN, "Account disabled"),
    BAD_CREDENTIALS(304, FORBIDDEN, "Login and / or password is incorrect"),
    EMAIL_ALREADY_EXISTS(305, CONFLICT, "Email already exists"),;

    private final int code;
    private final HttpStatus status;
    private final String description;

    BusinessErrorCodes(int code, HttpStatus status, String description) {
        this.code = code;
        this.status = status;
        this.description = description;
    }
}
