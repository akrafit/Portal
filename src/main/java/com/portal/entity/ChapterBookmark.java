package com.portal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "chapter_bookmarks")
@Data
public class ChapterBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bookmark_name")
    private String bookmarkName;

    @Column(name = "chapter_title")
    private String chapterTitle;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "path")
    private String path;
    @Column(name = "created_date")
    private LocalDateTime createdDate;


    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }
}