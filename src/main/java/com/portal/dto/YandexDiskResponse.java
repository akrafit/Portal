package com.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class YandexDiskResponse {
    private String path;
    private String type;
    private String name;
    private String created;
    private String modified;

    @JsonProperty("_embedded")
    private Embedded embedded;

    private String resourceId;
    private Long revision;

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

    public Embedded getEmbedded() { return embedded; }
    public void setEmbedded(Embedded embedded) { this.embedded = embedded; }

    @JsonProperty("resource_id")
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public Long getRevision() { return revision; }
    public void setRevision(Long revision) { this.revision = revision; }

    public static class Embedded {
        private String path;
        private Integer limit;
        private Integer offset;
        private String sort;
        private Integer total;
        private List<YandexDiskItem> items;

        // Геттеры и сеттеры
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }

        public Integer getLimit() { return limit; }
        public void setLimit(Integer limit) { this.limit = limit; }

        public Integer getOffset() { return offset; }
        public void setOffset(Integer offset) { this.offset = offset; }

        public String getSort() { return sort; }
        public void setSort(String sort) { this.sort = sort; }

        public Integer getTotal() { return total; }
        public void setTotal(Integer total) { this.total = total; }

        public List<YandexDiskItem> getItems() { return items; }
        public void setItems(List<YandexDiskItem> items) { this.items = items; }
    }
}
