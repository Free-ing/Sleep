package freeing.sleep_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import freeing.sleep_service.repository.SleepTimeRecordEntity;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AIService {
    private final ChatClient chatClient;
    private final WebClient webClient;

    @Value("${deepl.api-key}")
    private String deeplApiKey;

    public AIService(ChatClient chatClient, WebClient.Builder webClientBuilder, @Value("${deepl.url}") String deeplUrl) {
        this.chatClient = chatClient;
        this.webClient = webClientBuilder.baseUrl(deeplUrl).build();
    }

    public String generateFeedback(LocalTime avgSleepTime, LocalTime avgWakeUpTime, Long avgSleepDurationInMinutes, List<SleepTimeRecordEntity> sleepRecords) {
        String formattedRecords = sleepRecords.stream()
                .map(record -> String.format("Record Date: %s, Sleep Time: %s, Wake-Up Time: %s, Duration: %d min, Status: %s",
                        record.getRecordDay(), record.getSleepTime(), record.getWakeUpTime(),
                        record.getSleepDurationInMinutes(), record.getSleepStatus()))
                .collect(Collectors.joining("; "));

        String prompt = String.format(
                "You are '후링이,' a friendly AI designed to help users improve their sleep quality. Based on the following sleep data, provide warm, supportive, and insightful feedback. Please avoid using symbols like '*' and '#'. For emphasis, feel free to use emojis or adjust the tone instead.\n\n"
                        + "Data Summary:\n"
                        + "- Average sleep duration: %d minutes\n"
                        + "- Average sleep time: %s\n"
                        + "- Average wake-up time: %s\n\n"
                        + "Detailed Records:\n%s\n\n"
                        + "Please highlight positive aspects of the user's sleep habits, suggest specific areas for improvement, and offer encouraging closing remarks."
                , avgSleepDurationInMinutes, avgSleepTime, avgWakeUpTime, formattedRecords);



        String englishFeedback = chatClient.prompt(prompt).call().content();
        englishFeedback = englishFeedback.replace("*", "").replace("#", "");

        String translatedFeedback = translateToKorean(englishFeedback);
        translatedFeedback = translatedFeedback.replace("*", "").replace("#", "");

        String returnFeedback =  refineKoreanFeedback(translatedFeedback);
        returnFeedback = returnFeedback.replace("*", "").replace("#", "");
        return returnFeedback;

    }


    private String refineKoreanFeedback(String translatedFeedback) {
        String refinementPrompt = String.format(
                "Please review the following Korean feedback carefully and adjust it to make the language sound natural, warm, and friendly, as if speaking to a friend. Please avoid using symbols like '*' and '#'. For emphasis, feel free to use emojis or adjust the tone instead. Korean feedback: \"%s\"", translatedFeedback);

        return chatClient.prompt(refinementPrompt).call().content();
    }


    private String translateToKorean(String text) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("auth_key", deeplApiKey)
                        .queryParam("text", text)
                        .queryParam("target_lang", "KO")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseTranslation)
                .block();
    }

    private String parseTranslation(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            JsonNode textNode = root.path("translations").get(0).path("text");
            return textNode.asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}