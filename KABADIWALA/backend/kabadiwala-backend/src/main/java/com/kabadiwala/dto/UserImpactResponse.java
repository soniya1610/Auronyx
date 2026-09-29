package com.kabadiwala.dto;

public class UserImpactResponse {
    private boolean available;
    private Object data;
    private String message;

    public UserImpactResponse() {
        this.available = false;
        this.data = null;
        this.message = "Environmental impact metrics will be available upon Waste Operations and Ecosystem module integration.";
    }

    public UserImpactResponse(boolean available, Object data, String message) {
        this.available = available;
        this.data = data;
        this.message = message;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
