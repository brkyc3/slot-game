package com.spyke.slotgame.service;

import com.spyke.slotgame.entity.Player;
import com.spyke.slotgame.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
@Slf4j
public class InitDataService {
    private final PlayerRepository playerRepository;

    @PostConstruct
    private void saveInitialTestData(){
        Player testPlayer1 = Player.builder()
                .id("61ec0a9e1165241fe2e61fc9")
                .coinAmount(1000)
                .spinAmount(20)
                .build();
        Player testPlayer2 = Player.builder()
                .id("61ec646c1165241fe2e61fd4")
                .coinAmount(0)
                .spinAmount(10)
                .build();
        Player testPlayer3 = Player.builder()
                .id("61ec9e121165241fe2e61ff2")
                .coinAmount(30000)
                .spinAmount(0)
                .build();
        playerRepository.save(testPlayer1);
        playerRepository.save(testPlayer2);
        playerRepository.save(testPlayer3);
    }
}
