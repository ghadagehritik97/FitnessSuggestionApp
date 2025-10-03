package com.fitness.aiservice.controller;

import com.fitness.aiservice.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.fitness.aiservice.entity.Recommendations;

@RestController
public class RecommendationController {
    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/recommendations/user/{userId}")
    private ResponseEntity<List<Recommendations>> getRecommendations(@PathVariable String userId)
    {
        return ResponseEntity.ok(recommendationService.getRecommendations(userId));
    }

    @GetMapping("/recommendations/activity/{activityId}")
    private ResponseEntity<Recommendations> getRecommendationsForActivity(@PathVariable String activityId)
    {
        return ResponseEntity.ok(recommendationService.getRecommendationsByActivity(activityId));
    }
}
