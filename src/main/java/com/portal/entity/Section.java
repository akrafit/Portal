package com.portal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "section")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "sections", fetch = FetchType.LAZY)
    private List<Chapter> chapters = new ArrayList<>();

    public Section() {}

    public Section(String name) {
        this.name = name;
    }

}