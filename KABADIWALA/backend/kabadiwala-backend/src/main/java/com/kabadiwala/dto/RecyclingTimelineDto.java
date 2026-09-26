package com.kabadiwala.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class RecyclingTimelineDto {
    private Long recordId;
    private Long pickupId;
    private String transactionRef;
    private String categoryName;
    private String itemName;
    private BigDecimal weight;
    private BigDecimal co2SavedKg;
    private String currentStatus;
    private List<TimelineStage> stages;

    public static class TimelineStage {
        private String stage;
        private String title;
        private String description;
        private boolean completed;
        private boolean current;
        private LocalDateTime timestamp;

        public TimelineStage() {}

        public TimelineStage(String stage, String title, String description, boolean completed, boolean current, LocalDateTime timestamp) {
            this.stage = stage;
            this.title = title;
            this.description = description;
            this.completed = completed;
            this.current = current;
            this.timestamp = timestamp;
        }

        public String getStage() { return stage; }
        public void setStage(String stage) { this.stage = stage; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
        public boolean isCurrent() { return current; }
        public void setCurrent(boolean current) { this.current = current; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    public RecyclingTimelineDto() {}

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getPickupId() { return pickupId; }
    public void setPickupId(Long pickupId) { this.pickupId = pickupId; }
    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public BigDecimal getCo2SavedKg() { return co2SavedKg; }
    public void setCo2SavedKg(BigDecimal co2SavedKg) { this.co2SavedKg = co2SavedKg; }
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
    public List<TimelineStage> getStages() { return stages; }
    public void setStages(List<TimelineStage> stages) { this.stages = stages; }
}
