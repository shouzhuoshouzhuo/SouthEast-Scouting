package org.example.southeastscouting.player.repository;

import org.example.southeastscouting.player.entity.PlayerSeasonStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerSeasonStatRepository extends JpaRepository<PlayerSeasonStat, Long> {
}

