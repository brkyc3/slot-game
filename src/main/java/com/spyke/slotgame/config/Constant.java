package com.spyke.slotgame.config;


import com.spyke.slotgame.enums.SpinResult;

import java.util.Map;

import static java.util.Map.*;

public class Constant {
    public static final String PLAYER_ID_HEADER = "X_PLAYER_ID";

    public static final Map<SpinResult, Integer> SPIN_RESULT_PERCENTS = Map.ofEntries(
            entry(SpinResult.A_WILD_BONUS,12),
            entry(SpinResult.WILD_WILD_SEVEN,12),
            entry(SpinResult.JACKPOT_JACKPOT_A,12),
            entry(SpinResult.WILD_BONUS_A,12),
            entry(SpinResult.BONUS_A_JACKPOT,12),
            entry(SpinResult.A_A_A,9),
            entry(SpinResult.BONUS_BONUS_BONUS,8),
            entry(SpinResult.SEVEN_SEVEN_SEVEN,7),
            entry(SpinResult.WILD_WILD_WILD,6),
            entry(SpinResult.JACKPOT_JACKPOT_JACKPOT,5),
            entry(SpinResult.THIEF_THIEF_THIEF,5)
    );


    public static final long LOCK_MAX_WAIT_TIME_IN_SECONDS = 3;
}
