package com.portal.service;

import com.portal.dto.YandexDiskItem;
import com.portal.dto.YandexDiskResponse;
import com.portal.entity.Project;
//import com.portal.repo.DocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {

//
//    private final DocumentRepository documentRepository;
//
//    public DocumentService(DocumentRepository documentRepository) {
//        this.documentRepository = documentRepository;
//    }
//
//    public List<Document> getProjectDocuments(Project project) {
//        return documentRepository.findByProjectOrderByName(project);
//    }
//
//    public Document createDocumentFromYandexItem(YandexDiskItem item, Project project) {
//        // Проверяем, не существует ли уже документ с таким resourceId
//        if (item.getResourceId() != null &&
//                documentRepository.existsByResourceId(item.getResourceId())) {
//            return documentRepository.findByResourceId(item.getResourceId())
//                    .orElseThrow(() -> new RuntimeException("Document not found"));
//        }
//
//        Document document = new Document();
//        document.setName(item.getName());
//        document.setType(DocumentType.fromMimeType(item.getMimeType()));
//        document.setStatus(DocumentStatus.IN_WORK);
//        document.setProject(project);
//        document.setYandexPath(item.getPath());
//        document.setYandexUrl(item.getFile());
//        document.setPublicUrl(item.getPublicUrl());
//        document.setSize(item.getSize());
//        document.setMimeType(item.getMimeType());
//        document.setResourceId(item.getResourceId());
//        document.setCreatedAt(LocalDateTime.now());
//        document.setModifiedAt(LocalDateTime.now());
//
//        return documentRepository.save(document);
//    }
//
//    public void syncProjectDocumentsFromYandex(YandexDiskResponse yandexResponse, Project project) {
//        if (yandexResponse.getEmbedded() != null && yandexResponse.getEmbedded().getItems() != null) {
//            for (YandexDiskItem item : yandexResponse.getEmbedded().getItems()) {
//                if ("file".equals(item.getType())) {
//                    createDocumentFromYandexItem(item, project);
//                }
//            }
//        }
//    }
//
//    public Document updateDocumentStatus(Long documentId, DocumentStatus status) {
//        Document document = documentRepository.findById(documentId)
//                .orElseThrow(() -> new RuntimeException("Document not found"));
//        document.setStatus(status);
//        document.setModifiedAt(LocalDateTime.now());
//        return documentRepository.save(document);
//    }
//
//    public void deleteDocument(Long documentId) {
//        documentRepository.deleteById(documentId);
//    }
//
//    public Document save(Document document) {
//        return documentRepository.save(document);
//    }
}