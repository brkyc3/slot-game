package com.spyke.slotgame.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spyke.slotgame.message.request.AuthRequest;
import com.spyke.slotgame.message.response.AuthResponse;
import com.spyke.slotgame.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class AuthHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final AuthService authService;

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        authService.addSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        authService.removeSession(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
        AuthRequest authRequest = objectMapper.readValue(message.getPayload(), AuthRequest.class);
        AuthResponse authenticateResponse = authService.authenticate(authRequest);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(authenticateResponse)));

    }


}