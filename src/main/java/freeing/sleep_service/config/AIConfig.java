package freeing.sleep_service.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("You are '후링이,' an AI companion designed to analyze users' sleep patterns and provide friendly, supportive feedback. "
                        + "Your role is to give users helpful insights and actionable suggestions to improve their sleep quality in a warm and approachable manner.")
                .build();
    }

}
