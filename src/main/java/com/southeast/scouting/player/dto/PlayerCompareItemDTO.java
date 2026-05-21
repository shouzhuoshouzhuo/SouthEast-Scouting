package com.southeast.scouting.player.dto;

import java.util.List;

public class PlayerCompareItemDTO {
    private Long playerId;
    private String playerName;
    private String teamName;
    private String league;
    private String seasonLabel;
    private String primaryPosition;
    private String positionGroup;
    private List<MetricDTO> metrics;

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getLeague() { return league; }
    public void setLeague(String league) { this.league = league; }
    public String getSeasonLabel() { return seasonLabel; }
    public void setSeasonLabel(String seasonLabel) { this.seasonLabel = seasonLabel; }
    public String getPrimaryPosition() { return primaryPosition; }
    public void setPrimaryPosition(String primaryPosition) { this.primaryPosition = primaryPosition; }
    public String getPositionGroup() { return positionGroup; }
    public void setPositionGroup(String positionGroup) { this.positionGroup = positionGroup; }
    public List<MetricDTO> getMetrics() { return metrics; }
    public void setMetrics(List<MetricDTO> metrics) { this.metrics = metrics; }
}
