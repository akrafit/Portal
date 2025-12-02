package com.portal.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class LocalFileService {

    @Value("${file.storage.path}")
    private String storagePath;

    @PostConstruct
    public void init() {
        try {
            // Создаем структуру папок как в Яндекс.Диске
            Files.createDirectories(Paths.get(storagePath, "portal", "projects"));
            Files.createDirectories(Paths.get(storagePath, "portal", "templates"));
            log.info("Локальное хранилище инициализировано: {}", storagePath);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директории хранилища", e);
        }
    }

    /**
     * Сохраняет файл в структуру portal/projects/{projectId}/
     */
    public String saveProjectFile(MultipartFile file, Long projectId) {
        try {
            String relativePath = "portal/projects/" + projectId;
            return saveFile(file, relativePath);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения файла проекта: " + e.getMessage(), e);
        }
    }

    /**
     * Сохраняет файл в структуру portal/templates/{generalId}/
     */
    public String saveTemplateFile(MultipartFile file, Long generalId) {
        try {
            String relativePath = "portal/templates/" + generalId;
            return saveFile(file, relativePath);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сохранения файла шаблона: " + e.getMessage(), e);
        }
    }

    /**
     * Универсальный метод сохранения файла
     */
    /**
     * Универсальный метод сохранения файла
     */
    private String saveFile(MultipartFile file, String relativePath) throws IOException {
        // ИСПРАВЛЕНИЕ: сохраняем оригинальное имя файла с поддержкой кириллицы
        String fileName = preserveOriginalFileNameWithUnicode(file.getOriginalFilename());
        Path filePath = Paths.get(storagePath, relativePath, fileName);

        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String fullRelativePath = relativePath + "/" + fileName;
        log.info("Файл сохранен: {}", fullRelativePath);
        return fullRelativePath;
    }

    /**
     * Сохраняет оригинальное имя файла с поддержкой кириллицы и Unicode
     */
    private String preserveOriginalFileNameWithUnicode(String originalFileName) {
        if (originalFileName == null) {
            return "file_" + System.currentTimeMillis();
        }

        // Убираем только действительно опасные символы для файловой системы
        // Кириллица и другие Unicode символы остаются
        String safeName = originalFileName
                .replaceAll("[\\\\/:*?\"<>|]", "_") // Заменяем только системные запрещенные символы
                .replaceAll("\\s+", " ") // Заменяем множественные пробелы на один
                .trim(); // Убираем пробелы в начале и конце

        // Проверяем, не осталось ли имя пустым после очистки
        if (safeName.isEmpty()) {
            return "file_" + System.currentTimeMillis();
        }

        // Обрезаем слишком длинные имена (максимум 255 символов)
        if (safeName.length() > 255) {
            int dotIndex = safeName.lastIndexOf(".");
            if (dotIndex > 0) {
                String name = safeName.substring(0, Math.min(dotIndex, 200));
                String extension = safeName.substring(dotIndex);
                safeName = name + extension;
            } else {
                safeName = safeName.substring(0, 255);
            }
        }

        log.debug("Оригинальное имя файла: '{}', безопасное имя: '{}'", originalFileName, safeName);
        return safeName;
    }

    /**
     * Загружает файл как Resource
     */
    public Resource loadFile(String relativePath) {
        try {
            Path filePath = Paths.get(storagePath, relativePath);
            if (!Files.exists(filePath)) {
                throw new RuntimeException("Файл не найден: " + relativePath);
            }
            return new FileSystemResource(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки файла: " + e.getMessage(), e);
        }
    }

    /**
     * Копирует файл из шаблона в проект
     */
    public String copyTemplateToProject(String templateFilePath, Long projectId) {
        try {
            // Из пути шаблона извлекаем имя файла
            String fileName = extractFileNameFromPath(templateFilePath);
            String sourcePath = templateFilePath; // уже содержит portal/templates/{id}/filename
            String targetPath = "portal/projects/" + projectId + "/" + fileName;

            Path source = Paths.get(storagePath, sourcePath);
            Path target = Paths.get(storagePath, targetPath);

            Files.createDirectories(target.getParent());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

            log.info("Файл скопирован из шаблона в проект: {} -> {}", sourcePath, targetPath);
            return targetPath;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка копирования файла из шаблона в проект: " + e.getMessage(), e);
        }
    }


    /**
     * Проверяет существование файла
     */
    public boolean fileExists(String relativePath) {
        return Files.exists(Paths.get(storagePath, relativePath));
    }

    /**
     * Удаляет файл
     */
    public void deleteFile(String relativePath) {
        try {
            Files.deleteIfExists(Paths.get(storagePath, relativePath));
            log.info("Файл удален: {}", relativePath);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка удаления файла: " + e.getMessage(), e);
        }
    }


    /**
     * Извлекает имя файла из пути
     */
    private String extractFileNameFromPath(String path) {
        if (path == null || path.isEmpty()) {
            return "file_" + System.currentTimeMillis();
        }
        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < path.length() - 1) {
            return path.substring(lastSlashIndex + 1);
        }
        return path;
    }

    public String getStoragePath() {
        return storagePath;
    }
    public void createProjectFolder(Long projectId) {
        try {
            String projectPath = "portal/projects/" + projectId;
            Path folderPath = Paths.get(storagePath, projectPath);

            Files.createDirectories(folderPath);
            log.info("Папка проекта создана: {}", folderPath.toAbsolutePath());

        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать папку проекта: " + e.getMessage(), e);
        }
    }

    /**
     * Создает папку для шаблона (General)
     */
    public void createTemplateFolder(Long generalId) {
        try {
            String templatePath = "portal/templates/" + generalId;
            Path folderPath = Paths.get(storagePath, templatePath);

            Files.createDirectories(folderPath);
            log.info("Папка шаблона создана: {}", folderPath.toAbsolutePath());

        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать папку шаблона: " + e.getMessage(), e);
        }
    }
    public void copyFile(String sourceRelativePath, String targetRelativePath) {
        try {
            Path source = Paths.get(storagePath, sourceRelativePath);
            Path target = Paths.get(storagePath, targetRelativePath);

            Files.createDirectories(target.getParent());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

            log.info("Файл скопирован: {} -> {}", sourceRelativePath, targetRelativePath);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка копирования файла: " + e.getMessage(), e);
        }
    }
}