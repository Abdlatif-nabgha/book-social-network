package com.nabgha.book.user.infrastructure.persistence.jpa.entity;

import com.nabgha.book.book.infrastructure.persistence.jpa.entity.BookEntity;
import com.nabgha.book.common.infrastructure.persistence.BaseEntity;
import com.nabgha.book.role.infrastructure.persistence.jpa.entity.RoleEntity;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "_users")
public class UserEntity extends BaseEntity implements UserDetails, Principal {

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    @Column(unique = true)
    private String email;
    private String password;
    private boolean enabled;
    private boolean accountLocked;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<RoleEntity> roles;

    @OneToMany(mappedBy = "owner")
    private List<BookEntity> books;

    @OneToMany(mappedBy = "user")
    private List<BookTransactionHistoryEntity> histories;

    @Override
    public String getName() {
        return email;
    }

    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @NullMarked
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String fullName() {
        return firstName + " " + lastName;
    }

}
