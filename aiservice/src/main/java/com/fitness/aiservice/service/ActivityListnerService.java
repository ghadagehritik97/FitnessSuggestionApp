package com.fitness.aiservice.service;

import com.fitness.aiservice.entity.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityListnerService {
    private final ActivityAiService activityAiService;
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processActivity(Activity activity){
        log.info("Message recieved from RabbitMQ: {}",activity.getId());
        CompletableFuture
                .supplyAsync(() -> activityAiService.getRecommendation(activity))
                .thenAccept(recommendation -> log.info("Generated recommendation: {}", recommendation))
                .exceptionally(ex -> {
                            log.error("Failed to generate recommendation for activity {}: {}", activity.getId(), ex.getMessage(), ex);
                            return null;
                        });
        log.info("Meassage consumed successfully for activity: {}",activity.getId());

    }
}
