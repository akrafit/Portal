package com.portal.service;

import com.portal.dto.*;
import com.portal.entity.Chapter;
import com.portal.entity.General;
import com.portal.entity.Project;
import com.portal.entity.Section;
import com.portal.repo.ChapterRepository;
import com.portal.repo.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class YandexDiskService {

    private final WebClient webClient;
    private final ChapterRepository chapterRepository;
    private final ProjectRepository projectRepository;

    public YandexDiskService(WebClient yandexDiskWebClient, ChapterRepository chapterRepository, ProjectRepository projectRepository) {
        this.webClient = yandexDiskWebClient;
        this.chapterRepository = chapterRepository;
        this.projectRepository = projectRepository;
    }

    public List<YandexDiskItem> getFilesFromPortalDirectory(String path) {
        try {
            YandexDiskResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/resources")
                            .queryParam("path", "/portal/" + path)
                            .build())
                    .retrieve()
                    .bodyToMono(YandexDiskResponse.class)
                    .block();

            assert response != null;
            return response.getEmbedded().getItems();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении файлов из Яндекс Диска", e);
        }
    }
    // Дополнительный метод с фильтрацией только файлов
    public List<YandexDiskItem> getFilesOnlyFromPortalDirectory(String path) {
        List<YandexDiskItem> allItems = getFilesFromPortalDirectory(path);
        return allItems.stream()
                .filter(item -> "file".equals(item.getType()))
                .collect(Collectors.toList());
    }

    // Метод для получения информации о конкретном файле
    public YandexDiskItem getFileInfo(String fileName) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/resources")
                            .queryParam("path", fileName)
                            .build())
                    .retrieve()
                    .bodyToMono(YandexDiskItem.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении информации о файле", e);
        }
    }

    public boolean isFolderExists(String folderPath) {
        try {
            YandexDiskResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/resources")
                            .queryParam("path", folderPath)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            error -> Mono.empty()) // Игнорируем 404 ошибку
                    .bodyToMono(YandexDiskResponse.class)
                    .blockOptional()
                    .orElse(null);

            assert response != null;
            return response.getPath() != null; // Если ответ не null, значит папка существует
        } catch (Exception e) {
            // Если произошла ошибка (кроме 404), считаем что папка не существует
            return true;
        }
    }

    public YandexResponse createFolderForProject(Project project) {
        String folderPath = "portal/projects/" + project.getId();
        return createFolder(folderPath);
    }

    public YandexResponse createFolderForGeneral(General general) {
        String folderPath = "portal/templates/" + general.getId();
        return createFolder(folderPath);
    }

    private YandexResponse createFolder(String folderPath) {
        if (isFolderExists(folderPath)) {
            YandexResponse yandexResponse = new YandexResponse();
            yandexResponse.setError("No created");
            yandexResponse.setMessage("Такая директория уже существует");
            return yandexResponse;
        }
        try {
            return webClient.put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/resources")
                            .queryParam("path", folderPath)
                            .build())
                    .retrieve()
                    .bodyToMono(YandexResponse.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании директории на Яндекс Диске", e);
        }
    }
    /**
     * Получает URL для загрузки файла на Яндекс.Диск
     */
    public String getUploadUrl(String filePath, boolean overwrite) {
        try {
            YandexDiskUploadResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/resources/upload")
                            .queryParam("path", filePath)
                            .queryParam("overwrite", overwrite)
                            .build())
                    .retrieve()
                    .bodyToMono(YandexDiskUploadResponse.class)
                    .block();

            return response != null ? response.getUploadUrl() : null;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении URL для загрузки", e);
        }
    }

    /**
     * Загружает файл в указанную папку на Яндекс.Диске
     */
    public YandexDiskItem uploadFileToFolder(MultipartFile file, String folderPath) {
        try {
            String fullPath = folderPath + "/" + file.getOriginalFilename();

            // Получаем URL для загрузки
            String uploadUrl = getUploadUrl(fullPath, true);

            if (uploadUrl == null) {
                throw new RuntimeException("Не удалось получить URL для загрузки");
            }

            // Создаем временный файл
            Path tempFile = Files.createTempFile("yadisk_upload", file.getOriginalFilename());
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Загружаем файл на Яндекс.Диск
            byte[] fileBytes = Files.readAllBytes(tempFile);

            webClient.put()
                    .uri(uploadUrl)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .bodyValue(fileBytes)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            // Удаляем временный файл
            Files.deleteIfExists(tempFile);
            return getFileInfo(fullPath);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при обработке файла", e);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке файла на Яндекс.Диск", e);
        }
    }

    /**
     * Загружает файл в папку portal/template
     */
    public YandexDiskItem uploadFileToTemplateFolder(MultipartFile file, Long generalId) {
        return uploadFileToFolder(file, "portal/templates/");
    }

    /**
     * Загружает файл в папку конкретного проекта
     */
    public YandexDiskItem uploadFileToProjectFolder(MultipartFile file, Long projectId) {
        return uploadFileToFolder(file, "portal/projects/" + projectId);
    }

    /**
     * Загружает файл в папку конкретного шаблона (General)
     */
    public YandexDiskItem uploadFileToGeneralFolder(MultipartFile file, Long generalId) {
        return uploadFileToFolder(file, "portal/templates/" + generalId);
    }

    /**
     * Создает папку portal/template если она не существует
     */
    public void createTemplateFolderIfNotExists() {
        String folderPath = "portal/templates";
        if (!isFolderExists(folderPath)) {
            webClient.put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/resources")
                            .queryParam("path", folderPath)
                            .build())
                    .retrieve()
                    .bodyToMono(YandexResponse.class)
                    .block();
        }
    }

    /**
     * Удаляет файл с Яндекс.Диска
     */
    public YandexResponse deleteFile(String filePath) {
        try {
            return webClient.delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("/resources")
                            .queryParam("path", filePath)
                            .queryParam("permanently", true)
                            .build())
                    .retrieve()
                    .bodyToMono(YandexResponse.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении файла", e);
        }
    }

    public Boolean copyFromTemplateToProject(List<Chapter> chapterList, Project project, Section section) {
        String projectBasePath = "/portal/projects/" + project.getId();

        if(isFolderExists(projectBasePath)){
            List<Chapter> uniqueChapters = new ArrayList<>();
            List<Chapter> existChapterList = chapterRepository.findChaptersByProject(project);
            Set<String> nameFromData = existChapterList.stream().map(Chapter::getName).collect(Collectors.toSet());
//                List<YandexDiskItem> existingYandexDiskItem = getFilesFromPortalDirectory("/projects/" + project.getId());
//                Set<String> nameFromDisk = existingYandexDiskItem.stream().map(YandexDiskItem::getName).collect(Collectors.toSet());
            for (Chapter chapter : chapterList){
                if(!nameFromData.contains(chapter.getName())){
                    uniqueChapters.add(chapter);
                }
            }
            return loadChaptersToDisk(uniqueChapters, project, projectBasePath, section);
        }else{
            YandexResponse yandexResponse = createFolder(projectBasePath);
            if (yandexResponse.getError() != null) {
                return false;////////////////////////////////
            }
            return loadChaptersToDisk(chapterList, project, projectBasePath, section);
        }
    }

    private Boolean loadChaptersToDisk(List<Chapter> chapterList, Project project, String projectBasePath, Section section) {
        try {
            List<Chapter> newChapters = new ArrayList<>();
            for (Chapter templateChapter : chapterList) {
                // Извлекаем имя файла из оригинального пути
                String originalFileName = extractFileNameFromPath(templateChapter.getPath());
                String newFilePath = projectBasePath + "/" + originalFileName;

                // Копируем файл на Яндекс.Диске
                YandexDiskUploadResponse copyResult = copyFile(templateChapter.getPath(), newFilePath);
                if (copyResult.getError() == null && copyResult.getUploadUrl() != null) {
                    // Получаем мета-информацию для нового файла

                    YandexDiskItem yandexDiskItem = getFileMetaInfo(copyResult.getUploadUrl());
                    // Создаем новую сущность Chapter для проекта на основе мета-информации
                    Chapter newChapter = new Chapter(yandexDiskItem, project);
                    newChapter.getSections().add(section);
                    newChapter.setGeneral(project.getGeneral());
                    newChapters.add(newChapter);

                } else {
                    // Если копирование не удалось, откатываем операцию
                    //rollbackCreatedFiles(yandexDiskService, projectBasePath, newChapters);
                    return false;
                }
            }

            // Сохраняем все новые Chapter в базу данных
            if (!newChapters.isEmpty()) {
                chapterRepository.saveAll(newChapters);
                // Обновляем связь проекта с главами
                project.getChapters().addAll(newChapters);
                projectRepository.save(project);
            }

            return true;

        } catch (Exception e) {
            // В случае ошибки выполняем откат
            log.error("Error copying chapters to project: " + e.getMessage(), e);
            return false;
        }
    }

    private YandexDiskItem getFileMetaInfo(String newFilePath) {
        try {
            String url = URLDecoder.decode(newFilePath, StandardCharsets.UTF_8);
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(YandexDiskItem.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении мета информации", e);
        }
    }

    private YandexDiskUploadResponse copyFile(String path, String newFilePath) {
        try {
            return webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/resources/copy")
                            .queryParam("from", path)
                            .queryParam("path", newFilePath)
                            .queryParam("overwrite", false)
                            .queryParam("force_async", false)
                            .build())
                    .retrieve()
                    .bodyToMono(YandexDiskUploadResponse.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при копировании файлов", e);
        }
    }

    // Создание Chapter на основе мета-информации
    private Chapter createChapterFromMetaInfo(Map<String, Object> metaInfo, Chapter templateChapter,
                                              Project project, String filePath) {
        Chapter newChapter = new Chapter();

        // Устанавливаем базовые поля из templateChapter
        newChapter.setName(templateChapter.getName());
        newChapter.setTemplate(false);
        newChapter.setProject(project);
        newChapter.setGeneral(templateChapter.getGeneral());

        // Копируем связанные секции
        if (templateChapter.getSections() != null) {
            newChapter.setSections(new ArrayList<>(templateChapter.getSections()));
        }

        // Обновляем поля на основе мета-информации
        if (metaInfo != null) {
            // Пример извлечения данных из мета-информации (адаптируйте под вашу структуру)
            newChapter.setPath(filePath);
            newChapter.setPublicUrl((String) metaInfo.get("public_url"));
            newChapter.setResourceId((String) metaInfo.get("resource_id"));

            // Если в мета-информации есть название файла, можно использовать его
            String fileNameFromMeta = (String) metaInfo.get("name");
            if (fileNameFromMeta != null && !fileNameFromMeta.isEmpty()) {
                newChapter.setName(fileNameFromMeta);
            }

            // Дополнительные поля из мета-информации
            if (metaInfo.containsKey("src")) {
                newChapter.setSrc((String) metaInfo.get("src"));
            } else {
                newChapter.setSrc(templateChapter.getSrc());
            }

            // Добавьте другие поля по необходимости
        } else {
            // Если мета-информация недоступна, используем значения по умолчанию
            newChapter.setPath(filePath);
            newChapter.setSrc(templateChapter.getSrc());
            newChapter.setResourceId(templateChapter.getResourceId());
            newChapter.setPublicUrl(templateChapter.getPublicUrl());
        }

        return newChapter;
    }

    // Вспомогательный метод для извлечения имени файла из пути
    private String extractFileNameFromPath(String path) {
        if (path == null || path.isEmpty()) {
            return "chapter_" + System.currentTimeMillis();
        }

        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < path.length() - 1) {
            return path.substring(lastSlashIndex + 1);
        }

        return path;
    }

    // Метод для отката созданных файлов в случае ошибки
//    private void rollbackCreatedFiles(String basePath, List<Chapter> createdChapters) {
//        try {
//            // Удаляем все созданные файлы
//            for (Chapter chapter : createdChapters) {
//                if (chapter.getPath() != null) {
//                    diskService.deleteFile(chapter.getPath());
//                }
//            }
//
//            // Удаляем базовую директорию, если она пуста
//            //diskService.deleteFolder(basePath);
//        } catch (Exception e) {
//            log.error("Error during rollback: " + e.getMessage(), e);
//        }
//    }
}
