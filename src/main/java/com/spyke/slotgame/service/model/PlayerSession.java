package com.spyke.slotgame.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerSession {
    private String playerId;
    private WebSocketSession session;
}
