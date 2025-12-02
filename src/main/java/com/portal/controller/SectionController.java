package com.portal.controller;

import com.portal.dto.DocumentDto;
import com.portal.dto.YandexDiskItem;
import com.portal.entity.Chapter;
import com.portal.entity.Project;
import com.portal.entity.Section;
import com.portal.repo.SectionRepository;
import com.portal.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/project/{projectId}/{sectionId}")
public class SectionController {
    private final ProjectService projectService;
    private final SectionService sectionService;
    private final ChapterService chapterService;

    public SectionController(ProjectService projectService, SectionService sectionService, ChapterService chapterService) {
        this.projectService = projectService;
        this.sectionService = sectionService;
        this.chapterService = chapterService;
    }
    @GetMapping
    public String getProjectSectionDocuments(@PathVariable Long projectId,@PathVariable Long sectionId, Model model) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        Section section  = sectionService.getSectionById(sectionId);
        List<Chapter> chapterList = chapterService.getChaptersByProject(project,section);
        List<DocumentDto> documentDtoList = new ArrayList<>();
        for (Chapter chapter : chapterList) {
            documentDtoList.add(new DocumentDto(chapter));
        }
        model.addAttribute("project", project);
        model.addAttribute("documentDtoList", documentDtoList);
        model.addAttribute("section", section);
        return "section";
    }
    @GetMapping("/document/{chapterId}")
    public String openDocument(@PathVariable Long projectId,
                               @PathVariable Long sectionId,
                               @PathVariable Long chapterId,
                               Model model) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        Section section = sectionService.getSectionById(sectionId);
        Chapter chapter = chapterService.findById(chapterId);

        model.addAttribute("project", project);
        model.addAttribute("section", section);
        model.addAttribute("chapter", chapter);

        return "document"; // Thymeleaf-шаблон document.html
    }
}
