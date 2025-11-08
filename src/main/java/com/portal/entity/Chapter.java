package com.portal.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chapter")
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String src;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "general_id")
    private General general;

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

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSrc() { return src; }
    public void setSrc(String src) { this.src = src; }

    public General getGeneral() { return general; }
    public void setGeneral(General general) { this.general = general; }

    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }
}