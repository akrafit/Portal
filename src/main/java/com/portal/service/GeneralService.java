package com.portal.service;

import com.portal.dto.YandexResponse;
import com.portal.entity.General;
import com.portal.repo.GeneralRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneralService {

    private final GeneralRepository generalRepository;
    private final YandexDiskService yandexDiskService;

    public GeneralService(GeneralRepository generalRepository, YandexDiskService yandexDiskService) {
        this.generalRepository = generalRepository;
        this.yandexDiskService = yandexDiskService;
    }

    public List<General> getAllGenerals() {
        return generalRepository.findAll();
    }

    public General createGeneral(General general) {
        YandexResponse yandexResponse = yandexDiskService.createFolderForGeneral(general);
        if (yandexResponse.getError() != null) {
            return null;
        }else{
            generalRepository.save(general);
            general.setSrc("/" + general.getId());
            return general;
        }
    }

    public General getGeneralById(Long id) {
        return generalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("General not found with id: " + id));
    }
}