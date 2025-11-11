package com.portal.entity;

public enum DocumentType {
    XLSX("📊", "Excel документ"),
    XLSM("📈", "Excel с макросами"),
    DOC("📝", "Word документ"),
    DOCX("📄", "Word документ"),
    PDF("📕", "PDF документ"),
    OTHER("📁", "Другой файл");

    private final String icon;
    private final String description;

    DocumentType(String icon, String description) {
        this.icon = icon;
        this.description = description;
    }

    public String getIcon() { return icon; }
    public String getDescription() { return description; }

    public static DocumentType fromMimeType(String mimeType) {
        if (mimeType == null) return OTHER;

        return switch (mimeType) {
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> XLSX;
            case "application/vnd.ms-excel.sheet.macroEnabled.12" -> XLSM;
            case "application/msword" -> DOC;
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> DOCX;
            case "application/pdf" -> PDF;
            default -> OTHER;
        };
    }

    public static DocumentType fromFileName(String fileName) {
        if (fileName == null) return OTHER;

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "xlsx" -> XLSX;
            case "xlsm" -> XLSM;
            case "doc" -> DOC;
            case "docx" -> DOCX;
            case "pdf" -> PDF;
            default -> OTHER;
        };
    }
}