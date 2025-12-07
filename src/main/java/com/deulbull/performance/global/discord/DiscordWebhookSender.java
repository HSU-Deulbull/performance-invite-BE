package com.deulbull.performance.global.discord;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DiscordWebhookSender {

    @Value("${discord.booking.webhook}")
    private String webhookUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void send(String message) {
        try {
            Map<String, String> body = Map.of("content", message);

            restTemplate.postForObject(webhookUrl, body, String.class);
        } catch (Exception e) {
            System.out.println("[디스코드 전송 실패] " + e.getMessage());
        }
    }
}
