package com.spyke.slotgame.service;

import com.spyke.slotgame.entity.Player;
import com.spyke.slotgame.message.request.AuthRequest;
import com.spyke.slotgame.message.response.AuthResponse;
import com.spyke.slotgame.repository.PlayerRepository;
import com.spyke.slotgame.service.util.SocketHeaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ConcurrentHashMap<String, WebSocketSession> webSocketSessions = new ConcurrentHashMap<>();

    private final PlayerRepository playerRepository;

    public AuthResponse authenticate(AuthRequest request) {
        Player player = playerRepository.findById(request.getPlayerId()).orElseThrow();
        return AuthResponse.builder()
                .coinAmount(player.getCoinAmount())
                .spinAmount(player.getSpinAmount())
                .playerId(player.getId())
                .build();

    }

    public void addSession(WebSocketSession session) {
        webSocketSessions.put(SocketHeaderUtil.getPlayerId(session), session);
    }

    public void removeSession(WebSocketSession session) {
        webSocketSessions.remove(SocketHeaderUtil.getPlayerId(session));

    }

    public List<Map.Entry<String, WebSocketSession>> getAllOnlinePlayersSessions() {
        return new ArrayList<>(webSocketSessions.entrySet());
    }
}
