package com.spyke.slotgame.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.Striped;
import com.spyke.slotgame.config.Constant;
import com.spyke.slotgame.entity.Player;
import com.spyke.slotgame.message.notification.TheftNotificationResponse;
import com.spyke.slotgame.repository.PlayerRepository;
import com.spyke.slotgame.service.util.SocketHeaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TheftService {

    private final ConcurrentHashMap<String, WebSocketSession> webSocketSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final AuthService authService;
    private final PlayerRepository playerRepository;
    private final Striped<Lock> stripedLocks = Striped.lazyWeakLock(100);

    public void addSession(WebSocketSession session) {
        webSocketSessions.put(SocketHeaderUtil.getPlayerId(session), session);
    }

    public void removeSession(WebSocketSession session) {
        webSocketSessions.remove(SocketHeaderUtil.getPlayerId(session));

    }

    private void notifyPlayer(String playerId, Long currentCoinAmount) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(new TheftNotificationResponse(currentCoinAmount));
            TextMessage socketTextMessage = new TextMessage(jsonMessage);
            webSocketSessions.get(playerId).sendMessage(socketTextMessage);
        } catch (Exception exception) {
            log.error("io exception occurred while sending notification to player {}", playerId, exception);
        }
    }

    public Long stealFromRandomPlayer(String requestingPlayer) {
        Lock lock = null;
        boolean locked = false;
        try {
            Player selectedPlayer = selectRandomOnlinePlayer(requestingPlayer);
            log.info("Selected player for stealing is {}", selectedPlayer.getId());
            lock = stripedLocks.get(selectedPlayer.getId());
            locked = lock.tryLock(Constant.LOCK_MAX_WAIT_TIME_IN_SECONDS, TimeUnit.SECONDS);
            if (locked) {
                long halfOfPlayersCoins = (long) Math.ceil(selectedPlayer.getCoinAmount() / 2.0);
                long otherHalfOfPlayersCoins = (long) Math.floor(selectedPlayer.getCoinAmount() / 2.0);
                log.info("Stealing {} from player {}", halfOfPlayersCoins,selectedPlayer.getId());
                selectedPlayer.setCoinAmount(halfOfPlayersCoins);
                Player savedPlayer = playerRepository.save(selectedPlayer);
                notifyPlayer(savedPlayer.getId(), savedPlayer.getCoinAmount());
                lock.unlock();
                locked = false;
                return otherHalfOfPlayersCoins;
            } else {
                throw new RuntimeException(String.format("Stealing lock timed out for user %s", selectedPlayer.getId()));
            }

        } catch (Exception e) {
            log.error("Error occured while stealing from player", e);
            return 0L;
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    private Player selectRandomOnlinePlayer(String excludedPlayer) {
        List<Map.Entry<String, WebSocketSession>> onlinePlayersSessions = authService.getAllOnlinePlayersSessions();

        onlinePlayersSessions = onlinePlayersSessions.stream()
                .filter(e -> !e.getKey().equals(excludedPlayer))
                .collect(Collectors.toList());

        if (onlinePlayersSessions.isEmpty())
            throw new RuntimeException("Cannot find any online player");

        Map.Entry<String, WebSocketSession> selectedPlayer = onlinePlayersSessions.get(ThreadLocalRandom.current().nextInt(0, onlinePlayersSessions.size()));

        return playerRepository.findById(selectedPlayer.getKey()).orElseThrow();
    }


}
