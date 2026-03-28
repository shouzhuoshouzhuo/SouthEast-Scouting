package com.southeast.scouting.player.dto;

import java.util.List;

public class PlayerCompareResponseDTO {
    private List<PlayerCompareItemDTO> players;

    public PlayerCompareResponseDTO(List<PlayerCompareItemDTO> players) {
        this.players = players;
    }

    public List<PlayerCompareItemDTO> getPlayers() { return players; }
    public void setPlayers(List<PlayerCompareItemDTO> players) { this.players = players; }
}
