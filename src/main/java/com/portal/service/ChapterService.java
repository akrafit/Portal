package com.portal.service;

import com.portal.dto.FileItem;
import com.portal.dto.YandexDiskItem;
import com.portal.entity.*;
import com.portal.repo.ChapterRepository;
import com.portal.repo.GeneralRepository;
import com.portal.repo.SectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ChapterService {
    private final ChapterRepository chapterRepository;
    private final GeneralRepository generalRepository;
    private final SectionRepository sectionRepository;
    private final ChapterSyncService chapterSyncService;
    private final LocalFileService localFileService;

    public ChapterService(ChapterRepository chapterRepository, GeneralRepository generalRepository, SectionRepository sectionRepository, ChapterSyncService chapterSyncService, LocalFileService localFileService) {
        this.chapterRepository = chapterRepository;
        this.generalRepository = generalRepository;
        this.sectionRepository = sectionRepository;
        this.chapterSyncService = chapterSyncService;
        this.localFileService = localFileService;
    }

    public Chapter createChapter(Chapter chapter, Long generalId) {
        General general = generalRepository.findById(generalId)
                .orElseThrow(() -> new RuntimeException("General not found with id: " + generalId));
        chapter.setGeneral(general);
        chapter.setSrc(general.getSrc() + "/" + chapter.getName());
        return chapterRepository.save(chapter);
    }

//    public Chapter createChapterForSectionTemplate(Chapter chapter) {
//        yandexDiskService.makeChaptersPublicUrl(List.of(chapter));
//        return chapterRepository.save(chapter);
//    }

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
        // УБИРАЕМ вызов makeChaptersPublicUrl - больше не нужно открывать доступ через Яндекс.Диск
        return chapterRepository.findByGeneralAndTemplateTrue(general);
    }

    public List<Chapter> getChaptersByGeneralTemplate(General general, Section section) {
        return chapterRepository.findByGeneralAndTemplateTrueAndContainingSection(general, section);
    }

//    private void makeChaptersPublicUrl(General general) {
//        List<Chapter> chapterList = chapterRepository.findByGeneralId(general.getId());
//        if (chapterList.size() > 0) {
//            yandexDiskService.makeChaptersPublicUrl(chapterList);
//        }
//    }

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

//    public void loadBookMarkToFile(GeneralSection generalSection) {
//        List<Chapter> chapterList = chapterRepository.findByGeneralAndTemplateTrueAndContainingSection(generalSection.getGeneral(),generalSection.getSection());
//        simpleBookmarkService.generateBookMark(generalSection.getChapter(),chapterList);
//    }
    public void createChapterForTemplate(FileItem uploadedFile, Long generalId) {
        General general = generalRepository.findById(generalId)
                .orElseThrow(() -> new RuntimeException("General not found with id: " + generalId));

        Chapter chapter = new Chapter();
        chapter.setTemplate(true);
        chapter.setGeneral(general);
        chapter.setName(uploadedFile.getName());
        chapter.setPath(uploadedFile.getPath());
        chapter.setSrc(general.getSrc() + "/" + uploadedFile.getName());

        chapterRepository.save(chapter);
        log.info("Создана глава шаблона: {}", uploadedFile.getPath());
    }

    /**
     * Копирует главы из шаблона в проект (заменяет старый метод из YandexDiskService)
     */
    public Boolean copyFromTemplateToProject(List<Chapter> chapterList, Project project, Section section) {
        try {
            // 1. Находим главы, которые еще не связаны с этой секцией
            List<Chapter> chaptersToLink = new ArrayList<>();
            List<Chapter> chaptersToCreate = new ArrayList<>();

            for (Chapter templateChapter : chapterList) {
                // Ищем главу с таким же именем в проекте
                Optional<Chapter> existingChapter = chapterRepository
                        .findByProjectAndName(project, templateChapter.getName());

                if (existingChapter.isPresent()) {
                    Chapter chapter = existingChapter.get();
                    // Проверяем, не связана ли уже глава с этой секцией
                    if (!chapter.getSections().contains(section)) {
                        chaptersToLink.add(chapter);
                    }
                    // Если уже связана - ничего не делаем
                } else {
                    // Главы нет в проекте - создаем новую
                    chaptersToCreate.add(templateChapter);
                }
            }

            // 2. Связываем существующие главы с новой секцией
            if (!chaptersToLink.isEmpty()) {
                chaptersToLink.forEach(chapter -> {
                    chapter.getSections().add(section);
                });
                chapterRepository.saveAll(chaptersToLink);
            }

            // 3. Создаем и копируем только совершенно новые главы
            if (!chaptersToCreate.isEmpty()) {
                return copyChaptersToProject(chaptersToCreate, project, section);
            }

            return true;

        } catch (Exception e) {
            log.error("Ошибка копирования глав в проект: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Копирует главы в проект (внутренний метод)
     */
    private Boolean copyChaptersToProject(List<Chapter> chapterList, Project project, Section section) {
        try {
            List<Chapter> newChapters = new ArrayList<>();

            for (Chapter templateChapter : chapterList) {
                // Копируем файл в локальном хранилище
                String newFilePath = localFileService.copyTemplateToProject(templateChapter.getPath(), project.getId());

                // Создаем новую сущность Chapter для проекта
                Chapter newChapter = new Chapter();
                newChapter.setName(templateChapter.getName());
                newChapter.setPath(newFilePath);
                newChapter.setProject(project);
                newChapter.setTemplate(false);
                newChapter.getSections().add(section);
                newChapter.setGeneral(project.getGeneral());
                newChapter.setSrc(project.getGeneral().getSrc() + "/" + templateChapter.getName());

                newChapters.add(newChapter);
            }

            // Сохраняем все новые Chapter в базу данных
            if (!newChapters.isEmpty()) {
                chapterRepository.saveAll(newChapters);
                // Обновляем связь проекта с главами
                project.getChapters().addAll(newChapters);
            }

            return true;

        } catch (Exception e) {
            log.error("Ошибка создания новых глав в проекте: {}", e.getMessage());
            return false;
        }
    }

    public Chapter getChapterById(Long chapterId) {
        return chapterRepository.findChapterById(chapterId);
    }

    public Chapter findById(Long id) {
        return chapterRepository.findChapterById(id);
    }
}
