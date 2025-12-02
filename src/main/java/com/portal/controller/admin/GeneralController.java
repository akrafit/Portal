package com.portal.controller.admin;

import com.portal.entity.*;
import com.portal.service.GeneralService;
import com.portal.service.ChapterService;
import com.portal.service.SectionService;
import com.portal.service.LocalFileService;
import com.portal.service.GeneralSectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin/generals")
public class GeneralController {

    private final GeneralService generalService;
    private final ChapterService chapterService;
    private final SectionService sectionService;
    private final LocalFileService localFileService;
    private final GeneralSectionService generalSectionService;

    public GeneralController(GeneralService generalService,
                             ChapterService chapterService,
                             SectionService sectionService,
                             LocalFileService localFileService,
                             GeneralSectionService generalSectionService) {
        this.generalService = generalService;
        this.chapterService = chapterService;
        this.sectionService = sectionService;
        this.localFileService = localFileService;
        this.generalSectionService = generalSectionService;
    }

    @GetMapping
    public String getAllGenerals(Model model) {
        try {
            List<General> generals = generalService.getAllGenerals();
            model.addAttribute("generals", generals);
            if(!generals.isEmpty()){
                model.addAttribute("generals", generals);
            }else{
                model.addAttribute("generals",  null);
            }
            return "admin/generals-list";
        } catch (Exception e) {
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

            // Получаем все главы (обычные)
            List<Chapter> chapters = chapterService.getChaptersByGeneralTemplate(general);

            List<Section> sections = sectionService.getAllSections();

            // Создаем Map для быстрой проверки связей (для всех глав)
            Map<Long, Set<Long>> chapterSectionMap = new HashMap<>();

            // Добавляем связи для обычных глав
            for (Chapter chapter : chapters) {
                Set<Long> sectionIds = chapter.getSections().stream()
                        .map(Section::getId)
                        .collect(Collectors.toSet());
                chapterSectionMap.put(chapter.getId(), sectionIds);
            }

            // Создаем Map для связи разделов с шаблонными главами через GeneralSection
            Map<Long, Chapter> generalSectionMap = new HashMap<>();
            List<GeneralSection> generalSections = generalSectionService.findByGeneral(general);
            for (GeneralSection gs : generalSections) {
                if (gs.getChapter() != null) {
                    generalSectionMap.put(gs.getSection().getId(), gs.getChapter());
                }
            }

            model.addAttribute("generalSectionMap", generalSectionMap);
            model.addAttribute("general", general);
            model.addAttribute("chapters", chapters);
            model.addAttribute("sections", sections);
            model.addAttribute("chapterSectionMap", chapterSectionMap);

            return "admin/general-detail";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке деталей генерального: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/sections")
    public String getSectionsManagement(Model model) {
        try {
            List<Section> sections = sectionService.getAllSections();
            sections.sort(Comparator.comparing(Section::getName));
            model.addAttribute("sections", sections);
            model.addAttribute("newSection", new Section());
            return "admin/sections-management";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке разделов: " + e.getMessage());
            return "error";
        }
    }
}