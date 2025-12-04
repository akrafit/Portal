package com.portal.controller.onlyOffice;

import com.portal.entity.Chapter;
import com.portal.repo.ChapterRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/onlyoffice/callback")
public class OnlyOfficeCallbackController {
    private final ChapterRepository chapterRepository;
    String europeanDatePattern = "dd.MM.yyyy HH:mm:ss";
    DateTimeFormatter europeanDateFormatter = DateTimeFormatter.ofPattern(europeanDatePattern);

    public OnlyOfficeCallbackController(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }
    @PostMapping("/{id}")
    public ResponseEntity<String> handleCallback(@PathVariable Long id,
                                                 @RequestBody Map<String, Object> body) {
        System.out.println("Callback body: " + body);

        // Достаём статус (без падений)
        Integer status = null;
        Object statusObj = body.get("status");
        if (statusObj != null) {
            try { status = Integer.parseInt(statusObj.toString()); } catch (NumberFormatException ignored) {}
        }

        if (status != null && (status == 2 || status == 6)) {
            // Берём ссылку (url или downloadUri)
            String downloadUri = null;
            if (body.get("url") != null) downloadUri = body.get("url").toString();
            else if (body.get("downloadUri") != null) downloadUri = body.get("downloadUri").toString();

            if (downloadUri != null && !downloadUri.isBlank()) {

                downloadUri = downloadUri.replaceAll("localhost:8083", "onlyoffice");
                try {
                    Chapter chapter = chapterRepository.findById(id).orElseThrow();
                    Resource resource = new UrlResource(downloadUri);

                    Path storageDir = Paths.get("/app/file-storage");
                    Files.createDirectories(storageDir);

                    // Определяем целевой путь: если chapter.getPath() абсолютный — используем как есть,
                    // если относительный — резолвим относительно /app/file-storage
                    Path chapterPath = Paths.get(chapter.getPath());
                    Path targetPath = chapterPath.isAbsolute()
                            ? chapterPath
                            : Paths.get("/app/file-storage").resolve(chapterPath).normalize();

                    // Создаём директории и сохраняем файл
                    Files.createDirectories(targetPath.getParent());
                    try (InputStream in = resource.getInputStream()) {
                        Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        chapter.setModified(europeanDateFormatter.format(LocalDateTime.now()));
                    }
                    chapterRepository.save(chapter);
                    System.out.println("Документ " + id + " сохранён в " + targetPath);
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.ok("{\"error\":1}");
                }
            }
        }

        return ResponseEntity.ok("{\"error\":0}");
    }


}



//    @PostMapping("/{id}")
//    public ResponseEntity<String> handleCallback(@PathVariable Long id,
//                                                 @RequestBody Map<String, Object> body) {
//        System.out.println("Callback body: " + body);
//        // Берём URL из callback
//        String fileUrl = body.get("url").toString();
//
//        // Подменяем localhost:8083 на имя контейнера onlyoffice
//        fileUrl = fileUrl.replace("localhost:8083", "onlyoffice");
//        Integer status = (Integer) body.get("status");
//        if (status != null && (status == 2 || status == 6)) {
//            String downloadUri = (String) body.get("url");
//            if (downloadUri == null) {
//                downloadUri = (String) body.get("downloadUri");
//            }
//            if (downloadUri != null) {
//                try {
//                    Chapter chapter = chapterRepository.findById(id).orElseThrow();
//                    Resource resource = new UrlResource(downloadUri);
//
//                    Path storageDir = Paths.get("/app/file-storage");
//                    Path targetPath = storageDir.resolve(Paths.get(chapter.getPath()).getFileName());
//
//                    Files.copy(resource.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
//                    System.out.println("Документ " + id + " сохранён в " + targetPath);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return ResponseEntity.ok("{\"error\":1}");
//                }
//            }
//        }
//        return ResponseEntity.ok("{\"error\":0}");
//    }
