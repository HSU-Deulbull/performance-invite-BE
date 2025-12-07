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
    private String bookingWebhook;

    @Value("${discord.log.webhook}")
    private String logWebhook;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendBooking(String message) {
        sendToWebhook(bookingWebhook, message);
    }

    public void sendLog(String message) {
        sendToWebhook(logWebhook, message);
    }

    private void sendToWebhook(String webhookUrl, String message) {
        try {
            Map<String, String> body = Map.of("content", message);
            restTemplate.postForObject(webhookUrl, body, String.class);
        } catch (Exception e) {
            System.out.println("[디스코드 전송 실패] url=" + webhookUrl + ", error=" + e.getMessage());
        }
    }
}

