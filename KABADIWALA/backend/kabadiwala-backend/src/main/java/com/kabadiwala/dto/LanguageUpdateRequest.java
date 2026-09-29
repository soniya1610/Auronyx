package com.kabadiwala.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LanguageUpdateRequest {

    @NotBlank(message = "Language code is required")
    @Pattern(regexp = "^[a-zA-Z]{2,10}(-[a-zA-Z0-9]{2,10})?$", message = "Invalid language code format (e.g. en, hi, ta, te)")
    private String language;

    public LanguageUpdateRequest() {
    }

    public LanguageUpdateRequest(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
