//package com.piotgreen.piotgreen.config;
//
//import com.piotgreen.piotgreen.handler.VideoStreamHandler;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//
//@Configuration
//@EnableWebSocket
//public class VideoSocket implements WebSocketConfigurer {
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(new VideoStreamHandler(), "/video-stream").setAllowedOrigins("*");
//    }
//}
