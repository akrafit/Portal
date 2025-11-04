package com.portal.service;

import com.portal.dto.*;
import com.portal.entity.Project;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Component
public class YandexDiskService {

    private final WebClient webClient;

    public YandexDiskService(WebClient yandexDiskWebClient) {
        this.webClient = yandexDiskWebClient;
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
            System.out.println(response.toString());

            return response.getEmbedded().getItems();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении файлов из Яндекс Диска", e);
        }
    }

    // Дополнительный метод с фильтрацией только файлов
//    public List<Resource> getFilesOnlyFromPortalDirectory() {
//        List<Resource> allItems = getFilesFromPortalDirectory();
//        return allItems.stream()
//                .filter(item -> "file".equals(item.getType()))
//                .collect(Collectors.toList());
//    }

    // Метод для получения информации о конкретном файле
    public Resource getFileInfo(String fileName) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/resources")
                            .queryParam("path", "portal/" + fileName)
                            .build())
                    .retrieve()
                    .bodyToMono(Resource.class)
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
        String folderPath = "portal/" + project.getName();
        // Проверяем существование директории
        if (isFolderExists(folderPath)) {
            YandexResponse yandexResponse = new YandexResponse();
            yandexResponse.setError("No created");
            yandexResponse.setMessage("Такой проект уже существует");
            return yandexResponse;
        }
        try {
            return webClient.put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/resources")
                            .queryParam("path", folderPath)
                            .build())
                    .retrieve()
//                    .onStatus(HttpStatusCode::is4xxClientError,
//                            error -> Mono.error(new RuntimeException("API not found")))
//                    .onStatus(HttpStatusCode::is5xxServerError,
//                            error -> Mono.error(new RuntimeException("Server is not responding")))
                    .bodyToMono(YandexResponse.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании директории на Яндекс Диске", e);
        }
    }
}
