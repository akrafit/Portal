package com.portal.repo;

import com.portal.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByGeneralId(Long generalId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Chapter c JOIN c.sections s WHERE c.id = :chapterId AND s.id = :sectionId")
    boolean existsChapterSectionRelation(@Param("chapterId") Long chapterId,
                                         @Param("sectionId") Long sectionId);
}