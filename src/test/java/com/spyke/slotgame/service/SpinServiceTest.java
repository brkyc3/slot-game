package com.spyke.slotgame.service;

import com.spyke.slotgame.SlotGameApplication;
import com.spyke.slotgame.config.Constant;
import com.spyke.slotgame.entity.Player;
import com.spyke.slotgame.enums.SpinResult;
import com.spyke.slotgame.message.request.AuthRequest;
import com.spyke.slotgame.message.request.SpinRequest;
import com.spyke.slotgame.message.response.AuthResponse;
import com.spyke.slotgame.message.response.SpinResponse;
import com.spyke.slotgame.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;


@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SlotGameApplication.class)
@Slf4j
class SpinServiceTest {

    @InjectMocks
    private SpinService spinService;
    @Autowired
    private SpinService spinServiceWithoutMocks;
    @Mock
    private PlayerRepository playerRepositoryMock;
    @Autowired
    private PlayerRepository playerRepository;
    @Mock
    private TheftService mockTheftService;
    @Mock
    private MongoTemplate mongoTemplate;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void whenNonExistingPlayerIdIsProvided_thenReturnWithSucceedFieldFalse() {
        SpinResponse spinResponse = spinService.spin(new SpinRequest("randomPlayerId"));
        Assertions.assertFalse(spinResponse.isSuccess());
    }

    @Test
    public void whenPlayerHasNotEnoughSpinAmount_thenReturnWithSucceedFieldFalse() {
        Mockito.doReturn(Optional.of(getZeroSpinAmountPlayer())).when(playerRepositoryMock).findById(Mockito.anyString());
        SpinResponse spinResponse = spinService.spin(new SpinRequest("xxx"));
        Assertions.assertFalse(spinResponse.isSuccess());
    }


    @Test
    public void whenValidRequest_thenCoinAndSpinAmountsShouldCalculatedCorrectly() {
        Player player = getNormalPlayer();
        try {
            Stream.of(SpinResult.values()).forEach(e->assertSpinAndCoinAmounts(player,e));
        }catch (Exception e){
            Assertions.fail("unexpected exception",e);
        }finally {
            playerRepository.delete(player);
        }

    }

    @Test
    public void whenValidRequest_ThenTotalCountShouldMatchWithPercent() {
        Player player = getNormalPlayer();
        try {
            HashMap<SpinResult, Integer> spinResultCountMap = new HashMap<>();

            spinServiceWithoutMocks.setTheftService(mockTheftService);
            Mockito.when(mockTheftService.stealFromRandomPlayer(Mockito.anyString()))
                    .thenReturn(ThreadLocalRandom.current().nextLong(0, 2000));

            for(int i = 0;i<100;i++) {
                SpinResponse spinResponse = spinServiceWithoutMocks.spin(new SpinRequest(player.getId()));
                Assertions.assertTrue(spinResponse.isSuccess(), "Spin response should be true");
                spinResultCountMap.put(spinResponse.getSpinResult(),spinResultCountMap.getOrDefault(spinResponse.getSpinResult(),0)+1);
            }

           for(var entry : Constant.SPIN_RESULT_PERCENTS.entrySet()){
               Assertions.assertEquals(
                       spinResultCountMap.get(entry.getKey()),
                       entry.getValue(),
                       String.format("Spin response count not valid for %s calculated count %d actual count should be %d",
                               entry.getKey(),
                               spinResultCountMap.get(entry.getKey()),
                               entry.getValue())
               );

           }

        }catch (Exception e){
            Assertions.fail("Unexpected exception",e);
        }finally {
            playerRepository.delete(player);
        }

    }



    private void assertSpinAndCoinAmounts(Player player, SpinResult spinType) {
        SpinService spy = Mockito.spy(spinServiceWithoutMocks);
        Mockito.doReturn(spinType).when(spy).getNextRandomSpinResult();
        spinServiceWithoutMocks.setTheftService(mockTheftService);

        final long mockTheftAmount = ThreadLocalRandom.current().nextInt(0, 2000);
        Mockito.when(mockTheftService.stealFromRandomPlayer(Mockito.anyString())).thenReturn(mockTheftAmount);
        SpinResponse spinResponse = spy.spin(new SpinRequest(player.getId()));


        Assertions.assertTrue(spinResponse.isSuccess(),
                "Spin response should be true");
        Assertions.assertEquals(
                (spinType != SpinResult.THIEF_THIEF_THIEF ? spinType.getPrice() : mockTheftAmount)  + player.getCoinAmount(),
                spinResponse.getCoinAmount(),
                "Spin response spin amount not valid "
        );
        Assertions.assertEquals(
                spinResponse.getSpinAmount(),
                player.getSpinAmount() - 1,
                "Sping response spin amount not valid"
        );
        player.setCoinAmount(spinResponse.getCoinAmount());
        player.setSpinAmount(spinResponse.getSpinAmount());
    }

    private Player getNormalPlayer() {
        Player player = Player.builder()
                .coinAmount(ThreadLocalRandom.current().nextInt(100, 2000))
                .spinAmount(ThreadLocalRandom.current().nextInt(100, 200))
                .build();
        return playerRepository.save(player);
    }

    private Player getZeroSpinAmountPlayer(){
        return Player.builder()
                .id("randim")
                .spinAmount(0)
                .coinAmount(ThreadLocalRandom.current().nextInt(100, 2000))
                .build();
    }


}