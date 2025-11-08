package com.portal.service;

import com.portal.entity.User;
import com.portal.enums.UserRole;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String yandexUserId = oauth2User.getAttribute("id"); // user_id Яндекс
        String yandexId = oauth2User.getAttribute("login");
        String email = oauth2User.getAttribute("default_email");
        String name = oauth2User.getAttribute("real_name") != null ?
                oauth2User.getAttribute("real_name") :
                oauth2User.getAttribute("display_name");

        // Получаем пользователя из БД по yandexId
        Optional<User> userOptional = userService.findByYandexId(yandexId);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            // Создаем нового пользователя
            user = new User();
            user.setYandexUserId(yandexUserId);
            user.setYandexId(yandexId);
            user.setEmail(email);
            user.setName(name);
            user.setRole(UserRole.EMPLOYEE); // По умолчанию EMPLOYEE
            user.setCreatedAt(LocalDateTime.now());
            user = userService.save(user);
        }

        // Создаем кастомного пользователя с ролями из БД
        return new CustomOAuth2User(oauth2User, user.getRole(), user);
    }
}
