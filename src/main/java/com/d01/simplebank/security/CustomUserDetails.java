package com.d01.simplebank.security;

import com.d01.simplebank.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    
    private final String id;
    private final String email;
    private final String password;
    private final String role;
    private final LocalDateTime createdDate;
    private final LocalDateTime updatedDate;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;
    
    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.createdDate = user.getCreatedDate();
        this.updatedDate = user.getUpdatedDate();
        this.enabled = true; // You can add an enabled field to User entity if needed
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }
    
    // Custom getters for additional data
    public String getId() {
        return id;
    }
    
    public String getRole() {
        return role;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    
    // Standard UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
} 