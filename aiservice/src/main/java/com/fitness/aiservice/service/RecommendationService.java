package com.fitness.aiservice.service;

import com.fitness.aiservice.entity.Recommendations;

import java.util.List;

public interface RecommendationService {
    List<Recommendations> getRecommendations(String userId);

    Recommendations getRecommendationsByActivity(String activityId);
}
