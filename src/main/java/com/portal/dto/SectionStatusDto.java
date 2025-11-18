package com.portal.dto;

import com.portal.entity.Section;
import com.portal.entity.Project;
import com.portal.entity.User;
import com.portal.service.ProjectService;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SectionStatusDto {
    private Section section;
    private boolean generated;
    private String statusText;
    private String badgeClass;
    private String buttonText;
    private String buttonIcon;
    private List<User> assignedUsers = new ArrayList<>();

    // Конструкторы, геттеры и сеттеры

    public static SectionStatusDto from(Section section, Project project) {
        SectionStatusDto dto = new SectionStatusDto();
        dto.setSection(section);

        boolean isGenerated = project.getGeneratedSections().contains(section);
        dto.setGenerated(isGenerated);

        if (isGenerated) {
            dto.setStatusText("Сгенерировано");
            dto.setBadgeClass("status-generated");
            dto.setButtonText("Перегенерировать");
            dto.setButtonIcon("🔄");
        } else {
            dto.setStatusText("Не сгенерировано");
            dto.setBadgeClass("status-not-generated");
            dto.setButtonText("Сгенерировать");
            dto.setButtonIcon("⚡");
        }

        return dto;
    }
}