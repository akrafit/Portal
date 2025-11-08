package com.portal.dto;

import java.util.ArrayList;
import java.util.List;

public class ChapterForm {
    private String name;
    private String src;
    private List<Long> sectionIds = new ArrayList<>();

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSrc() { return src; }
    public void setSrc(String src) { this.src = src; }

    public List<Long> getSectionIds() { return sectionIds; }
    public void setSectionIds(List<Long> sectionIds) { this.sectionIds = sectionIds; }
}
