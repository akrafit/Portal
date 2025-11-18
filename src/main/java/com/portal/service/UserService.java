package com.portal.service;

import com.portal.entity.User;
import com.portal.enums.UserRole;
import com.portal.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Метод для поиска по yandexId
    public Optional<User> findByYandexId(String yandexId) {
        return userRepository.findByYandexId(yandexId);
    }

    // Метод для сохранения пользователя
    public User save(User user) {
        return userRepository.save(user);
    }

    // Старый метод оставляем для обратной совместимости
    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String yandexId = oauth2User.getAttribute("login");

        return findByYandexId(yandexId)
                .orElseThrow(() -> new IllegalStateException("User not found in database"));
    }

    public List<User> getUsersByRole(UserRole contractor) {
        return userRepository.findUserByRole(contractor);
    }

    public User findById(Long userId) {
        return userRepository.findUserById(userId);
    }
}
