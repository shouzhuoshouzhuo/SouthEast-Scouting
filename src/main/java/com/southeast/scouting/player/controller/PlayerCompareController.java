package com.southeast.scouting.player.controller;

import com.southeast.scouting.player.dto.PlayerCompareResponseDTO;
import com.southeast.scouting.player.service.PlayerCompareService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerCompareController {

    private final PlayerCompareService compareService;

    public PlayerCompareController(PlayerCompareService compareService) {
        this.compareService = compareService;
    }

    @GetMapping("/compare")
    public PlayerCompareResponseDTO compare(@RequestParam("ids") String ids) {
        List<Long> statIds = Arrays.stream(ids.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(Long::parseLong)
            .toList();
        return compareService.compare(statIds);
    }
}
