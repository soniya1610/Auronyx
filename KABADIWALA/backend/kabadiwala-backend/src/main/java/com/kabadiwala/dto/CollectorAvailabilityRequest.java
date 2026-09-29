package com.kabadiwala.dto;

import jakarta.validation.constraints.NotNull;

public class CollectorAvailabilityRequest {

    @NotNull(message = "isAvailable flag is required")
    private Boolean isAvailable;

    private String workingHours;

    public CollectorAvailabilityRequest() {
    }

    public CollectorAvailabilityRequest(Boolean isAvailable, String workingHours) {
        this.isAvailable = isAvailable;
        this.workingHours = workingHours;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }
}
