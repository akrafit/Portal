package com.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

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

    // Геттеры и сеттеры
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCreated() { return created; }
    public void setCreated(String created) { this.created = created; }

    public String getModified() { return modified; }
    public void setModified(String modified) { this.modified = modified; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public String getMd5() { return md5; }
    public void setMd5(String md5) { this.md5 = md5; }

    public String getSha256() { return sha256; }
    public void setSha256(String sha256) { this.sha256 = sha256; }

    public String getPreview() { return preview; }
    public void setPreview(String preview) { this.preview = preview; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public String getPublicUrl() { return publicUrl; }
    public void setPublicUrl(String publicUrl) { this.publicUrl = publicUrl; }

    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public List<PreviewSize> getSizes() { return sizes; }
    public void setSizes(List<PreviewSize> sizes) { this.sizes = sizes; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public Long getRevision() { return revision; }
    public void setRevision(Long revision) { this.revision = revision; }

    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }

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