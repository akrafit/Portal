package com.portal.entity;

import com.portal.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "yandex_id")
    private String yandexId;

    private String email;
    private String name;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "user_id")
    private String yandexUserId;

    // Конструкторы
    public User() {}

    public User(String yandexId, String email, String name, UserRole role) {
        this.yandexId = yandexId;
        this.email = email;
        this.name = name;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getYandexId() {
        return yandexId;
    }

    public void setYandexId(String yandexId) {
        this.yandexId = yandexId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setYandexUserId(String yandexUserId) {
        this.yandexUserId = yandexUserId;
    }

    public String getYandexUserId() {
        return yandexUserId;
    }
}

