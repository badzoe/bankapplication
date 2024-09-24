package com.bankapp.bankapplication.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class UserDetails {
    public abstract String getPassword();

    public abstract String getUsername();

    public abstract boolean isAccountNonExpired();

    public abstract boolean isAccountNonLocked();

    public abstract boolean isCredentialsNonExpired();

    public abstract boolean isEnabled();
}
