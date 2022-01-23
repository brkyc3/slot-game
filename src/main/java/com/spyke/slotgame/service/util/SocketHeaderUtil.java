package com.spyke.slotgame.service.util;

import com.spyke.slotgame.config.Constant;
import org.springframework.web.socket.WebSocketSession;

public class SocketHeaderUtil {
    public static String getPlayerId(WebSocketSession session) {
        return session.getHandshakeHeaders().get(Constant.PLAYER_ID_HEADER).get(0);
    }
}
