//package com.portal.entity;
//
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "documents")
//public class Document {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String name;
//
//    private String description;
//
//    @Enumerated(EnumType.STRING)
//    private DocumentType type;
//
//    @Enumerated(EnumType.STRING)
//    private DocumentStatus status;
//
//    @Column(name = "yandex_path")
//    private String yandexPath;
//
//    @Column(name = "yandex_url")
//    private String yandexUrl;
//
//    @Column(name = "public_url")
//    private String publicUrl;
//
//    private Long size;
//
//    @Column(name = "mime_type")
//    private String mimeType;
//
//    @Column(name = "resource_id")
//    private String resourceId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "project_id")
//    private Project project;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "assigned_to")
//    private User assignedTo;
//
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    @Column(name = "modified_at")
//    private LocalDateTime modifiedAt;
//
//    // Конструкторы
//    public Document() {}
//
//    public Document(String name, DocumentType type, Project project) {
//        this.name = name;
//        this.type = type;
//        this.project = project;
//        this.status = DocumentStatus.IN_WORK;
//        this.createdAt = LocalDateTime.now();
//    }
//
//    // Геттеры и сеттеры
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//
//    public String getDescription() { return description; }
//    public void setDescription(String description) { this.description = description; }
//
//    public DocumentType getType() { return type; }
//    public void setType(DocumentType type) { this.type = type; }
//
//    public DocumentStatus getStatus() { return status; }
//    public void setStatus(DocumentStatus status) { this.status = status; }
//
//    public String getYandexPath() { return yandexPath; }
//    public void setYandexPath(String yandexPath) { this.yandexPath = yandexPath; }
//
//    public String getYandexUrl() { return yandexUrl; }
//    public void setYandexUrl(String yandexUrl) { this.yandexUrl = yandexUrl; }
//
//    public String getPublicUrl() { return publicUrl; }
//    public void setPublicUrl(String publicUrl) { this.publicUrl = publicUrl; }
//
//    public Long getSize() { return size; }
//    public void setSize(Long size) { this.size = size; }
//
//    public String getMimeType() { return mimeType; }
//    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
//
//    public String getResourceId() { return resourceId; }
//    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
//
//    public Project getProject() { return project; }
//    public void setProject(Project project) { this.project = project; }
//
//    public User getAssignedTo() { return assignedTo; }
//    public void setAssignedTo(User assignedTo) { this.assignedTo = assignedTo; }
//
//    public LocalDateTime getCreatedAt() { return createdAt; }
//    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
//
//    public LocalDateTime getModifiedAt() { return modifiedAt; }
//    public void setModifiedAt(LocalDateTime modifiedAt) { this.modifiedAt = modifiedAt; }
//}