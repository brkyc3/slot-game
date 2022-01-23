package com.spyke.slotgame.service;

import com.spyke.slotgame.SlotGameApplication;
import com.spyke.slotgame.entity.Player;
import com.spyke.slotgame.message.request.AuthRequest;
import com.spyke.slotgame.message.response.AuthResponse;
import com.spyke.slotgame.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.NoSuchElementException;
import java.util.Optional;


@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SlotGameApplication.class)
@Slf4j
class AuthServiceTest {


    @InjectMocks
    private AuthService authService;
    @Mock
    private PlayerRepository playerRepository;


    @Test
    public void whenExistingPlayerIdIsProvided_thenSuccessfullyReturn() {
        AuthResponse authResponse = null;
        final String testPlayerId = "testid";
        Mockito.when(playerRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(new Player(testPlayerId, 111, 2222)));
        try {
            authResponse = authService.authenticate(new AuthRequest(testPlayerId));
        } catch (Exception e) {
            log.error("error for authentiction test", e);
        }

        Assertions.assertEquals(authResponse.getPlayerId(), testPlayerId);
    }

    @Test
    public void whenNonExistingPlayerIdIsProvided_thenThrow() {
        Assertions.assertThrows(NoSuchElementException.class, () -> {
            authService.authenticate(new AuthRequest("random id"));
        });

    }

}