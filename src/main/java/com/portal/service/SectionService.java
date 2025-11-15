package com.portal.service;

import com.portal.entity.Section;
import com.portal.repo.SectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public Section createSection(Section section) {
        return sectionRepository.save(section);
    }
    public Section getSectionById(Long Id){
        return sectionRepository.getReferenceById(Id);
    }
    // Добавляем метод для обновления раздела
    public Section updateSection(Section section) {
        return sectionRepository.save(section);
    }

    // Добавляем метод для получения всех разделов с шаблонами
    public List<Section> getAllSectionsWithTemplates() {
        return sectionRepository.findAll();
    }
}
