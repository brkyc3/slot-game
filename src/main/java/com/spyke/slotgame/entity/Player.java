package com.spyke.slotgame.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Player")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Player {
    @Id
    private String id;
    private int spinAmount;
    private long coinAmount;
}
