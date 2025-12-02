package com.portal.service;

import com.portal.entity.Chapter;
import com.portal.entity.General;
import com.portal.entity.GeneralSection;
import com.portal.entity.Section;
import com.portal.repo.GeneralSectionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GeneralSectionService {
    private final GeneralSectionRepository generalSectionRepository;

    public GeneralSectionService(GeneralSectionRepository generalSectionRepository) {
        this.generalSectionRepository = generalSectionRepository;
    }

    public void createOrUpdateGeneralSection(General general, Section section, Chapter chapter) {
        GeneralSection generalSection = new GeneralSection();
        generalSection.setGeneral(general);
        generalSection.setSection(section);
        generalSection.setChapter(chapter);
        generalSection.setCreatedAt(LocalDateTime.now());
        generalSectionRepository.save(generalSection);
    }

    public GeneralSection findByGeneralIdAndSectionId(General general, Section section) {
        return generalSectionRepository.findGeneralSectionByGeneralAndSection(general, section);
    }

    public void delete(GeneralSection generalSection) {
        generalSectionRepository.delete(generalSection);
    }

    public List<GeneralSection> findByGeneral(General general) {
        return generalSectionRepository.findGeneralSectionByGeneral(general);
    }
}
