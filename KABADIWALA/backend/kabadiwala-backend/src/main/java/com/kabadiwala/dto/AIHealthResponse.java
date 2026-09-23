package com.kabadiwala.dto;

public class AIHealthResponse {
    private String status;
    private String url;
    private String mode;
    private String message;
    private boolean available;

    public AIHealthResponse() {}

    public AIHealthResponse(boolean available, String mode, String message) {
        this.available = available;
        this.mode = mode;
        this.message = message;
        this.status = available ? "UP" : "DOWN";
    }

    public AIHealthResponse(String status, String url, String mode, String message) {
        this.status = status;
        this.url = url;
        this.mode = mode;
        this.message = message;
        this.available = "UP".equalsIgnoreCase(status);
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
        this.available = "UP".equalsIgnoreCase(status);
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
