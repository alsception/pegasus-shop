package org.alsception.pegasus.core.config;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Size;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "config")
public class PGSConfig 
{    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, unique = true, length = 100)
    @jakarta.validation.constraints.NotBlank(message = " name cannot be blank")
    @Size(max = 100, message = " name cannot exceed 100 characters")
    private String name;
    
    @Column(name = "value", length = 500)
    @Size(max = 500, message = " value cannot exceed 500 characters")
    private String value;
    
    @Column(name = "is_boolean", nullable = false)
    private Boolean isBoolean = false;
    
    @Column(name = "description", length = 255)
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
    
    @Column(name = "category", length = 50)
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    @Column(name = "created_by", length = 50)
    private String createdBy;
    
    @Column(name = "updated_by", length = 50)
    private String updatedBy;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
        
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Utility methods
    public Boolean getBooleanvalue() {
        if (isBoolean && value != null) {
            return Boolean.parseBoolean(value);
        }
        return null;
    }
    
    public void setBooleanvalue(Boolean value) {
        this.value = value != null ? value.toString() : null;
        this.isBoolean = true;
    }
    
    public Integer getIntegervalue() {
        if (value != null && !isBoolean) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    public void setIntegervalue(Integer value) {
        this.value = value != null ? value.toString() : null;
        this.isBoolean = false;
    }
    
    public Double getDoublevalue() {
        if (value != null && !isBoolean) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    public void setDoublevalue(Double value) {
        this.value = value != null ? value.toString() : null;
        this.isBoolean = false;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getname() {
        return name;
    }
    
    public void setname(String name) {
        this.name = name;
    }
    
    public String getvalue() {
        return value;
    }
    
    public void setvalue(String value) {
        this.value = value;
    }
    
    public Boolean getIsBoolean() {
        return isBoolean;
    }
    
    public void setIsBoolean(Boolean isBoolean) {
        this.isBoolean = isBoolean;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Config{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", isBoolean=" + isBoolean +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", isActive=" + isActive +
                ", displayOrder=" + displayOrder +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}