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
}
