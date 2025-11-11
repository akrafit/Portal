package com.portal.repo;


import com.portal.entity.Document;
import com.portal.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByProject(Project project);
    List<Document> findByProjectOrderByName(Project project);
    Optional<Document> findByResourceId(String resourceId);
    boolean existsByResourceId(String resourceId);
}