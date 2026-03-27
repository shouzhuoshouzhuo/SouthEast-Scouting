package org.example.southeastscouting.player.repository;

import org.example.southeastscouting.player.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByNormalizedName(String normalizedName);
}

