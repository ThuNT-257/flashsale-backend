package com.sapotest.flashsale.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a Flash Sale event.
 * Defines the timeframe, user participation limits, and associated products.
 */
@Entity
@Table(name = "flash_sales")
public class FlashSale {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "is_active")
    private boolean isActive = true;

    /**
     * The business rule constraint for maximum items a single user can purchase.
     */
    @Column(name = "max_quantity_per_user")
    private int maxQuantityPerUser = 2;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "flashSale", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FlashSaleProduct> flashSaleProducts;

    /**
     * Default constructor for JPA.
     */
    public FlashSale() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getMaxQuantityPerUser() { return maxQuantityPerUser; }
    public void setMaxQuantityPerUser(int maxQuantityPerUser) { this.maxQuantityPerUser = maxQuantityPerUser; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public List<FlashSaleProduct> getFlashSaleProducts() { return flashSaleProducts; }
    public void setFlashSaleProducts(List<FlashSaleProduct> flashSaleProducts) { this.flashSaleProducts = flashSaleProducts; }
}