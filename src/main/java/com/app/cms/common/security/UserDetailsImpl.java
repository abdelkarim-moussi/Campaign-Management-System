package com.app.cms.common.security;

import com.app.cms.user.entity.User;
import com.app.cms.user.entity.UserStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
public class UserDetailsImpl implements UserDetails {
    private final Long userId;
    private final Long organizationId;
    private final String userEmail;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;

    public UserDetailsImpl(User user) {
        this.userId = user.getId();
        this.organizationId = user.getOrganization().getId();
        this.userEmail = user.getEmail();
        this.password = user.getPassword();

        Set<GrantedAuthority> auths = new HashSet<>();
        auths.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        this.authorities = auths;

        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = user.getStatus() == UserStatus.ACTIVE;
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
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
