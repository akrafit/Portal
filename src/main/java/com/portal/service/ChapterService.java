package com.portal.service;

import com.portal.entity.Chapter;
import com.portal.entity.General;
import com.portal.entity.Section;
import com.portal.repo.ChapterRepository;
import com.portal.repo.GeneralRepository;
import com.portal.repo.SectionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final GeneralRepository generalRepository;
    private final SectionRepository sectionRepository;

    public ChapterService(ChapterRepository chapterRepository, GeneralRepository generalRepository, SectionRepository sectionRepository) {
        this.chapterRepository = chapterRepository;
        this.generalRepository = generalRepository;
        this.sectionRepository = sectionRepository;
    }

    public Chapter createChapter(Chapter chapter, Long generalId) {
        General general = generalRepository.findById(generalId)
                .orElseThrow(() -> new RuntimeException("General not found with id: " + generalId));
        chapter.setGeneral(general);
        return chapterRepository.save(chapter);
    }

    public void updateChapterSections(Long chapterId, List<Long> sectionIds) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapter not found with id: " + chapterId));

        List<Section> sections;
        if (sectionIds != null && !sectionIds.isEmpty()) {
            sections = sectionRepository.findAllById(sectionIds);
        } else {
            sections = new ArrayList<>();
        }

        chapter.setSections(sections);
        chapterRepository.save(chapter);
    }


    public List<Chapter> getChaptersByGeneral(Long generalId) {
        return chapterRepository.findByGeneralId(generalId);
    }
}
