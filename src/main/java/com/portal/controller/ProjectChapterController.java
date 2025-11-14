package com.portal.controller;

import com.portal.dto.SectionStatusDto;
import com.portal.entity.Project;
import com.portal.entity.Section;
import com.portal.repo.SectionRepository;
import com.portal.service.ChapterService;
import com.portal.service.ProjectService;
import com.portal.service.YandexDiskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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


    public ProjectChapterController(ProjectService projectService, YandexDiskService yandexDiskService, SectionRepository sectionRepository, ChapterService chapterService) {
        this.projectService = projectService;
        this.yandexDiskService = yandexDiskService;
        this.sectionRepository = sectionRepository;
        this.chapterService = chapterService;
    }

    @GetMapping
    public String getProjectSections(@PathVariable Long projectId, Model model) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        List<Section> sections = projectService.getAllSections(project);
        List<SectionStatusDto> sectionStatuses = sections.stream()
                .map(section -> SectionStatusDto.from(section, project)).sorted(Comparator.comparing(sectionStatusDto -> sectionStatusDto.getSection().getName())).collect(Collectors.toList());
        Long size = chapterService.countChaptersToProject(project);
        model.addAttribute("project", project);
        model.addAttribute("sectionStatuses", sectionStatuses);
        model.addAttribute("size", size);
        model.addAttribute("sections", sections);
        //model.addAttribute("newDocument", new Document());
        return "project"; // Возвращаем шаблон project.html
    }
    @PostMapping("/sections/{sectionId}/generate")
    public String generateSection(@PathVariable Long projectId,
                                  @PathVariable Long sectionId) {
        Project project = projectService.findById(projectId).orElseThrow();
        Section section = sectionRepository.findById(sectionId).orElseThrow();

        try {
            // Логика генерации...
            Boolean result = projectService.markSectionAsGenerated(project, section);
            return "redirect:/projects/" + projectId + "/documents?success=Section+generated";
        } catch (Exception e) {
            return "redirect:/projects/" + projectId + "/documents?error=Generation+failed";
        }
    }
//        @PostMapping
//    public String createDocument(@PathVariable Long projectId,
//                                 @ModelAttribute Document document) {
//        Project project = projectService.findById(projectId)
//                .orElseThrow(() -> new RuntimeException("Project not found"));
//
//        document.setProject(project);
//        documentService.save(document);
//
//        return "redirect:/projects/" + projectId + "/documents";
//    }

//    @PostMapping("/sync")
//    public String syncDocumentsFromYandex(@PathVariable Long projectId,
//                                          @RequestBody YandexDiskResponse yandexResponse) {
//        Project project = projectService.findById(projectId)
//                .orElseThrow(() -> new RuntimeException("Project not found"));
//
//        documentService.syncProjectDocumentsFromYandex(yandexResponse, project);
//
//        return "redirect:/projects/" + projectId + "/documents";
//    }
//
//    @PostMapping("/{documentId}/status")
//    public String updateDocumentStatus(@PathVariable Long projectId,
//                                       @PathVariable Long documentId,
//                                       @RequestParam DocumentStatus status) {
//        documentService.updateDocumentStatus(documentId, status);
//        return "redirect:/projects/" + projectId + "/documents";
//    }
//
//    @PostMapping
//    public String createDocument(@PathVariable Long projectId,
//                                 @ModelAttribute Document document) {
//        Project project = projectService.findById(projectId)
//                .orElseThrow(() -> new RuntimeException("Project not found"));
//
//        document.setProject(project);
//        documentService.save(document);
//
//        return "redirect:/projects/" + projectId + "/documents";
//    }
//
//    @PostMapping("/{documentId}/delete")
//    public String deleteDocument(@PathVariable Long projectId,
//                                 @PathVariable Long documentId) {
//        documentService.deleteDocument(documentId);
//        return "redirect:/projects/" + projectId + "/documents";
//    }
//
//    // REST endpoint для приема JSON из Яндекс.Диска API
//    @PostMapping(value = "/sync-json", consumes = "application/json")
//    public ResponseEntity<String> syncDocumentsJson(@PathVariable Long projectId,
//                                                    @RequestBody YandexDiskResponse yandexResponse) {
//        Project project = projectService.findById(projectId)
//                .orElseThrow(() -> new RuntimeException("Project not found"));
//
//        documentService.syncProjectDocumentsFromYandex(yandexResponse, project);
//
//        return ResponseEntity.ok("Documents synced successfully");
//    }
}