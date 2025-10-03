package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.entity.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ActivityServiceImpl implements ActivityService{

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ActivityRepository actRepository;

    @Autowired
    private UserDetailsWebClient userDetailsWebClient;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Override
    public ActivityResponse trackActivity(ActivityRequest activityRequest) {
        Boolean isValid = userDetailsWebClient.validateUser(activityRequest.getUserId());
        if (!isValid) {
            throw new RuntimeException("Invalid userId: " + activityRequest.getUserId());
        }
        Activity activity = Activity.builder()
                .userId(activityRequest.getUserId())
                .type(activityRequest.getActivityType())
                .caloriesBurned(activityRequest.getCaloriesBurned())
                .duration(activityRequest.getDuration())
                .startTime(activityRequest.getStartTime())
                .additionalMetrics(activityRequest.getAdditionalMetrics())
                .build();
        Activity savedActivity = actRepository.save(activity);
        // publish to rabbitmq with try and catch
         try {
             rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
         } catch (Exception e) {
                log.error("Error publishing to RabbitMQ: {}", e.getMessage());
         }
        return mapToResponse(savedActivity);
    }

    @Override
    public List<ActivityResponse> getUserActivities( String userId) {
        List<Activity>activities=actRepository.findByUserId(userId);
        return activities.stream().map(this::mapToResponse).toList();
    }

    @Override
    public ActivityResponse getActivity(String activityId) {
        Activity activity=actRepository.findById(activityId).orElseThrow(()->new RuntimeException("Activity not found"));
        return mapToResponse(activity);
    }

    private ActivityResponse mapToResponse(Activity savedActivity) {
        ActivityResponse activityRespoonse=new ActivityResponse();
        activityRespoonse.setId(savedActivity.getId());
        activityRespoonse.setUserId(savedActivity.getUserId());
        activityRespoonse.setType(savedActivity.getType());
        activityRespoonse.setDuration(savedActivity.getDuration());
        activityRespoonse.setCaloriesBurned(savedActivity.getCaloriesBurned());
        activityRespoonse.setStartTime(savedActivity.getStartTime());
        activityRespoonse.setAdditionalMetrics(savedActivity.getAdditionalMetrics());
        activityRespoonse.setCreatedDate(savedActivity.getCreatedDate());
        activityRespoonse.setUpdatedDate(savedActivity.getUpdatedDate());
        return activityRespoonse;
    }
}
