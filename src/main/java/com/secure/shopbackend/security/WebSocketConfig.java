package com.secure.shopbackend.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer { // ✅ WebSocketConfigurer 사용

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(new ChatWebSocketHandler(), "/ws-chat")
            .setAllowedOrigins("*");
  }
}
