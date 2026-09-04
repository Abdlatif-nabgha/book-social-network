package com.nabgha.book.user.domain.model;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class User {

    private final Integer id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private final Email email;
    private String password;
    private boolean enabled;
    private boolean accountLocked;
    private final List<String> rolesName;
    private final LocalDateTime createAt;

    private User(Builder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.birthDate = builder.birthDate;
        this.email = builder.email;
        this.password = builder.password;
        this.enabled = builder.enabled;
        this.accountLocked = builder.accountLocked;
        this.rolesName = builder.roleNames;
        this.createAt = builder.createdAt;
    }
    public static User register(String firstName, String lastName, Email email,
                                String encodedPassword) {
        return new Builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(encodedPassword)
                .enabled(false)
                .accountLocked(false)
                .roleNames(List.of("USER"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User reconstitute(Integer id, String firstName, String lastName,
                                    LocalDate birthDate, Email email, String password,
                                    boolean enabled, boolean accountLocked,
                                    List<String> roleNames, LocalDateTime createdAt) {
        return new Builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .birthDate(birthDate)
                .email(email)
                .password(password)
                .enabled(enabled)
                .accountLocked(accountLocked)
                .roleNames(roleNames)
                .createdAt(createdAt)
                .build();
    }

    // ---- Domain behavior ----

    public void activate() {
        this.enabled = true;
    }

    public boolean isAdmin() {
        return rolesName.contains("ADMIN");
    }

    public String fullName() {
        return firstName + " " + lastName;
    }

    // ---- Getters ----

    public Integer getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getBirthDate() { return birthDate; }
    public Email getEmail() { return email; }
    public String getPassword() { return password; }
    public boolean isEnabled() { return enabled; }
    public boolean isAccountLocked() { return accountLocked; }
    public List<String> getRoleNames() { return rolesName; }
    public LocalDateTime getCreatedAt() { return createAt; }

    private static class Builder {
        private Integer id;
        private String firstName;
        private String lastName;
        private LocalDate birthDate;
        private Email email;
        private String password;
        private boolean enabled;
        private boolean accountLocked;
        private List<String> roleNames;
        private LocalDateTime createdAt;

        Builder id(Integer id) { this.id = id; return this; }
        Builder firstName(String v) { this.firstName = v; return this; }
        Builder lastName(String v) { this.lastName = v; return this; }
        Builder birthDate(LocalDate v) { this.birthDate = v; return this; }
        Builder email(Email v) { this.email = v; return this; }
        Builder password(String v) { this.password = v; return this; }
        Builder enabled(boolean v) { this.enabled = v; return this; }
        Builder accountLocked(boolean v) { this.accountLocked = v; return this; }
        Builder roleNames(List<String> v) { this.roleNames = v; return this; }
        Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }

        User build() {
            if (email == null) throw new IllegalStateException("User must have an email");
            if (password == null || password.isBlank())
                throw new IllegalStateException("User must have a password");
            return new User(this);
        }
    }

}
