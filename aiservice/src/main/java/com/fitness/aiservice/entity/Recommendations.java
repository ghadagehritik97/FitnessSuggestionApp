package com.fitness.aiservice.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "recommendations")
@Builder //useful for creating instances of the class
@AllArgsConstructor
@NoArgsConstructor
public class Recommendations {
    @Id
    private String id;
    private String userId;
    private String activityId;
    private String activityType;
    private String recommendation;
    private List<String>improvements;
    private List<String>suggestions;
    private  List<String>safetyTips;
    @CreatedDate
    private LocalDateTime createdDate;

}

