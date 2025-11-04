package com.portal.controller;

import com.portal.dto.Resource;
import com.portal.service.YandexDiskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/yandex-disk")
public class YandexDiskController {

//    private final YandexDiskService yandexDiskService;
//
//    public YandexDiskController(YandexDiskService yandexDiskService) {
//        this.yandexDiskService = yandexDiskService;
//    }
//
//    @GetMapping("/portal/files")
//    public ResponseEntity<List<Resource>> getPortalFiles() {
//        try {
//            List<Resource> files = yandexDiskService.getFilesFromPortalDirectory();
//            return ResponseEntity.ok(files);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    @GetMapping("/portal/files-only")
//    public ResponseEntity<List<Resource>> getPortalFilesOnly() {
//        try {
//            List<Resource> files = yandexDiskService.getFilesOnlyFromPortalDirectory();
//            return ResponseEntity.ok(files);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
}
