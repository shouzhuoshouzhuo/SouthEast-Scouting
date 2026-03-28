package com.southeast.scouting.player.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.southeast.scouting.player.entity.Competition;
import com.southeast.scouting.player.entity.Player;
import com.southeast.scouting.player.entity.PlayerSeasonStat;
import com.southeast.scouting.player.entity.Team;
import com.southeast.scouting.player.repository.CompetitionRepository;
import com.southeast.scouting.player.repository.PlayerRepository;
import com.southeast.scouting.player.repository.PlayerSeasonStatRepository;
import com.southeast.scouting.player.repository.TeamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Component
public class SampleDataImporter implements CommandLineRunner {

    private static final String PLAYER_CSV_PATH =
            "data/五大联赛data/德甲球员/Bundesliga 22-23.csv";

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final CompetitionRepository competitionRepository;
    private final PlayerSeasonStatRepository playerSeasonStatRepository;

    public SampleDataImporter(TeamRepository teamRepository,
                              PlayerRepository playerRepository,
                              CompetitionRepository competitionRepository,
                              PlayerSeasonStatRepository playerSeasonStatRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.competitionRepository = competitionRepository;
        this.playerSeasonStatRepository = playerSeasonStatRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (Boolean.parseBoolean(System.getenv().getOrDefault("APP_SKIP_SAMPLE_IMPORT", "false"))) {
            return;
        }
        if (playerSeasonStatRepository.count() > 0) {
            return;
        }
        Path csvPath = Path.of(PLAYER_CSV_PATH);
        if (!Files.exists(csvPath)) {
            return;
        }
        importSamplePlayers(csvPath, 20);
    }

    private void importSamplePlayers(Path csvPath, int limit) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
             CSVReader csv = new CSVReader(reader)) {
            String[] header;
            try {
                header = csv.readNext();
            } catch (CsvValidationException e) {
                // 表头本身解析失败，直接放弃
                return;
            }
            if (header == null || header.length == 0) {
                return;
            }
            // 宽松兼容：有些 CSV 表头可能带 BOM（\uFEFF），需要先清理，否则 row.get("Player") 取不到
            for (int i = 0; i < header.length; i++) {
                if (header[i] != null) {
                    header[i] = header[i].replace("\uFEFF", "").trim();
                }
            }
            int headerLen = header.length;
            int imported = 0;
            int recordNum = 2; // header is row 1

            while (imported < limit) {
                String[] record;
                try {
                    record = csv.readNext();
                } catch (CsvValidationException e) {
                    // 宽松模式：某一行解析失败就跳过
                    recordNum++;
                    continue;
                }

                if (record == null) {
                    break;
                }

                // 宽松处理：字段数多则截断；少则跳过
                if (record.length < headerLen) {
                    recordNum++;
                    continue;
                }
                if (record.length > headerLen) {
                    record = Arrays.copyOf(record, headerLen);
                }

                Map<String, String> row = new HashMap<>(headerLen);
                for (int i = 0; i < headerLen; i++) {
                    row.put(header[i], record[i]);
                }
                try {
                    saveOneRow(row, recordNum);
                    imported++;
                } catch (Exception ignored) {
                    // 单行失败不影响导入整体
                }
                recordNum++;
            }
        }
    }

    private void saveOneRow(Map<String, String> row, int rowNum) {
        String playerName = row.get("Player");
        String fullName = row.get("Full name");
        Long wyscoutId = parseLong(row.get("Wyscout id"));
        String teamName = row.get("Team");
        String competitionName = row.get("Competition");
        String primaryPos = row.get("Primary position");
        String secondaryPos = row.get("Secondary position");
        String thirdPos = row.get("Third position");

        // 宽松模式：必填字段为空则跳过，避免整批回滚
        if (playerName == null || playerName.isBlank()) {
            return;
        }
        if (competitionName == null || competitionName.isBlank()) {
            return;
        }

        Team team = null;
        if (teamName != null && !teamName.isBlank()) {
            String normalizedTeamName = normalize(teamName);
            Optional<Team> existingTeam = teamRepository.findByNormalizedName(normalizedTeamName);
            team = existingTeam.orElseGet(() -> {
                Team t = new Team();
                t.setName(teamName);
                t.setNormalizedName(normalizedTeamName);
                return teamRepository.save(t);
            });
        }

        String seasonLabel = "22-23";
        Competition competition = competitionRepository
                .findByNameAndSeasonLabel(competitionName, seasonLabel)
                .orElseGet(() -> {
                    Competition c = new Competition();
                    c.setName(competitionName);
                    c.setSeasonLabel(seasonLabel);
                    return competitionRepository.save(c);
                });

        Player player = null;
        if (wyscoutId != null) {
            player = playerRepository.findByWyscoutId(wyscoutId).orElse(null);
        }
        if (player == null) {
            player = new Player();
            player.setWyscoutId(wyscoutId);
            player.setDisplayName(playerName);
            player.setFullName(fullName);
            player.setNormalizedName(normalize(fullName != null && !fullName.isBlank() ? fullName : playerName));
            player.setAge(parseShort(row.get("Age")));
            player.setBirthDate(parseDate(row.get("Birthday")));
            player.setBirthCountry(cleanSimple(row.get("Birth country")));
            player.setPassportCountryRaw(row.get("Passport country"));
            player.setFoot(cleanSimple(row.get("Foot")));
            player.setHeightCm(parseShort(row.get("Height")));
            player.setWeightKg(parseShort(row.get("Weight")));
            player.setMarketValueEur(parseLong(row.get("Market value")));
            player.setContractExpires(parseDate(row.get("Contract expires")));
            player = playerRepository.save(player);
        }

        PlayerSeasonStat stat = new PlayerSeasonStat();
        stat.setPlayer(player);
        stat.setTeam(team);
        stat.setCompetition(competition);
        stat.setSeasonLabel(seasonLabel);
        stat.setPrimaryPosition(primaryPos);
        stat.setSecondaryPosition(secondaryPos);
        stat.setThirdPosition(thirdPos);
        stat.setMatchesPlayed(parseInt(row.get("Matches played")));
        stat.setMinutesPlayed(parseInt(row.get("Minutes played")));
        stat.setGoals(parseBigDecimal(row.get("Goals")));
        stat.setExpectedGoals(parseBigDecimal(row.get("xG")));
        stat.setAssists(parseBigDecimal(row.get("Assists")));
        stat.setExpectedAssists(parseBigDecimal(row.get("xA")));
        stat.setOnLoan(parseBoolean(row.get("On loan")));
        stat.setSourceFile(PLAYER_CSV_PATH);
        stat.setSourceRowNum(rowNum);
        playerSeasonStatRepository.save(stat);
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String cleanSimple(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            return trimmed;
        }
        return trimmed;
    }

    private static Integer parseInt(String value) {
        try {
            return (value == null || value.isBlank()) ? null : Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Short parseShort(String value) {
        try {
            return (value == null || value.isBlank()) ? null : Short.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long parseLong(String value) {
        try {
            return (value == null || value.isBlank()) ? null : Long.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static BigDecimal parseBigDecimal(String value) {
        try {
            return (value == null || value.isBlank()) ? null : new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Boolean parseBoolean(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String v = value.trim().toLowerCase(Locale.ROOT);
        if ("true".equals(v)) {
            return Boolean.TRUE;
        }
        if ("false".equals(v)) {
            return Boolean.FALSE;
        }
        return null;
    }

    private static LocalDate parseDate(String value) {
        try {
            return (value == null || value.isBlank()) ? null : LocalDate.parse(value.trim());
        } catch (Exception e) {
            return null;
        }
    }
}

