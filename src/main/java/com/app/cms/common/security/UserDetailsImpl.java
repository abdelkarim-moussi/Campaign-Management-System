package com.app.cms.common.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
public class UserDetailsImpl implements UserDetails {
    private final String userId;
    private final String userEmail;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(UserEntity user) {
        this.userId = user.getId();
        this.userEmail = user.getEmail(); // Use email as username
        this.password = user.getPassword();
        Set<GrantedAuthority> auths = new HashSet<>();

        for (RoleEntity role : user.getRoles()){
            auths.add(new SimpleGrantedAuthority("ROLE_"+role.getName()));
            for (PermissionEntity permission : role.getPermissions()){
                auths.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }

        this.authorities = auths;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
