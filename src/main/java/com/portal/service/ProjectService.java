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
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final LocalFileService localFileService;
    private final GeneralRepository generalRepository;
    private final ChapterService chapterService;
    private final SectionAssignmentService sectionAssignmentService;

    public ProjectService(ProjectRepository projectRepository, UserService userService, LocalFileService localFileService, GeneralRepository generalRepository, ChapterService chapterService, SectionAssignmentService sectionAssignmentService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.localFileService = localFileService;
        this.generalRepository = generalRepository;
        this.chapterService = chapterService;
        this.sectionAssignmentService = sectionAssignmentService;
    }

//    public YandexResponse createProject(Project project, Authentication authentication, Long generalId) {
//        if (generalId != null) {
//            General general = generalRepository.findById(generalId)
//                    .orElseThrow(() -> new RuntimeException("General not found with id: " + generalId));
//            project.setGeneral(general);
//        }
//        User currentUser = userService.getCurrentUser(authentication);
//        project.setCreatedBy(currentUser);
//        project.setCreatedAt(LocalDateTime.now());
//        projectRepository.save(project);
//        YandexResponse response = yandexDiskService.createFolderForProject(project);
//        if (response.getError() != null ){
//            projectRepository.delete(project);
//            return response;
//        }
//        return response;
//    }
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
    public boolean isSectionGenerated(Project project, Section section) {
        return project.getGeneratedSections().contains(section);
    }

    public Boolean markSectionAsGenerated(Project project, Section section) {
        project.addGeneratedSection(section);
        List<Chapter> chapterList = chapterService.getChaptersByGeneralTemplate(project.getGeneral(), section);
        Boolean result = chapterService.copyFromTemplateToProject(chapterList, project,section);
        if (result){
            projectRepository.save(project);
            return true;
        }
        return false;
    }

    public void markSectionAsNotGenerated(Project project, Section section) {
        project.removeGeneratedSection(section);
        projectRepository.save(project);
    }

    public Set<Long> getGeneratedSectionIds(Project project) {
        return project.getGeneratedSections().stream()
                .map(Section::getId)
                .collect(Collectors.toSet());
    }

    public List<Project> findWhereUserContractor(User user) {
        List<SectionAssignment> sectionAssignments = sectionAssignmentService.getAssignmentsForUser(user);
        return sectionAssignments.stream().map(SectionAssignment::getProject).toList();
    }

    public Long countProject() {
        return projectRepository.count();
    }
    /**
     * Новый метод для создания проекта с локальным файловым хранилищем
     */
    public void createProjectLocal(Project project, Authentication authentication, Long generalId) {
        if (generalId != null) {
            General general = generalRepository.findById(generalId)
                    .orElseThrow(() -> new RuntimeException("General not found with id: " + generalId));
            project.setGeneral(general);
        }
        User currentUser = userService.getCurrentUser(authentication);
        project.setCreatedBy(currentUser);
        project.setCreatedAt(LocalDateTime.now());

        // Сначала сохраняем проект чтобы получить ID
        projectRepository.save(project);

        try {
            // Создаем папку проекта в локальном хранилище
            localFileService.createProjectFolder(project.getId());
        } catch (Exception e) {
            // Если не удалось создать папку, удаляем проект и пробрасываем исключение
            projectRepository.delete(project);
            throw new RuntimeException("Не удалось создать файловую структуру проекта: " + e.getMessage());
        }
    }
}