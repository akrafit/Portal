package com.portal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YandexDiskUploadResponse {
    @JsonProperty("href")
    private String uploadUrl;

    private String method;

    private String error;

    private Boolean templated;
    private String description;
    private String message;

}