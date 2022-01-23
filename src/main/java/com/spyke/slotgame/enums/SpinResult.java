package com.spyke.slotgame.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SpinResult {
    A_WILD_BONUS(0L),
    WILD_WILD_SEVEN(0L),
    JACKPOT_JACKPOT_A(0L),
    WILD_BONUS_A(0L),
    BONUS_A_JACKPOT(0L),
    A_A_A(5000L),
    BONUS_BONUS_BONUS(10000L),
    SEVEN_SEVEN_SEVEN(20000L),
    WILD_WILD_WILD(50000L),
    JACKPOT_JACKPOT_JACKPOT(100000L),
    THIEF_THIEF_THIEF(-1L);

    private final Long price;

}
