package org.example.southeastscouting.player.repository;

import org.example.southeastscouting.player.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    Optional<Competition> findByNameAndSeasonLabel(String name, String seasonLabel);
}

