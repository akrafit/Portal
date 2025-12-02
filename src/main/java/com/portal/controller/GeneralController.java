package com.portal.controller;

import com.portal.dto.FileItem;
import com.portal.entity.General;
import com.portal.entity.Chapter;
import com.portal.entity.GeneralSection;
import com.portal.entity.Section;
import com.portal.service.GeneralService;
import com.portal.service.ChapterService;
import com.portal.service.SectionService;
import com.portal.service.LocalFileService;
import com.portal.dto.ChapterForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final LocalFileService localFileService;

    public GeneralController(GeneralService generalService,
                             ChapterService chapterService,
                             SectionService sectionService,
                             LocalFileService localFileService) {
        this.generalService = generalService;
        this.chapterService = chapterService;
        this.sectionService = sectionService;
        this.localFileService = localFileService;
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

            return "admin/general-detail";
        } catch (Exception e) {
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

    /**
     * Новый метод для загрузки файла в шаблон
     */
    @PostMapping("/{generalId}/upload-template")
    public String uploadTemplateFile(@PathVariable Long generalId,
                                     @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return "redirect:/admin/generals/" + generalId + "?error=Файл+не+выбран";
            }

            General general = generalService.getGeneralById(generalId);
            if (general == null) {
                return "redirect:/admin/generals/" + generalId + "?error=Шаблон+не+найден";
            }

            // Сохраняем файл локально
            String filePath = localFileService.saveTemplateFile(file, generalId);

            // Создаем запись в БД
            FileItem item = new FileItem();
            item.setName(file.getOriginalFilename());
            item.setPath(filePath);
            item.setType("file");

            chapterService.createChapterForTemplate(item, generalId);

            return "redirect:/admin/generals/" + generalId + "?success=Файл+успешно+загружен";

        } catch (Exception e) {
            log.error("Ошибка загрузки файла в шаблон: {}", e.getMessage());
            return "redirect:/admin/generals/" + generalId + "?error=" +
                    URLEncoder.encode("Ошибка загрузки: " + e.getMessage(), StandardCharsets.UTF_8);
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

            // Копируем файл локально
            Chapter templateChapter = section.getTemplateChapter();
            String sourcePath = templateChapter.getPath();

            // Создаем новое имя файла для шаблона
            String fileName = extractFileNameFromPath(sourcePath);
            String newFileName = "Шаблон_" + section.getName() + "_" + fileName;
            String destinationPath = "portal/templates/" + generalId + "/" + newFileName;

            // Копируем файл в локальном хранилище
            localFileService.copyFile(sourcePath, destinationPath);

            // Создаем Chapter для шаблона
            Chapter newChapter = new Chapter();
            newChapter.setName(newFileName);
            newChapter.setPath(destinationPath);
            newChapter.setTemplate(true);
            newChapter.setGeneral(general);
            newChapter.setSrc("/" + generalId + "/" + newFileName);

            Chapter savedChapter = chapterService.saveChapter(newChapter);

            // Создаем связь в general_section
            GeneralSection generalSection = new GeneralSection();
            generalSection.setGeneral(general);
            generalSection.setSection(section);
            generalSection.setChapter(savedChapter);
            generalSection.setCreatedAt(LocalDateTime.now());
            generalService.save(generalSection);

            // Загружаем bookmark если нужно
//            chapterService.loadBookMarkToFile(generalSection);

            return "redirect:/admin/generals/" + generalId + "?success=Template+copied";

        } catch (Exception e) {
            log.error("Ошибка копирования шаблона: {}", e.getMessage());
            return "redirect:/admin/generals/" + generalId + "?error=" +
                    URLEncoder.encode("Ошибка копирования: " + e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Вспомогательный метод для извлечения имени файла из пути
     */
    private String extractFileNameFromPath(String path) {
        if (path == null || path.isEmpty()) {
            return "file_" + System.currentTimeMillis();
        }
        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < path.length() - 1) {
            return path.substring(lastSlashIndex + 1);
        }
        return path;
    }
}