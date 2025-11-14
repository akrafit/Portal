package com.portal.entity;

import com.portal.dto.YandexDiskItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "chapter")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;


    @Column(columnDefinition = "TEXT", length = 10000)
    private String path;

    private String created;
    private String modified;
    private Long size;

    @Column(columnDefinition = "TEXT", length = 10000)
    private String resourceId;

    @Column(nullable = false, columnDefinition = "TEXT", length = 1000)
    private String src;

    @Column(name = "url", columnDefinition = "TEXT", length = 1000)
    private String publicUrl;

    @Column(name = "template")
    private boolean template;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "general_id")
    private General general;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToMany
    @JoinTable(
            name = "chapter_section",
            joinColumns = @JoinColumn(name = "chapter_id"),
            inverseJoinColumns = @JoinColumn(name = "section_id")
    )
    private List<Section> sections = new ArrayList<>();

    public Chapter() {}

    public Chapter(String name, String src, General general) {
        this.name = name;
        this.src = src;
        this.general = general;
    }

    public Chapter(YandexDiskItem item, Project project) {
        this.name = item.getName();
        this.path = item.getPath();
        this.resourceId = item.getResourceId();
        this.created = item.getCreated();
        this.modified = item.getModified();
        this.publicUrl = item.getPublicUrl();
        this.project = project;
        this.src = item.getFile() != null ? item.getFile() : item.getPreview();
        this.size = item.getSize();
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public String getPublicUrl() {
        return publicUrl;
    }
}