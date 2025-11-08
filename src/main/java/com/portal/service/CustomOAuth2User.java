package com.portal.service;

import com.portal.entity.User;
import com.portal.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oauth2User;
    private final UserRole role;
    private final User user;

    public CustomOAuth2User(OAuth2User oauth2User, UserRole role, User user) {
        this.oauth2User = oauth2User;
        this.role = role;
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Преобразуем нашу роль в Spring Security Authority
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getName() {
        return oauth2User.getAttribute("login");
    }

    public UserRole getRole() {
        return role;
    }
    public String getYandexUserId() {
        return user.getYandexId();
    }
}
