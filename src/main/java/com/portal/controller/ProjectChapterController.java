package com.portal.controller;

import com.portal.dto.SectionStatusDto;
import com.portal.entity.Project;
import com.portal.entity.Section;
import com.portal.entity.SectionAssignment;
import com.portal.entity.User;
import com.portal.enums.UserRole;
import com.portal.repo.SectionRepository;
import com.portal.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/projects/{projectId}/documents")
public class ProjectChapterController {

    private final ProjectService projectService;
    private final YandexDiskService yandexDiskService;
    private final SectionRepository sectionRepository;
    private final ChapterService chapterService;
    private final UserService userService;
    private final SectionAssignmentService sectionAssignmentService;

    public ProjectChapterController(ProjectService projectService,
                                    YandexDiskService yandexDiskService,
                                    SectionRepository sectionRepository,
                                    ChapterService chapterService,
                                    UserService userService,
                                    SectionAssignmentService sectionAssignmentService) {
        this.projectService = projectService;
        this.yandexDiskService = yandexDiskService;
        this.sectionRepository = sectionRepository;
        this.chapterService = chapterService;
        this.userService = userService;
        this.sectionAssignmentService = sectionAssignmentService;
    }

    @GetMapping
    public String getProjectSections(@PathVariable Long projectId, Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.getCurrentUser(authentication);
            model.addAttribute("user", user);
            model.addAttribute("isProjectCreator", isProjectCreator(projectId, user));
            Project project = projectService.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            //Если подрядчик то только его разделы
            if (!user.hasRole(UserRole.CONTRACTOR)) {
                List<Section> sections = projectService.getAllSections(project);
                List<SectionStatusDto> sectionStatuses = sections.stream()
                        .map(section -> {
                            SectionStatusDto dto = SectionStatusDto.from(section, project);
                            List<User> assignedUsers = sectionAssignmentService.getAssignedUsersForSection(project, section);
                            dto.setAssignedUsers(assignedUsers);
                            return dto;
                        })
                        .sorted(Comparator.comparing(sectionStatusDto -> sectionStatusDto.getSection().getName()))
                        .collect(Collectors.toList());
                model.addAttribute("sectionStatuses", sectionStatuses);
            }else{
                List<SectionAssignment> sectionAssignmentList = sectionAssignmentService.getAssignmentsByProjectAndUser(project, user);
                List<SectionStatusDto> sectionStatuses = sectionAssignmentList.stream()
                        .map(sectionAssignment -> {
                            SectionStatusDto dto = SectionStatusDto.from(sectionAssignment.getSection(),project);
                            List<User> assignedUsers = sectionAssignmentService.getAssignedUsersForSection(project, sectionAssignment.getSection());
                            dto.setAssignedUsers(assignedUsers);
                            return dto;
                        })
                        .sorted(Comparator.comparing(sectionStatusDto -> sectionStatusDto.getSection().getName()))
                        .collect(Collectors.toList());
                model.addAttribute("sectionStatuses", sectionStatuses);
            }
            Long size = chapterService.countChaptersToProject(project);
            List<User> availableContractors = sectionAssignmentService.getAvailableContractors();
            model.addAttribute("project", project);
            model.addAttribute("size", size);
            model.addAttribute("availableContractors", availableContractors);

            return "project";
        }else{
            return "404";
        }
    }

    @PostMapping("/sections/{sectionId}/generate")
    public String generateSection(@PathVariable Long projectId,
                                  @PathVariable Long sectionId) {
        Project project = projectService.findById(projectId).orElseThrow();
        Section section = sectionRepository.findById(sectionId).orElseThrow();
        try {
            Boolean result = projectService.markSectionAsGenerated(project, section);
            if(!result){
                return "redirect:/projects/" + projectId + "/documents?error=Generation+failed";
            }
            return "redirect:/projects/" + projectId + "/documents?success=Section+generated";
        } catch (Exception e) {
            return "redirect:/projects/" + projectId + "/documents?error=Generation+failed";
        }
    }

    @PostMapping("/sections/{sectionId}/assign")
    public String assignUserToSection(@PathVariable Long projectId,
                                      @PathVariable Long sectionId,
                                      @RequestParam Long userId,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.getCurrentUser(authentication);
            Project project = projectService.findById(projectId).orElseThrow();

            // Проверяем права: только создатель проекта с ролью EMPLOYEE может назначать
            if (!isProjectCreator(projectId, currentUser) || !currentUser.getRole().equals(UserRole.EMPLOYEE)) {
                redirectAttributes.addFlashAttribute("error", "Недостаточно прав для назначения ответственного");
                System.out.println("Недостаточно прав для назначения ответственного");
                return "redirect:/projects/" + projectId + "/documents";
            }

            Section section = sectionRepository.findById(sectionId).orElseThrow();
            User contractor = userService.findById(userId);

            // Проверяем, что пользователь действительно CONTRACTOR
            if (!contractor.getRole().equals(UserRole.CONTRACTOR)) {
                System.out.println("Можно назначать только пользователей с ролью CONTRACTOR");
                redirectAttributes.addFlashAttribute("error", "Можно назначать только пользователей с ролью CONTRACTOR");
                return "redirect:/projects/" + projectId + "/documents";
            }

            sectionAssignmentService.assignUserToSection(project, section, contractor);
            redirectAttributes.addFlashAttribute("success", "Пользователь успешно назначен на раздел");
            System.out.println("Пользователь успешно назначен на раздел");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при назначении пользователя: " + e.getMessage());
        }

        return "redirect:/projects/" + projectId + "/documents";
    }

    private boolean isProjectCreator(Long projectId, User user) {
        Project project = projectService.findById(projectId).orElseThrow();
        return project.getCreatedBy().getId().equals(user.getId());
    }
}

