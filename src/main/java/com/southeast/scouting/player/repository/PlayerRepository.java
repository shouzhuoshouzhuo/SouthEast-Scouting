package com.southeast.scouting.player.repository;

import com.southeast.scouting.player.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByWyscoutId(Long wyscoutId);

    @Query(value = """
        SELECT DISTINCT p.id, p.display_name, p.full_name,
               t.name as team_name, c.name as competition_name,
               pss.season_label, pss.primary_position,
               GREATEST(
                   similarity(p.display_name, :searchTerm),
                   similarity(p.full_name, :searchTerm)
               ) AS relevance_score,
               pss.id as stat_id
        FROM player p
        JOIN player_season_stat pss ON p.id = pss.player_id
        LEFT JOIN team t ON pss.team_id = t.id
        LEFT JOIN competition c ON pss.competition_id = c.id
        WHERE (p.display_name % :searchTerm OR p.full_name % :searchTerm)
        AND (:league IS NULL OR c.name = :league)
        AND (:season IS NULL OR pss.season_label = :season)
        AND (:position IS NULL OR pss.primary_position = :position)
        ORDER BY relevance_score DESC
        LIMIT 10
        """, nativeQuery = true)
    List<Object[]> searchPlayersFuzzy(
        @Param("searchTerm") String searchTerm,
        @Param("league") String league,
        @Param("season") String season,
        @Param("position") String position
    );
}

