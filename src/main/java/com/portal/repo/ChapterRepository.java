package com.portal.repo;

import com.portal.entity.Chapter;
import com.portal.entity.General;
import com.portal.entity.Project;
import com.portal.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByGeneralId(Long generalId);
    List<Chapter> findByGeneralAndTemplateTrue(General general);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Chapter c JOIN c.sections s WHERE c.id = :chapterId AND s.id = :sectionId")
    boolean existsChapterSectionRelation(@Param("chapterId") Long chapterId,
                                         @Param("sectionId") Long sectionId);

    List<Chapter> findChaptersByProject(Project project);

    @Query("SELECT c FROM Chapter c JOIN c.sections s WHERE c.project = :project AND s.id = :sectionId")
    List<Chapter> findChaptersByProjectAndSection(@Param("project") Project project,
                                                  @Param("sectionId") Long sectionId);
    Optional<Chapter> findByResourceId(String resourceId);

    @Query("SELECT DISTINCT c FROM Chapter c " +
            "JOIN c.sections s " +
            "WHERE c.general = :general AND c.template = true AND s = :section")
    List<Chapter> findByGeneralAndTemplateTrueAndContainingSection(@Param("general") General general,
                                                                   @Param("section") Section section);

    Long countChapterByProject(Project project);

    Optional<Chapter> findByProjectAndName(Project project, String name);
}