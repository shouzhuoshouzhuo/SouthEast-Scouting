package com.southeast.scouting.player.dto;

public record PlayerSearchResultDTO(
    Long playerId,
    String playerName,
    String teamName,
    String league,
    String season,
    String position,
    Long statId
) {}
