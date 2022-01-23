package com.spyke.slotgame.repository;

import com.spyke.slotgame.entity.Player;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface PlayerRepository extends MongoRepository<Player,String> {

}
