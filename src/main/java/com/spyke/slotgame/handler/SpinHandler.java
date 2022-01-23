package com.spyke.slotgame.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spyke.slotgame.message.request.SpinRequest;
import com.spyke.slotgame.message.response.SpinResponse;
import com.spyke.slotgame.service.SpinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class SpinHandler extends TextWebSocketHandler {

    private final SpinService spinService;
    private final ObjectMapper objectMapper;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
        SpinRequest spinRequest = objectMapper.readValue(message.getPayload(), SpinRequest.class);
        SpinResponse spinResponse = spinService.spin(spinRequest);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(spinResponse)));


    }


}