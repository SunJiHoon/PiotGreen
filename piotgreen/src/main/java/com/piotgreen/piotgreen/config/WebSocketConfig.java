package com.piotgreen.piotgreen.config;

import com.piotgreen.piotgreen.handler.VideoStreamHandlerJpeg;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
@AllArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer  {
    private final VideoStreamHandlerJpeg videoStreamHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket")
                .setAllowedOriginPatterns("http://localhost:8080", "http://192.168.59.*", "http://main.putiez.com") // 패턴 매칭 허용
                .withSockJS();
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 일반 WebSocket 핸들러 추가
        registry.addHandler(videoStreamHandler, "/video-stream")
//                .setAllowedOriginPatterns("*"); // 모든 출처 허용
                .setAllowedOriginPatterns("http://localhost:8080", "http://192.168.59.*", "http://main.putiez.com") // 패턴 매칭 허용
//                .withSockJS();
        ;
    }
}
