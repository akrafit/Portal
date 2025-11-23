package com.portal.service;

import com.portal.dto.YandexResponse;
import com.portal.entity.Chapter;
import com.portal.entity.General;
import com.portal.entity.GeneralSection;
import com.portal.entity.Section;
import com.portal.repo.GeneralRepository;
import com.portal.repo.GeneralSectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeneralService {

    private final GeneralRepository generalRepository;
    private final YandexDiskService yandexDiskService;
    private final GeneralSectionRepository generalSectionRepository;

    public GeneralService(GeneralRepository generalRepository, YandexDiskService yandexDiskService, GeneralSectionRepository generalSectionRepository) {
        this.generalRepository = generalRepository;
        this.yandexDiskService = yandexDiskService;
        this.generalSectionRepository = generalSectionRepository;
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

    public GeneralSection findByGeneralAndSection(General general, Section section) {
        // Реализация поиска связи
        return generalSectionRepository.findGeneralSectionByGeneralAndSection(general, section);
    }

    public List<GeneralSection> findByGeneral(General general) {
        return generalSectionRepository.findGeneralSectionByGeneral(general);
    }

//    public Chapter getSectionTemplate(Long generalId, Long sectionId) {
//        GeneralSection gs = findByGeneralAndSection(generalId, sectionId);
//        return gs != null ? gs.getChapter() : null;
//    }

    public void save(GeneralSection generalSection) {
        generalSectionRepository.save(generalSection);
    }
}