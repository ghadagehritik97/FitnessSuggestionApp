package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.entity.Activity;
import com.fitness.aiservice.entity.Recommendations;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {
    private final GeminiService geminiService;
    private final RecommendationRepository recommendationRepository;
    public String getRecommendation(Activity activity) {
        log.info("Generating recommendation for activity: {}", activity.getType());
        String response=geminiService.getGeminiResponse(generatePrompt(activity));
        log.info("Received response from Gemini API: {}", response);
        processAiResponse(activity,response);
        return response;

    }
    public void processAiResponse(Activity activity,String aiResponse){
        try{
            ObjectMapper objectMapper=new ObjectMapper();
            JsonNode jsonNode=objectMapper.readTree(aiResponse);
            JsonNode analysis=jsonNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");
            String wholeAnalysis=analysis.asText()
                    .replaceAll("```json\\n","")
                    .replaceAll("\\n```","")
                    .trim();
            log.info("Parsed analysis: {}",analysis);
            Recommendations recommendations=new Recommendations();

            JsonNode node =objectMapper.readTree(wholeAnalysis);
            JsonNode analysisNode=node.path("analysis");
            StringBuilder analysisSummary=new StringBuilder();
            formatAnalysis(analysisNode,analysisSummary, "overall","Overall: ");
            formatAnalysis(analysisNode,analysisSummary, "pace","Pace: ");
            formatAnalysis(analysisNode,analysisSummary, "heartRate","Heart Rate: ");
            formatAnalysis(analysisNode,analysisSummary, "caloriesBurned","Calories Burned: ");

            List<String>improvements=extractImprovementsAndSuggestions(node,"improvements");
            List<String>suggestions=extractImprovementsAndSuggestions(node,"suggestions");
            List<String>safetyTips=extractSafetyTips(node,"safety");
            setAndSaveRecommendation(activity, recommendations, analysisSummary, improvements, suggestions, safetyTips);
        }catch (Exception e){
            log.error("Error processing AI response for activity {}: {}",activity.getId(),e.getMessage(),e);
        }
    }

    private void setAndSaveRecommendation(Activity activity, Recommendations recommendations, StringBuilder analysisSummary,
                                          List<String> improvements, List<String> suggestions, List<String> safetyTips) {
        recommendations.setUserId(activity.getUserId());
        recommendations.setActivityId(activity.getId());
        recommendations.setActivityType(activity.getType());
        recommendations.setRecommendation(analysisSummary.toString());
        recommendations.setImprovements(improvements);
        recommendations.setSuggestions(suggestions);
        recommendations.setSafetyTips(safetyTips);
        log.info("Final Recommendations object: {}", recommendations);
        //save recommendations object to mongodb
        recommendationRepository.save(recommendations);
    }

    private List<String> extractSafetyTips(JsonNode node, String key) {
        List<String>safetyTips=new ArrayList<>();
        if(node.has(key)){
            JsonNode safetyNode=node.get(key);
            safetyNode.forEach(safetyTip->safetyTips.add(safetyTip.asText()+"\n"));
        }
        return safetyTips;
    }

    private List<String> extractImprovementsAndSuggestions(JsonNode node, String key){
        List<String>improvements=new ArrayList<>();
        if(node.has(key)){
            JsonNode keyNode=node.get(key);
            //traverse keyNode and extract area with title Area and suggestion with title Suggestion in a string builder
            StringBuilder improvement=new StringBuilder();
            for(JsonNode item:keyNode) {
                if (item.has("area") && item.has("suggestion")) {
                    improvement.append("Area: ").append(item.path("area").asText()).append("\n\n");
                    improvement.append("Suggestion: ").append(item.path("suggestion").asText()).append("\n\n");
                    improvements.add(improvement.toString());
                    improvement.setLength(0); // Clear the StringBuilder for the next iteration
                }else if(item.has("workout") && item.has("description")){
                    improvement.append("Workout: ").append(item.path("workout").asText()).append("\n\n");
                    improvement.append("Description: ").append(item.path("description").asText()).append("\n\n");
                    improvements.add(improvement.toString());
                    improvement.setLength(0); // Clear the StringBuilder for the next iteration
                }
            }
        }
        return improvements;
    }

    private StringBuilder formatAnalysis(JsonNode node, StringBuilder analysisSummary, String key, String label) {
        if(node.has(key)){
            return analysisSummary.append(label).append(node.get(key)).append("\n\n");
        }
        return analysisSummary;
    }

    private String generatePrompt(Activity activity) {
        //generate a prompt which will take data from activity and json format will be Recommendations entity
        return String.format("""
                Analyze the fitness activity and provide detailed recommendation in the following exact json format.
                {
                "analysis": {
                "overall":"overall analysis of the activity",
                "pace":"pace analysis",
                "heartRate":"heart rate analysis",
                "caloriesBurned":"calories burned analysis"
                },
                "improvements": [{
                "area":"specific area to improve",
                "suggestion":"detailed suggestion for improvement"
                }],
                "suggestions":[{
                "workout":"workout name",
                "description":"brief description of the workout",}],
                "safety":[
                "safety tip 1",
                "safety tip 2"],
                
                }
                The activity details are as follows:
                Activity Type: %s,
                Duration: %d minutes
                calories Burned: %d
                additional metrics: %s,
                
                provide detailed analysis focusing on performance, areas of improvement, and safety tips.
                Ensure the JSON is properly formatted and adheres to the specified structure.
                """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics());
    }
}
