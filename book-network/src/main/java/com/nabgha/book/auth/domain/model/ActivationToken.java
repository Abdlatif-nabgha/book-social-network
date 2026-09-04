package com.nabgha.book.auth.domain.model;


import java.time.LocalDateTime;

public class ActivationToken {

    private final Integer id;
    private final String code;
    private final Integer userId;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiredAt;
    private LocalDateTime validatedAt;

    private ActivationToken(
            Integer id,
            String code,
            Integer userId,
            LocalDateTime createdAt,
            LocalDateTime expiredAt,
            LocalDateTime validatedAt
    ) {
        this.id = id;
        this.code = code;
        this.userId = userId;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.validatedAt = validatedAt;
    }

    public static ActivationToken create(String code, Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        return new ActivationToken(null,code,userId,now, now.plusMinutes(10),null);
    }

    public static ActivationToken reconstitute(Integer id, String code, Integer userId,
                                               LocalDateTime createdAt, LocalDateTime expiredAt,
                                               LocalDateTime validatedAt) {
        return new ActivationToken(id,code,userId,createdAt,expiredAt,validatedAt);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public void validate() {
        this.validatedAt = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public String getCode() { return code; }
    public Integer getUserId() { return userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiredAt() { return expiredAt; }
    public LocalDateTime getValidatedAt() { return validatedAt; }
}
