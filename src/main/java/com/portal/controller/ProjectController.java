package com.portal.controller;

import com.portal.entity.Project;
import com.portal.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public String projects(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
            String userName = oauthUser.getAttribute("real_name");
            if (userName == null) {
                userName = oauthUser.getAttribute("login");
            }
            model.addAttribute("userName", userName);
        }

        model.addAttribute("projects", projectService.findAll());
        model.addAttribute("newProject", new Project());
        return "projects";
    }

    @PostMapping
    public String createProject(@ModelAttribute Project project,
                                Authentication authentication) {
        projectService.createProject(project, authentication);
        return "redirect:/projects";
    }
}