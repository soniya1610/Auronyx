package com.kabadiwala.dto;

import java.util.List;

public class ConfigResponse {
    private String appName;
    private String appVersion;
    private List<String> supportedLanguages;
    private List<String> supportedRoles;
    private boolean otpVerificationEnabled;

    public ConfigResponse() {
    }

    public ConfigResponse(String appName, String appVersion, List<String> supportedLanguages, List<String> supportedRoles, boolean otpVerificationEnabled) {
        this.appName = appName;
        this.appVersion = appVersion;
        this.supportedLanguages = supportedLanguages;
        this.supportedRoles = supportedRoles;
        this.otpVerificationEnabled = otpVerificationEnabled;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public List<String> getSupportedLanguages() {
        return supportedLanguages;
    }

    public void setSupportedLanguages(List<String> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }

    public List<String> getSupportedRoles() {
        return supportedRoles;
    }

    public void setSupportedRoles(List<String> supportedRoles) {
        this.supportedRoles = supportedRoles;
    }

    public boolean isOtpVerificationEnabled() {
        return otpVerificationEnabled;
    }

    public void setOtpVerificationEnabled(boolean otpVerificationEnabled) {
        this.otpVerificationEnabled = otpVerificationEnabled;
    }
}
