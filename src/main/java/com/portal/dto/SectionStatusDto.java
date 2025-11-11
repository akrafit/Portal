package com.portal.dto;

import com.portal.entity.Project;
import com.portal.entity.Section;

public class SectionStatusDto {
    private Section section;
    private boolean generated;

    // простой конструктор
    public static SectionStatusDto from(Section section, Project project) {
        SectionStatusDto dto = new SectionStatusDto();
        dto.setSection(section);
        dto.setGenerated(project.getGeneratedSections().contains(section));
        return dto;
    }

    // геттеры
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    // вспомогательные методы для HTML
    public String getStatusText() {
        return generated ? "Сгенерирован" : "Не сгенерирован";
    }

    public String getBadgeClass() {
        return generated ? "status-generated" : "status-not-generated";
    }

    public String getButtonText() {
        return generated ? "Обновить" : "Сгенерировать";
    }

    public String getButtonIcon() {
        return generated ? "🔄" : "📄";
    }
}
