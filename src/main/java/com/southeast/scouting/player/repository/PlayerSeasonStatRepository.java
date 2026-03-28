package com.southeast.scouting.player.repository;

import com.southeast.scouting.player.entity.PlayerSeasonStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerSeasonStatRepository extends JpaRepository<PlayerSeasonStat, Long> {
}

