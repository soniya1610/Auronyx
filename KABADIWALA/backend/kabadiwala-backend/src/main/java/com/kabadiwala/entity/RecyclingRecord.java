package com.kabadiwala.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recycling_records")
public class RecyclingRecord {

    public enum Status {
        COLLECTED, SORTED, AGGREGATED, TRANSPORT, RECEIVED, PROCESSING, RECYCLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pickup_id", nullable = false)
    private Pickup pickup;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "waste_category_id", nullable = false)
    private WasteCategory wasteCategory;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "waste_item_id")
    private Waste wasteItem;

    @Column(nullable = false, precision = 8, scale = 3)
    private BigDecimal weight;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "collector_id", nullable = false)
    private Collector collector;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recycler_id")
    private Recycler recycler;

    @Column(name = "handover_info", columnDefinition = "TEXT")
    private String handoverInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Status status = Status.COLLECTED;

    @Column(name = "processing_info", columnDefinition = "TEXT")
    private String processingInfo;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public RecyclingRecord() {}

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }
    public Pickup getPickup() { return pickup; }
    public void setPickup(Pickup pickup) { this.pickup = pickup; }
    public WasteCategory getWasteCategory() { return wasteCategory; }
    public void setWasteCategory(WasteCategory wasteCategory) { this.wasteCategory = wasteCategory; }
    public Waste getWasteItem() { return wasteItem; }
    public void setWasteItem(Waste wasteItem) { this.wasteItem = wasteItem; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public Collector getCollector() { return collector; }
    public void setCollector(Collector collector) { this.collector = collector; }
    public Recycler getRecycler() { return recycler; }
    public void setRecycler(Recycler recycler) { this.recycler = recycler; }
    public String getHandoverInfo() { return handoverInfo; }
    public void setHandoverInfo(String handoverInfo) { this.handoverInfo = handoverInfo; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getProcessingInfo() { return processingInfo; }
    public void setProcessingInfo(String processingInfo) { this.processingInfo = processingInfo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
