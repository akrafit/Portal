package com.portal.controller;

import com.portal.dto.YandexResponse;
import com.portal.entity.General;
import com.portal.entity.Chapter;
import com.portal.entity.Section;
import com.portal.service.GeneralService;
import com.portal.service.ChapterService;
import com.portal.service.SectionService;
import com.portal.dto.ChapterForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/generals")
public class GeneralController {

    private static final Logger logger = LoggerFactory.getLogger(GeneralController.class);

    private final GeneralService generalService;

    private final ChapterService chapterService;
    private final SectionService sectionService;

    public GeneralController(GeneralService generalService, ChapterService chapterService, SectionService sectionService) {
        this.generalService = generalService;
        this.chapterService = chapterService;
        this.sectionService = sectionService;
    }

    @GetMapping
    public String getAllGenerals(Model model) {
        try {
            logger.info("Fetching all generals");
            List<General> generals = generalService.getAllGenerals();
            logger.info("Found {} generals", generals.size());
            model.addAttribute("generals", generals);
            return "admin/generals-list";
        } catch (Exception e) {
            logger.error("Error fetching generals", e);
            model.addAttribute("error", "Ошибка при загрузке списка: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}")
    public String getGeneralDetail(@PathVariable Long id, Model model) {
        try {
            General general = generalService.getGeneralById(id);
            if (general == null){
                return "error";
            }
            List<Chapter> chapters = chapterService.getChaptersByGeneralTemplate(general);
            List<Section> sections = sectionService.getAllSections();

            // Создаем Map для быстрой проверки связей
            Map<Long, Set<Long>> chapterSectionMap = new HashMap<>();
            for (Chapter chapter : chapters) {
                Set<Long> sectionIds = chapter.getSections().stream()
                        .map(Section::getId)
                        .collect(Collectors.toSet());
                chapterSectionMap.put(chapter.getId(), sectionIds);
            }

            model.addAttribute("general", general);
            model.addAttribute("chapters", chapters);
            model.addAttribute("sections", sections);
            model.addAttribute("chapterSectionMap", chapterSectionMap);
            //model.addAttribute("chapterForm", new ChapterForm());

            return "admin/general-detail";
        } catch (Exception e) {
            // обработка ошибок
            model.addAttribute("error", "Ошибка при загрузке деталей генерального: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping
    public String createGeneral(@ModelAttribute General general) {
        try {
            General serviceGeneral = generalService.createGeneral(general);
            if (serviceGeneral == null){
               return "redirect:/admin/generals?error=ошибка_создания";
            }
            return "redirect:/admin/generals";
        } catch (Exception e) {
            return "redirect:/admin/generals?error=" + e.getMessage();
        }
    }

    @PostMapping("/{generalId}/chapters")
    public String createChapter(@PathVariable Long generalId,
                                @ModelAttribute ChapterForm chapterForm) {
        try {
            logger.info("Creating new chapter for general {}: {}", generalId, chapterForm.getName());
            Chapter chapter = new Chapter();
            chapter.setName(chapterForm.getName());
            chapter.setSrc(chapterForm.getSrc());

            chapterService.createChapter(chapter, generalId);
            return "redirect:/admin/generals/" + generalId;
        } catch (Exception e) {
            logger.error("Error creating chapter", e);
            return "redirect:/admin/generals/" + generalId + "?error=" + e.getMessage();
        }
    }

    @PostMapping("/{generalId}/chapters/{chapterId}/sections")
    public String updateChapterSections(@PathVariable Long generalId,
                                        @PathVariable Long chapterId,
                                        @RequestParam(required = false) List<Long> sectionIds) {
        try {
            logger.info("Updating sections for chapter {}: {}", chapterId, sectionIds);
            chapterService.updateChapterSections(chapterId, sectionIds != null ? sectionIds : List.of());
            return "redirect:/admin/generals/" + generalId;
        } catch (Exception e) {
            logger.error("Error updating chapter sections", e);
            return "redirect:/admin/generals/" + generalId + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/sections")
    public String getSectionsManagement(Model model) {
        try {
            logger.info("Fetching all sections");
            List<Section> sections = sectionService.getAllSections();
            model.addAttribute("sections", sections);
            model.addAttribute("newSection", new Section());
            return "admin/sections-management";
        } catch (Exception e) {
            logger.error("Error fetching sections", e);
            model.addAttribute("error", "Ошибка при загрузке разделов: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/sections")
    public String createSection(@ModelAttribute Section section) {
        try {
            logger.info("Creating new section: {}", section.getName());
            sectionService.createSection(section);
            return "redirect:/admin/generals/sections";
        } catch (Exception e) {
            logger.error("Error creating section", e);
            return "redirect:/admin/generals/sections?error=" + e.getMessage();
        }
    }
    @PostMapping("/{generalId}/save-sections")
    public String saveAllChapterSections(@PathVariable Long generalId,
                                         @RequestParam(value = "chapterSections", required = false) List<String> chapterSections) {
        try {
            logger.info("Saving all chapter sections for general: {}", generalId);

            if (chapterSections != null) {
                // Группируем по chapterId
                Map<Long, List<Long>> chapterSectionsMap = new HashMap<>();

                for (String chapterSection : chapterSections) {
                    String[] parts = chapterSection.split("_");
                    if (parts.length == 2) {
                        Long chapterId = Long.parseLong(parts[0]);
                        Long sectionId = Long.parseLong(parts[1]);

                        chapterSectionsMap.computeIfAbsent(chapterId, k -> new ArrayList<>()).add(sectionId);
                    }
                }

                // Обновляем связи для каждой главы
                for (Map.Entry<Long, List<Long>> entry : chapterSectionsMap.entrySet()) {
                    chapterService.updateChapterSections(entry.getKey(), entry.getValue());
                }

                logger.info("Updated sections for {} chapters", chapterSectionsMap.size());
            } else {
                // Если ничего не выбрано, очищаем все связи
                List<Chapter> chapters = chapterService.getChaptersByGeneral(generalId);
                for (Chapter chapter : chapters) {
                    chapterService.updateChapterSections(chapter.getId(), new ArrayList<>());
                }
                logger.info("Cleared all section associations");
            }

            return "redirect:/admin/generals/" + generalId + "?success=Sections+updated";

        } catch (Exception e) {
            logger.error("Error saving chapter sections", e);
            return "redirect:/admin/generals/" + generalId + "?error=Error+saving+sections";
        }
    }
}