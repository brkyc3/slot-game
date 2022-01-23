package com.spyke.slotgame.message.response;

import com.spyke.slotgame.enums.SpinResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpinResponse {
    private boolean isSuccess;
    private Integer spinAmount;
    private Long coinAmount;
    private Long price;
    private SpinResult spinResult;
}
