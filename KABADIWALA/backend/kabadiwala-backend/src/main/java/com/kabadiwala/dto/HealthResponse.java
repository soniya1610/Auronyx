package com.kabadiwala.dto;

import java.time.Instant;

public class HealthResponse {
    private String status;
    private Instant timestamp;
    private String database;

    public HealthResponse() {
        this.timestamp = Instant.now();
    }

    public HealthResponse(String status, String database) {
        this.status = status;
        this.database = database;
        this.timestamp = Instant.now();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
