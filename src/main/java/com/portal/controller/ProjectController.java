package com.portal.controller;

import com.portal.dto.ProjectForm;
import com.portal.dto.YandexResponse;
import com.portal.entity.Project;
import com.portal.entity.User;
import com.portal.enums.UserRole;
import com.portal.service.GeneralService;
import com.portal.service.ProjectService;
import com.portal.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final GeneralService generalService;
    private final UserService userService;

    public ProjectController(ProjectService projectService, GeneralService generalService, UserService userService) {
        this.projectService = projectService;
        this.generalService = generalService;
        this.userService = userService;
    }

    @GetMapping
    public String projects(Model model, Authentication authentication) {
        User user = userService.getCurrentUser(authentication);
        List<Project> projectList;
        if (user.hasRole(UserRole.ADMIN)) {
            projectList =  projectService.findAll();
        } else if (user.hasRole(UserRole.EMPLOYEE)) {
            projectList =  projectService.findByUser(user);
        } else if (user.hasRole(UserRole.CONTRACTOR)) {
            projectList =  projectService.findWhereUserContractor(user);
        } else {
            projectList = java.util.Collections.emptyList();
        }
        model.addAttribute("projects", projectList);
        model.addAttribute("projectForm", new ProjectForm());
        model.addAttribute("user", userService.getCurrentUser(authentication));
        model.addAttribute("generals", generalService.getAllGenerals());
        return "projects";
    }

    @PostMapping
    public String createProject(@ModelAttribute ProjectForm projectForm,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        Project project = new Project();
        project.setName(projectForm.getName());
        project.setDescription(projectForm.getDescription());
        YandexResponse response = projectService.createProject(project, authentication,projectForm.getGeneralId());
        if(response.getHref() != null){
            redirectAttributes.addFlashAttribute("successMessage", "Проект успешно создан!");
        }else{
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при создании проекта: " + response.getMessage());
        }
        return "redirect:/projects";
    }
}