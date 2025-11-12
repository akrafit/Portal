package com.portal;

import com.portal.dto.YandexResponse;
import com.portal.entity.*;
import com.portal.repo.GeneralRepository;
import com.portal.repo.ProjectRepository;
import com.portal.service.ChapterService;
import com.portal.service.ProjectService;
import com.portal.service.UserService;
import com.portal.service.YandexDiskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserService userService;

    @Mock
    private YandexDiskService yandexDiskService;

    @Mock
    private GeneralRepository generalRepository;

    @Mock
    private ChapterService chapterService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProjectService projectService;

    private Project project;
    private User user;
    private General general;
    private Chapter chapter;
    private Section section1, section2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        general = new General();
        general.setId(1L);
        general.setName("Test General");

        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setCreatedBy(user);
        project.setCreatedAt(LocalDateTime.now());
        project.setGeneral(general);

        section1 = new Section();
        section1.setId(1L);
        section1.setName("Section 1");

        section2 = new Section();
        section2.setId(2L);
        section2.setName("Section 2");

        chapter = new Chapter();
        chapter.setId(1L);
        chapter.setGeneral(general);
        chapter.setSections(Arrays.asList(section1, section2));
    }

    @Test
    void createProject_WithGeneralId_ShouldCreateProjectSuccessfully() {
        // Arrange
        Long generalId = 1L;
        YandexResponse yandexResponse = new YandexResponse();
        yandexResponse.setSuccess(true);

        when(generalRepository.findById(generalId)).thenReturn(Optional.of(general));
        when(userService.getCurrentUser(authentication)).thenReturn(user);
        when(yandexDiskService.createFolderForProject(project)).thenReturn(yandexResponse);
        when(projectRepository.save(project)).thenReturn(project);

        // Act
        YandexResponse response = projectService.createProject(project, authentication, generalId);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(general, project.getGeneral());
        assertEquals(user, project.getCreatedBy());
        assertNotNull(project.getCreatedAt());
        verify(projectRepository, times(1)).save(project);
        verify(yandexDiskService, times(1)).createFolderForProject(project);
    }

    @Test
    void createProject_WithYandexError_ShouldReturnErrorResponse() {
        // Arrange
        Long generalId = 1L;
        YandexResponse yandexResponse = new YandexResponse();
        yandexResponse.setError("Yandex error");

        when(generalRepository.findById(generalId)).thenReturn(Optional.of(general));
        when(userService.getCurrentUser(authentication)).thenReturn(user);
        when(yandexDiskService.createFolderForProject(project)).thenReturn(yandexResponse);

        // Act
        YandexResponse response = projectService.createProject(project, authentication, generalId);

        // Assert
        assertNotNull(response);
        assertEquals("Yandex error", response.getError());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void findById_ShouldReturnProject() {
        // Arrange
        Long projectId = 1L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // Act
        Optional<Project> result = projectService.findById(projectId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(project, result.get());
        verify(projectRepository, times(1)).findById(projectId);
    }

    @Test
    void findAll_ShouldReturnAllProjects() {
        // Arrange
        List<Project> projects = Arrays.asList(project);
        when(projectRepository.findAll()).thenReturn(projects);

        // Act
        List<Project> result = projectService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(projects, result);
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    void findByUser_ShouldReturnUserProjects() {
        // Arrange
        List<Project> userProjects = Arrays.asList(project);
        when(projectRepository.findByCreatedBy(user)).thenReturn(userProjects);

        // Act
        List<Project> result = projectService.findByUser(user);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userProjects, result);
        verify(projectRepository, times(1)).findByCreatedBy(user);
    }

    @Test
    void getAllSections_WithValidProject_ShouldReturnAllSections() {
        // Arrange
        when(chapterService.getChaptersByGeneral(general.getId())).thenReturn(Arrays.asList(chapter));

        // Act
        List<Section> sections = projectService.getAllSections(project);

        // Assert
        assertNotNull(sections);
        assertEquals(2, sections.size());
        assertTrue(sections.contains(section1));
        assertTrue(sections.contains(section2));
    }

    @Test
    void getAllSections_WithNullProject_ShouldReturnEmptyList() {
        // Act
        List<Section> sections = projectService.getAllSections(null);

        // Assert
        assertNotNull(sections);
        assertTrue(sections.isEmpty());
    }

    @Test
    void isSectionGenerated_WhenSectionIsGenerated_ShouldReturnTrue() {
        // Arrange
        project.addGeneratedSection(section1);

        // Act
        boolean result = projectService.isSectionGenerated(project, section1);

        // Assert
        assertTrue(result);
    }

    @Test
    void isSectionGenerated_WhenSectionIsNotGenerated_ShouldReturnFalse() {
        // Act
        boolean result = projectService.isSectionGenerated(project, section1);

        // Assert
        assertFalse(result);
    }

    @Test
    void markSectionAsGenerated_ShouldAddSectionToGeneratedSections() {
        // Arrange
        when(projectRepository.save(project)).thenReturn(project);

        // Act
        projectService.markSectionAsGenerated(project, section1);

        // Assert
        assertTrue(project.getGeneratedSections().contains(section1));
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void markSectionAsNotGenerated_ShouldRemoveSectionFromGeneratedSections() {
        // Arrange
        project.addGeneratedSection(section1);
        when(projectRepository.save(project)).thenReturn(project);

        // Act
        projectService.markSectionAsNotGenerated(project, section1);

        // Assert
        assertFalse(project.getGeneratedSections().contains(section1));
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void getGeneratedSectionIds_ShouldReturnSectionIds() {
        // Arrange
        project.addGeneratedSection(section1);
        project.addGeneratedSection(section2);

        // Act
        Set<Long> sectionIds = projectService.getGeneratedSectionIds(project);

        // Assert
        assertNotNull(sectionIds);
        assertEquals(2, sectionIds.size());
        assertTrue(sectionIds.contains(section1.getId()));
        assertTrue(sectionIds.contains(section2.getId()));
    }
}