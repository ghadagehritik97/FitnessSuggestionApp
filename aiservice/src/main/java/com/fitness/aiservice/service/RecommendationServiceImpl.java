package com.fitness.aiservice.service;

import com.fitness.aiservice.entity.Recommendations;
import com.fitness.aiservice.repository.RecommendationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecommendationServiceImpl implements RecommendationService{

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Override
    public List<Recommendations> getRecommendations(String userId) {
        List<Recommendations>recommendations=recommendationRepository.findByUserId(userId);
        return recommendations;
    }

    @Override
    public Recommendations getRecommendationsByActivity(String activityId) {
        Optional<Recommendations>recommendations=recommendationRepository.findByActivityId(activityId);
        return recommendations.orElseThrow(()->new RuntimeException("Recommendations not found for activityId: "+activityId));
    }
}
