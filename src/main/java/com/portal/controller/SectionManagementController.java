//package com.portal.controller;
//
//import com.portal.entity.Section;
//import com.portal.service.SectionService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/section-management")
//public class SectionManagementController {
//
//    private final SectionService sectionService;
//
//    public SectionManagementController(SectionService sectionService) {
//        this.sectionService = sectionService;
//    }
//
//    @GetMapping
//    public String getSectionManagementPage(Model model) {
//        List<Section> sections = sectionService.getAllSectionsWithTemplates();
//        model.addAttribute("sections", sections);
//        return "section-management";
//    }
//}
