package com.portal.controller.onlyOffice;

import com.portal.entity.Chapter;
import com.portal.repo.ChapterRepository;
import com.portal.service.jwt.JwtHelper;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;


@RestController
@RequestMapping("/onlyoffice")
public class OnlyOfficeController {

    private final ChapterRepository chapterRepository;
    private final JwtHelper jwtHelper;

    @Value("${onlyoffice.document-server.url}")
    private String onlyofficeUrl;
    @Value("${onlyoffice.document-server.callback-url}")
    private String callbackUrl;

    public OnlyOfficeController(ChapterRepository chapterRepository, JwtHelper jwtHelper) {
        this.chapterRepository = chapterRepository;
        this.jwtHelper = jwtHelper;
    }

    @GetMapping("/config/{id}")
    public Map<String, Object> getConfig(@PathVariable Long id, Principal principal) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        // URL для скачивания файла
        String fileUrl = "http://" + callbackUrl + "/api/files/" + id;

        // Имя файла с расширением
        String fileName = Paths.get(chapter.getPath()).getFileName().toString();


        Map<String, Object> document = new HashMap<>();
        document.put("fileType", getExtension(fileName)); // docx, xlsx, pdf
        document.put("key", String.valueOf(chapter.getId()));
        document.put("title", fileName); // имя с расширением
        document.put("url", fileUrl);

        // 👇 добавляем имя пользователя
        Map<String, Object> user = new HashMap<>();
        user.put("id", principal.getName());          // уникальный идентификатор
        user.put("name", principal.getName());        // отображаемое имя

        Map<String, Object> customization = new HashMap<>();
        customization.put("forcesave", true);
        customization.put("autosave", true);

        Map<String, Object> editorConfig = new HashMap<>();
        editorConfig.put("callbackUrl", "http://portal-application:8082/onlyoffice/callback/" + id);
        editorConfig.put("lang", "ru"); // 👈 язык интерфейса редактора
        editorConfig.put("user", user); // 👈 имя пользователя
        editorConfig.put("customization", customization);

        Map<String, Object> config = new HashMap<>();
        config.put("document", document);
        config.put("editorConfig", editorConfig);

        // JWT токен
        String token = jwtHelper.createToken(config);
        config.put("token", token);

        return config;
    }
    @GetMapping("/editor")
    public String openEditor(@RequestParam("docId") Long docId, Model model) {
        Chapter chapter = chapterRepository.findById(docId)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        model.addAttribute("document", chapter);
        return "document"; // Thymeleaf шаблон document.html
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex != -1) ? fileName.substring(dotIndex + 1) : "docx";
    }
}