package com.southeast.scouting.player.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "player_season_stat")
public class PlayerSeasonStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @Column(name = "season_label", nullable = false, length = 32)
    private String seasonLabel;

    @Column(name = "primary_position", length = 64)
    private String primaryPosition;

    @Column(name = "secondary_position", length = 64)
    private String secondaryPosition;

    @Column(name = "third_position", length = 64)
    private String thirdPosition;

    @Column(name = "matches_played")
    private Integer matchesPlayed;

    @Column(name = "minutes_played")
    private Integer minutesPlayed;

    @Column
    private BigDecimal goals;

    @Column
    private BigDecimal assists;

    @Column(name = "xg")
    private BigDecimal expectedGoals;

    @Column(name = "xa")
    private BigDecimal expectedAssists;

    @Column(name = "on_loan")
    private Boolean onLoan;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_metrics", columnDefinition = "jsonb", nullable = false)
    private String rawMetrics = "{}";

    @Column(name = "source_file")
    private String sourceFile;

    @Column(name = "source_row_num")
    private Integer sourceRowNum;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public String getSeasonLabel() {
        return seasonLabel;
    }

    public void setSeasonLabel(String seasonLabel) {
        this.seasonLabel = seasonLabel;
    }

    public String getPrimaryPosition() {
        return primaryPosition;
    }

    public void setPrimaryPosition(String primaryPosition) {
        this.primaryPosition = primaryPosition;
    }

    public String getSecondaryPosition() {
        return secondaryPosition;
    }

    public void setSecondaryPosition(String secondaryPosition) {
        this.secondaryPosition = secondaryPosition;
    }

    public String getThirdPosition() {
        return thirdPosition;
    }

    public void setThirdPosition(String thirdPosition) {
        this.thirdPosition = thirdPosition;
    }

    public Integer getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(Integer matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public Integer getMinutesPlayed() {
        return minutesPlayed;
    }

    public void setMinutesPlayed(Integer minutesPlayed) {
        this.minutesPlayed = minutesPlayed;
    }

    public BigDecimal getGoals() {
        return goals;
    }

    public void setGoals(BigDecimal goals) {
        this.goals = goals;
    }

    public BigDecimal getAssists() {
        return assists;
    }

    public void setAssists(BigDecimal assists) {
        this.assists = assists;
    }

    public BigDecimal getExpectedGoals() {
        return expectedGoals;
    }

    public void setExpectedGoals(BigDecimal expectedGoals) {
        this.expectedGoals = expectedGoals;
    }

    public BigDecimal getExpectedAssists() {
        return expectedAssists;
    }

    public void setExpectedAssists(BigDecimal expectedAssists) {
        this.expectedAssists = expectedAssists;
    }

    public Boolean getOnLoan() {
        return onLoan;
    }

    public void setOnLoan(Boolean onLoan) {
        this.onLoan = onLoan;
    }

    public String getRawMetrics() {
        return rawMetrics;
    }

    public void setRawMetrics(String rawMetrics) {
        this.rawMetrics = rawMetrics;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public Integer getSourceRowNum() {
        return sourceRowNum;
    }

    public void setSourceRowNum(Integer sourceRowNum) {
        this.sourceRowNum = sourceRowNum;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

