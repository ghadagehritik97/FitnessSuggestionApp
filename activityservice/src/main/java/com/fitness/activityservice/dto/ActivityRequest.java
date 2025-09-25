package com.fitness.activityservice.dto;

import com.fitness.activityservice.entity.ActivityType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ActivityRequest {
    private String userId;
    private ActivityType activityType;
    private Integer caloriesBurned;
    private Integer duration;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics;
}
