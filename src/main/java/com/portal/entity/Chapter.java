package com.portal.entity;

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
}