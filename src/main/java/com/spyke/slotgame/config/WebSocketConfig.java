package com.spyke.slotgame.config;

import com.spyke.slotgame.handler.TheftHandler;
import com.spyke.slotgame.handler.AuthHandler;
import com.spyke.slotgame.handler.SpinHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final TheftHandler theftHandler;
    private final AuthHandler authHandler;
    private final SpinHandler spinHandler;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(authHandler, "/auth");
        registry.addHandler(spinHandler, "/spin");
        registry.addHandler(theftHandler, "/theft");

    }

}