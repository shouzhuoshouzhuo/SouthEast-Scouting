package com.southeast.scouting.player.controller;

import com.southeast.scouting.player.dto.PlayerSearchResultDTO;
import com.southeast.scouting.player.service.PlayerSearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerSearchController {

    private final PlayerSearchService searchService;

    public PlayerSearchController(PlayerSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public List<PlayerSearchResultDTO> searchPlayers(
            @RequestParam String q,
            @RequestParam(required = false) String league,
            @RequestParam(required = false) String season,
            @RequestParam(required = false) String position) {

        return searchService.searchPlayers(q, league, season, position);
    }
}
