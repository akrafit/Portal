package com.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class YandexDiskItem {
    private String path;
    private String type;
    private String name;
    private String created;
    private String modified;
    private Long size;

    @JsonProperty("mime_type")
    private String mimeType;

    private String md5;
    private String sha256;
    private String preview;

    @JsonProperty("public_key")
    private String publicKey;

    @JsonProperty("public_url")
    private String publicUrl;

    @JsonProperty("media_type")
    private String mediaType;

    private List<PreviewSize> sizes;

    @JsonProperty("resource_id")
    private String resourceId;

    private Long revision;
    private String file;

    private String error;

    public YandexDiskItem(String error) {
        this.error = error;
    }

    public static class PreviewSize {
        private String url;
        private String name;

        // Геттеры и сеттеры
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}