package com.nabgha.book.role.domain.model;

public class Role {

    private final Integer id;
    private final String name;

    private Role(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Role create(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Role name cannot be blank");
        }
        return new Role(null, name);
    }

    public static Role reconstitute(Integer id, String name) {
        return new Role(id, name);
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
}