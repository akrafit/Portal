package com.portal.service;

import com.portal.dto.YandexResponse;
import com.portal.entity.*;
import com.portal.repo.GeneralRepository;
import com.portal.repo.ProjectRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final YandexDiskService yandexDiskService;
    private final GeneralRepository generalRepository;
    private final ChapterService chapterService;

    public ProjectService(ProjectRepository projectRepository, UserService userService, YandexDiskService yandexDiskService, GeneralRepository generalRepository, ChapterService chapterService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.yandexDiskService = yandexDiskService;
        this.generalRepository = generalRepository;
        this.chapterService = chapterService;
    }

    public YandexResponse createProject(Project project, Authentication authentication, Long generalId) {
        if (generalId != null) {
            General general = generalRepository.findById(generalId)
                    .orElseThrow(() -> new RuntimeException("General not found with id: " + generalId));
            project.setGeneral(general);
        }
        User currentUser = userService.getCurrentUser(authentication);
        project.setCreatedBy(currentUser);
        project.setCreatedAt(LocalDateTime.now());
        YandexResponse response = yandexDiskService.createFolderForProject(project);
        if (response.getError() != null ){
            return response;
        }
        projectRepository.save(project);
        return response;
    }
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public List<Project> findByUser(User user) {
        return projectRepository.findByCreatedBy(user);
    }

    public List<Section> getAllSections(Project project) {
        if (project == null || project.getGeneral() == null) {
            return new ArrayList<>();
        }
        Set<Section> sections = new HashSet<>();
        List<Chapter> chapters = chapterService.getChaptersByGeneral(project.getGeneral().getId());

        for (Chapter chapter : chapters) {
            sections.addAll(chapter.getSections());
        }

        return new ArrayList<>(sections);
    }
}