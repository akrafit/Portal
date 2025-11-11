package com.portal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private DocumentType type;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    @Column(name = "yandex_path")
    private String yandexPath;

    @Column(name = "yandex_url")
    private String yandexUrl;

    @Column(name = "public_url")
    private String publicUrl;

    private Long size;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "resource_id")
    private String resourceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    // Конструкторы
    public Document() {}

    public Document(String name, DocumentType type, Project project) {
        this.name = name;
        this.type = type;
        this.project = project;
        this.status = DocumentStatus.IN_WORK;
        this.createdAt = LocalDateTime.now();
    }
}