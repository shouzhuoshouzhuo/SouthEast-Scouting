package org.example.southeastscouting.player.repository;

import org.example.southeastscouting.player.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByWyscoutId(Long wyscoutId);
}

