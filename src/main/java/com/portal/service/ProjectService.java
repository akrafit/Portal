package com.portal.service;

import com.portal.dto.YandexResponse;
import com.portal.entity.Project;
import com.portal.entity.User;
import com.portal.repo.ProjectRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final YandexDiskService yandexDiskService;

    public ProjectService(ProjectRepository projectRepository, UserService userService, YandexDiskService yandexDiskService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.yandexDiskService = yandexDiskService;
    }

    public YandexResponse createProject(Project project, Authentication authentication) {
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
}