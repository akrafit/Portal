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
    private final YandexDiskService yandexDiskService;
    private final SectionService sectionService;
    private final ChapterService chapterService;
    private final ChapterSyncService chapterSyncService;

    public SectionController(ProjectService projectService, YandexDiskService yandexDiskService, SectionRepository sectionRepository, SectionService sectionService, ChapterService chapterService, ChapterSyncService chapterSyncService) {
        this.projectService = projectService;
        this.yandexDiskService = yandexDiskService;
        this.sectionService = sectionService;
        this.chapterService = chapterService;
        this.chapterSyncService = chapterSyncService;
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
        //model.addAttribute("newDocument", new Document());
        return "section"; // Возвращаем шаблон section.html
    }
}
