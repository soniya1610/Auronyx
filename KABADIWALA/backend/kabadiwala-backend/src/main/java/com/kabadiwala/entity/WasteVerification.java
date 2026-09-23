package com.kabadiwala.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "waste_verifications")
public class WasteVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pickup_id", nullable = false, unique = true)
    private Pickup pickup;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "collector_id", nullable = false)
    private Collector collector;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "actual_category_id", nullable = false)
    private WasteCategory actualCategory;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "actual_waste_item_id")
    private Waste actualWasteItem;

    @Column(name = "actual_weight", nullable = false, precision = 8, scale = 3)
    private BigDecimal actualWeight;

    @Column(name = "item_condition", length = 50)
    private String condition;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt = LocalDateTime.now();

    public WasteVerification() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Pickup getPickup() { return pickup; }
    public void setPickup(Pickup pickup) { this.pickup = pickup; }
    public Collector getCollector() { return collector; }
    public void setCollector(Collector collector) { this.collector = collector; }
    public WasteCategory getActualCategory() { return actualCategory; }
    public void setActualCategory(WasteCategory actualCategory) { this.actualCategory = actualCategory; }
    public Waste getActualWasteItem() { return actualWasteItem; }
    public void setActualWasteItem(Waste actualWasteItem) { this.actualWasteItem = actualWasteItem; }
    public BigDecimal getActualWeight() { return actualWeight; }
    public void setActualWeight(BigDecimal actualWeight) { this.actualWeight = actualWeight; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
}
