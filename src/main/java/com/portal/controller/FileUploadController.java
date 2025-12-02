package com.portal.controller;

import com.portal.dto.FileItem;
import com.portal.entity.General;
import com.portal.service.ChapterService;
import com.portal.service.GeneralService;
import com.portal.service.LocalFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final LocalFileService localFileService;
    private final ChapterService chapterService;
    private final GeneralService generalService;

    public FileUploadController(LocalFileService localFileService,
                                ChapterService chapterService,
                                GeneralService generalService) {
        this.localFileService = localFileService;
        this.chapterService = chapterService;
        this.generalService = generalService;
    }

    /**
     * Загружает файл в папку portal/templates/{generalId}
     */
    @PostMapping("/upload/template")
    public ResponseEntity<FileItem> uploadToTemplate(@RequestParam("file") MultipartFile file,
                                                     @RequestParam("generalId") Long generalId) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(new FileItem("Отсутствует файл"));
            }

            General general = generalService.getGeneralById(generalId);
            if (general == null) {
                return ResponseEntity.badRequest().body(new FileItem("Шаблон не найден"));
            }

            // Сохраняем файл локально
            String filePath = localFileService.saveTemplateFile(file, generalId);

            // Создаем запись в БД
            FileItem item = new FileItem();
            item.setName(file.getOriginalFilename());
            item.setPath(filePath);
            item.setType("file");
            item.setSize(file.getSize());

            chapterService.createChapterForTemplate(item, generalId);

            return ResponseEntity.ok(item);
        } catch (Exception e) {
            log.error("Ошибка загрузки файла в шаблон: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new FileItem("Ошибка загрузки: " + e.getMessage()));
        }
    }

    /**
     * Загружает файл в папку portal/projects/{projectId}
     */
    @PostMapping("/upload/project/{projectId}")
    public ResponseEntity<FileItem> uploadToProject(@RequestParam("file") MultipartFile file,
                                                    @PathVariable Long projectId) {
        try {
            String filePath = localFileService.saveProjectFile(file, projectId);

            FileItem item = new FileItem();
            item.setName(file.getOriginalFilename());
            item.setPath(filePath);
            item.setType("file");
            item.setSize(file.getSize());

            return ResponseEntity.ok(item);
        } catch (Exception e) {
            log.error("Ошибка загрузки файла в проект: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new FileItem("Ошибка загрузки: " + e.getMessage()));
        }
    }

    /**
     * Удаляет файл
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam String filePath) {
        try {
            localFileService.deleteFile(filePath);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Ошибка удаления файла: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка удаления файла: " + e.getMessage());
        }
    }
}