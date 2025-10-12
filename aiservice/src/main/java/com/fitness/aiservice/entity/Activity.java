package com.fitness.aiservice.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class Activity {
    private String id;
    private String type; // e.g., running, cycling, swimming
    private String userId;
    private Integer duration; // in minutes
    private Integer caloriesBurned;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
