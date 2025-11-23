package com.portal.repo;

import com.portal.entity.ChapterBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterBookmarkRepository extends JpaRepository<ChapterBookmark, Long> {

    List<ChapterBookmark> findByBookmarkNameIn(List<String> bookmarkNames);

    Optional<ChapterBookmark> findByBookmarkName(String bookmarkName);
}