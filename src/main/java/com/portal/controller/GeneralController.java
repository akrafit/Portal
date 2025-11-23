package com.portal.controller;

import com.portal.dto.YandexDiskItem;
import com.portal.dto.YandexDiskUploadResponse;
import com.portal.entity.General;
import com.portal.entity.Chapter;
import com.portal.entity.GeneralSection;
import com.portal.entity.Section;
import com.portal.service.GeneralService;
import com.portal.service.ChapterService;
import com.portal.service.SectionService;
import com.portal.dto.ChapterForm;
import com.portal.service.YandexDiskService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin/generals")
public class GeneralController {

    private final GeneralService generalService;
    private final ChapterService chapterService;
    private final SectionService sectionService;
    private final YandexDiskService yandexDiskService;

    public GeneralController(GeneralService generalService, ChapterService chapterService, SectionService sectionService, YandexDiskService yandexDiskService) {
        this.generalService = generalService;
        this.chapterService = chapterService;
        this.sectionService = sectionService;
        this.yandexDiskService = yandexDiskService;
    }

    @GetMapping
    public String getAllGenerals(Model model) {
        try {
            List<General> generals = generalService.getAllGenerals();
            model.addAttribute("generals", generals);
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
            Map<Long, Chapter> generalSectionMap = new HashMap<>();
            List<GeneralSection> generalSections = generalService.findByGeneral(general);
            for (GeneralSection gs : generalSections) {
                generalSectionMap.put(gs.getSection().getId(), gs.getChapter());
            }

            model.addAttribute("generalSectionMap", generalSectionMap);
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
            Chapter chapter = new Chapter();
            chapter.setName(chapterForm.getName());
            chapter.setSrc(chapterForm.getSrc());
            chapterService.createChapter(chapter, generalId);
            return "redirect:/admin/generals/" + generalId;
        } catch (Exception e) {
            return "redirect:/admin/generals/" + generalId + "?error=" + e.getMessage();
        }
    }

    @PostMapping("/{generalId}/chapters/{chapterId}/sections")
    public String updateChapterSections(@PathVariable Long generalId,
                                        @PathVariable Long chapterId,
                                        @RequestParam(required = false) List<Long> sectionIds) {
        try {
            chapterService.updateChapterSections(chapterId, sectionIds != null ? sectionIds : List.of());
            return "redirect:/admin/generals/" + generalId;
        } catch (Exception e) {
            return "redirect:/admin/generals/" + generalId + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/sections")
    public String getSectionsManagement(Model model) {
        try {
            List<Section> sections = sectionService.getAllSections();
//            List<Chapter> chapterList = sections.stream().map(Section::getTemplateChapter).toList();
//            if(chapterList.size() > 0) {
//                yandexDiskService.makeChaptersPublicUrl(chapterList);
//            }
            sections.sort(Comparator.comparing(Section::getName));
            model.addAttribute("sections", sections);
            model.addAttribute("newSection", new Section());
            return "admin/sections-management";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке разделов: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/sections")
    public String createSection(@ModelAttribute Section section) {
        try {
            sectionService.createSection(section);
            return "redirect:/admin/generals/sections";
        } catch (Exception e) {
            return "redirect:/admin/generals/sections?error=" + e.getMessage();
        }
    }
    @PostMapping("/{generalId}/save-sections")
    public String saveAllChapterSections(@PathVariable Long generalId,
                                         @RequestParam(value = "chapterSections", required = false) List<String> chapterSections) {
        try {

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

            } else {
                // Если ничего не выбрано, очищаем все связи
                List<Chapter> chapters = chapterService.getChaptersByGeneral(generalId);
                for (Chapter chapter : chapters) {
                    chapterService.updateChapterSections(chapter.getId(), new ArrayList<>());
                }

            }

            return "redirect:/admin/generals/" + generalId + "?success=Sections+updated";

        } catch (Exception e) {
            return "redirect:/admin/generals/" + generalId + "?error=Error+saving+sections";
        }
    }
    @PostMapping("/{generalId}/sections/{sectionId}/copy-template")
    public String copySectionTemplate(@PathVariable Long generalId,
                                      @PathVariable Long sectionId) {
        try {
            Section section = sectionService.getSectionById(sectionId);
            General general = generalService.getGeneralById(generalId);

            if (section == null || section.getTemplateChapter() == null || general == null) {
                return "redirect:/admin/generals/" + generalId + "?error=Section+or+template+not+found";
            }

            // Проверяем, нет ли уже шаблона для этой связи
            GeneralSection existing = generalService.findByGeneralAndSection(general, section);
            if (existing != null) {
                return "redirect:/admin/generals/" + generalId + "?error=Template+already+exists";
            }

            // Копируем файл
            Chapter templateChapter = section.getTemplateChapter();
            String sourcePath = templateChapter.getPath();
            String fileName = YandexDiskService.extractFileNameFromPath(sourcePath);
            String destinationPath = "/portal/templates/" + generalId + "/Шаблон_" + section.getName() + "_" + fileName;
            YandexDiskUploadResponse copyResult = yandexDiskService.copyFile(sourcePath, destinationPath);

            if (copyResult.getError() == null) {
                // Создаем Chapter для шаблона
                YandexDiskItem copiedFile = yandexDiskService.getFileInfo(destinationPath);
                Chapter newChapter = new Chapter(copiedFile, null);
                //newChapter.setGeneral(general);
                newChapter.setName("Шаблон_" + section.getName() + "_" + fileName);
                newChapter.setTemplate(true); // Помечаем как шаблон
                Chapter savedChapter = chapterService.saveChapter(newChapter);
                yandexDiskService.makeChaptersPublicUrl(List.of(savedChapter));

                // Создаем связь в general_section
                GeneralSection generalSection = new GeneralSection();
                generalSection.setGeneral(general);
                generalSection.setSection(section);
                generalSection.setChapter(savedChapter);
                generalSection.setCreatedAt(LocalDateTime.now());
                generalService.save(generalSection);
                chapterService.loadBookMarkToFile(generalSection);

                return "redirect:/admin/generals/" + generalId + "?success=Template+copied";
            }
        } catch (Exception e) {
            return "redirect:/admin/generals/" + generalId + "?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
        return "redirect:/admin/generals/" + generalId + "?error=Error+copying+template";
    }
}