package com.spyke.slotgame.message.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TheftNotificationResponse {
    private long currentCoinAmount;
}
