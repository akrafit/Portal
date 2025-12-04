package com.portal.dto;

import com.portal.entity.Chapter;
import com.portal.service.DateUtils;
import lombok.Data;

import java.io.File;
import java.util.List;

@Data
public class DocumentDto {
    private Long id;
    private String path;
    private String type;
    private String name;
    private String nameTow;
    private String created;
    private String modified;
    private Long size;
    private String mimeType;
    private String preview;
    private String mediaType;
    private List<YandexDiskItem.PreviewSize> sizes;
    private String file;
    private String dot;
    private String publicUrl;
    public DocumentDto(Chapter chapter) {
        this.id = chapter.getId();
        this.path = chapter.getPath();
        this.name = removeFileExtension(chapter.getName());
        this.created = chapter.getCreated();
        this.modified = chapter.getModified();
        this.size = chapter.getSize();
        this.dot = getFileExtension(chapter.getName());
        this.publicUrl = chapter.getPublicUrl();
    }


    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        File file = new File(fileName);
        String name = file.getName();

        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < name.length() - 1) {
            return name.substring(lastDotIndex + 1).toLowerCase();
        }

        return "";
    }
    public static String removeFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return filename;
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return filename; // нет расширения
        }

        return filename.substring(0, lastDotIndex);
    }
}
