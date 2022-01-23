package com.spyke.slotgame.service;

import com.spyke.slotgame.config.Constant;
import com.spyke.slotgame.entity.Player;
import com.spyke.slotgame.enums.SpinResult;
import com.spyke.slotgame.message.request.SpinRequest;
import com.spyke.slotgame.message.response.SpinResponse;
import com.spyke.slotgame.repository.PlayerRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class SpinService {
    private final PlayerRepository playerRepository;
    private final ArrayList<SpinResult> gameTable = new ArrayList<>();
    private final MongoTemplate mongoTemplate;
    private int spinCounter = 0;
    @Autowired
    private TheftService theftService;

    @PostConstruct
    public void initializeGameTable() {

        for (var entry : Constant.SPIN_RESULT_PERCENTS.entrySet()) {
            for (int z = 0; z < entry.getValue(); z++) {
                gameTable.add(entry.getKey());
            }
        }
        shuffleGameTable();
    }

    public SpinResponse spin(SpinRequest request) {
        try {
            Player player = playerRepository.findById(request.getPlayerId()).orElseThrow();
            if (player.getSpinAmount() <= 0)
                throw new RuntimeException("Player has not enough spin amount");

            SpinResult randomSpinResult = getNextRandomSpinResult();
            log.info("Spin result , {}", randomSpinResult);

            Long earnedPrice = calculateEarnedPrice(player.getId(), randomSpinResult);

            Player updatedPlayer = updatePlayerCoinAndSpinInfo(player, earnedPrice);

            return SpinResponse.builder()
                    .isSuccess(true)
                    .spinAmount(updatedPlayer.getSpinAmount())
                    .coinAmount(updatedPlayer.getCoinAmount())
                    .price(earnedPrice)
                    .spinResult(randomSpinResult)
                    .build();

        } catch (Exception e) {
            log.error("Error occurred for spin request , player {}", request.getPlayerId(), e);
            return SpinResponse.builder()
                    .isSuccess(false)
                    .build();
        }

    }

    @Synchronized
    public SpinResult getNextRandomSpinResult() {
        int counter = spinCounter++ % 100;
        if (counter == 0) {
            shuffleGameTable();
        }
        return gameTable.get(counter);
    }


    public Long calculateEarnedPrice(String requestingPlayerId, SpinResult randomSpinResult) {
        if (!SpinResult.THIEF_THIEF_THIEF.equals(randomSpinResult)) {
            return randomSpinResult.getPrice();
        } else {
            return theftService.stealFromRandomPlayer(requestingPlayerId);
        }
    }


    private Player updatePlayerCoinAndSpinInfo(Player player, Long earnedCoins) {
        Update update = new Update();
        update.inc("coinAmount", earnedCoins);
        update.inc("spinAmount", -1);
        return mongoTemplate.findAndModify(Query.query(Criteria.where("id").is(player.getId())), update, FindAndModifyOptions.options().returnNew(true), Player.class);
    }

    private Integer incrementCircularCounter(Integer currentValue) {
        if (++currentValue == 100) {
            return 0;
        } else {
            return currentValue;
        }
    }

    private void shuffleGameTable() {
        Collections.shuffle(gameTable);
        log.info("Game table shuffled");
    }
}
