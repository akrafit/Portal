package com.portal.service;

import com.portal.dto.YandexDiskItem;
import com.portal.entity.*;
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
    private final ChapterSyncService chapterSyncService;
    private final YandexDiskService yandexDiskService;
    private final SimpleBookmarkService simpleBookmarkService;

    public ChapterService(ChapterRepository chapterRepository, GeneralRepository generalRepository, SectionRepository sectionRepository, ChapterSyncService chapterSyncService, YandexDiskService yandexDiskService, SimpleBookmarkService simpleBookmarkService) {
        this.chapterRepository = chapterRepository;
        this.generalRepository = generalRepository;
        this.sectionRepository = sectionRepository;
        this.chapterSyncService = chapterSyncService;
        this.yandexDiskService = yandexDiskService;
        this.simpleBookmarkService = simpleBookmarkService;
    }

    public Chapter createChapter(Chapter chapter, Long generalId) {
        General general = generalRepository.findById(generalId)
                .orElseThrow(() -> new RuntimeException("General not found with id: " + generalId));
        chapter.setGeneral(general);
        chapter.setSrc(general.getSrc() + "/" + chapter.getName());
        return chapterRepository.save(chapter);
    }

    public Chapter createChapterForSectionTemplate(Chapter chapter) {
        yandexDiskService.makeChaptersPublicUrl(List.of(chapter));
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
    public List<Chapter> getChaptersByGeneralTemplate(General general) {
        makeChaptersPublicUrl(general);
        return chapterRepository.findByGeneralAndTemplateTrue(general);
    }

    private void makeChaptersPublicUrl(General general) {
        List<Chapter> chapterList = chapterRepository.findByGeneralId(general.getId());
        if (chapterList.size() > 0) {
            yandexDiskService.makeChaptersPublicUrl(chapterList);
        }
    }

    public List<Chapter> getChaptersByGeneralTemplate(General general, Section section) {
        return chapterRepository.findByGeneralAndTemplateTrueAndContainingSection(general, section);
    }

    public List<Chapter> getChaptersByProject(Project project, Section section) {
        return chapterRepository.findChaptersByProjectAndSection(project, section.getId());
        //return chapterRepository.findChaptersByProjectAndSectionsIs(project,section);
    }

    public void createChapterForTemplate(YandexDiskItem uploadedFile, Long generalId) {
        General general = generalRepository.findById(generalId)
                .orElseThrow(() -> new RuntimeException("General not found with id: " + generalId));
        Chapter chapter = new Chapter();
        chapter.setTemplate(true);
        chapter.setGeneral(general);
        chapterSyncService.syncChapterFromYandexDiskItem(uploadedFile, chapter);
    }


    public Long countChaptersToProject(Project project) {
        return chapterRepository.countChapterByProject(project);
    }
    public Chapter updateChapter(Chapter chapter) {
        return chapterRepository.save(chapter);
    }

    public void deleteChapter(Long chapterId) {
        chapterRepository.deleteById(chapterId);
    }

    public Long countChapter() {
        return chapterRepository.count();
    }

    public Chapter saveChapter(Chapter newChapter) {
        return chapterRepository.save(newChapter);
    }

    public void loadBookMarkToFile(GeneralSection generalSection) {
        List<Chapter> chapterList = chapterRepository.findByGeneralAndTemplateTrueAndContainingSection(generalSection.getGeneral(),generalSection.getSection());
        simpleBookmarkService.generateBookMark(generalSection.getChapter(),chapterList);
    }
}
