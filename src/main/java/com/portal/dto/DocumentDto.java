package com.portal.dto;

import com.portal.service.DateUtils;
import lombok.Data;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DocumentDto {
    private String path;
    private String type;
    private String name;
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
    public DocumentDto(YandexDiskItem yandexDiskItem) {
        this.path = yandexDiskItem.getPath();
        this.type = yandexDiskItem.getType();
        this.name = yandexDiskItem.getName();
        this.created = DateUtils.formatDateTime(yandexDiskItem.getCreated());
        this.modified = yandexDiskItem.getModified();
        this.size = yandexDiskItem.getSize();
        this.mimeType = yandexDiskItem.getMimeType();
        this.preview = yandexDiskItem.getPreview();
        this.mediaType = yandexDiskItem.getMediaType();
        this.file = yandexDiskItem.getFile();
        this.sizes = yandexDiskItem.getSizes();
        this.dot = getFileExtension(yandexDiskItem.getName());
        this.publicUrl = yandexDiskItem.getPublicUrl();
        System.out.println(this.publicUrl);
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
}
