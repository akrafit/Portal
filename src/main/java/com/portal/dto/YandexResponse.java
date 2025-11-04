package com.portal.dto;

import lombok.Data;

@Data
public class YandexResponse {
    private String method;
    private String href;
    private String templated;
    private String error;
    private String description;
    private String message;
    private String reason;
    private String limit;
}
