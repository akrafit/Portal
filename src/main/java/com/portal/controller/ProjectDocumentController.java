package com.portal.controller;

import com.portal.dto.DocumentDto;
import com.portal.dto.YandexDiskItem;
import com.portal.dto.YandexDiskResponse;
import com.portal.entity.Chapter;
import com.portal.entity.Project;
import com.portal.entity.Section;
import com.portal.service.DocumentService;
import com.portal.service.ProjectService;
import com.portal.service.YandexDiskService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/projects/{projectId}/documents")
public class ProjectDocumentController {

    private final ProjectService projectService;
    private final DocumentService documentService;
    private final YandexDiskService yandexDiskService;


    public ProjectDocumentController(ProjectService projectService, DocumentService documentService, YandexDiskService yandexDiskService) {
        this.projectService = projectService;
        this.documentService = documentService;
        this.yandexDiskService = yandexDiskService;
    }

    @GetMapping
    public String getProjectDocuments(@PathVariable Long projectId, Model model) {
        Project project = projectService.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        List<DocumentDto> documentDtoList = new ArrayList<>();
        List<YandexDiskItem> yandexDiskItemList = yandexDiskService.getFilesFromPortalDirectory(project.getName());
        if(yandexDiskItemList != null) {
            yandexDiskItemList.forEach(yandexDiskItem -> {
                documentDtoList.add(new DocumentDto(yandexDiskItem));
            });
        }
        model.addAttribute("project", project);
        model.addAttribute("documentDtoList", documentDtoList);
        model.addAttribute("sections", projectService.getAllSections(project));
        //model.addAttribute("newDocument", new Document());
        return "project"; // Возвращаем шаблон project.html
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