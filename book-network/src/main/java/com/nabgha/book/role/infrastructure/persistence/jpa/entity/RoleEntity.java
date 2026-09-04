package com.nabgha.book.role.infrastructure.persistence.jpa.entity;

import com.nabgha.book.common.infrastructure.persistence.BaseEntity;
import jakarta.persistence.Column;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class RoleEntity extends BaseEntity {

    @Column(unique = true)
    private String name;
}
