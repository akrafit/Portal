package com.portal.repo;

import com.portal.entity.User;
import com.portal.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;



public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByYandexId(String yandexId);
    Optional<User> findByEmail(String email);
    List<User> findUserByRole(UserRole role);

    User findUserById(Long id);
}