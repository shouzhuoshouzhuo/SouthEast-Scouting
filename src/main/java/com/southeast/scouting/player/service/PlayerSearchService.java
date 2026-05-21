package com.southeast.scouting.player.service;

import com.southeast.scouting.player.dto.PlayerSearchResultDTO;
import com.southeast.scouting.player.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerSearchService {

    private final PlayerRepository playerRepository;

    public PlayerSearchService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<PlayerSearchResultDTO> searchPlayers(
            String query, String league, String season, String position) {

        List<Object[]> results = playerRepository.searchPlayersFuzzy(
            query, league, season, position);

        return results.stream()
            .map(row -> new PlayerSearchResultDTO(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[3],
                (String) row[4],
                (String) row[5],
                (String) row[6],
                row[8] != null ? ((Number) row[8]).longValue() : null
            ))
            .toList();
    }
}
