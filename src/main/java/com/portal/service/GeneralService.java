package com.portal.service;

import com.portal.entity.General;
import com.portal.entity.GeneralSection;
import com.portal.entity.Section;
import com.portal.repo.GeneralRepository;
import com.portal.repo.GeneralSectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GeneralService {

    private final GeneralRepository generalRepository;
    private final LocalFileService localFileService;
    private final GeneralSectionRepository generalSectionRepository;

    public GeneralService(GeneralRepository generalRepository, LocalFileService localFileService, GeneralSectionRepository generalSectionRepository) {
        this.generalRepository = generalRepository;
        this.localFileService = localFileService;
        this.generalSectionRepository = generalSectionRepository;
    }

    public List<General> getAllGenerals() {
        return generalRepository.findAll();
    }

    public General createGeneral(General general) {
        try {
            // Сначала сохраняем general чтобы получить ID
            generalRepository.save(general);

            // Создаем папку в локальном хранилище
            localFileService.createTemplateFolder(general.getId());

            general.setSrc("/" + general.getId());
            return generalRepository.save(general);

        } catch (Exception e) {
            // Если не удалось создать папку, удаляем general
            generalRepository.delete(general);
            throw new RuntimeException("Не удалось создать файловую структуру шаблона: " + e.getMessage());
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

    public Optional<Object> findById(Long generalId) {
        return Optional.of(generalRepository.findById(generalId));
    }
}