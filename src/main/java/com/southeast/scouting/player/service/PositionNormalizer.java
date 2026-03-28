package com.southeast.scouting.player.service;

import java.util.Set;

public class PositionNormalizer {
    private static final Set<String> GK    = Set.of("GK");
    private static final Set<String> LB_RB = Set.of("LB", "RB", "LWB", "RWB", "LB5", "RB5");
    private static final Set<String> CB    = Set.of("CB", "LCB", "RCB", "LCB3", "RCB3");
    private static final Set<String> DMF   = Set.of("DMF", "LDMF", "RDMF");
    private static final Set<String> FW    = Set.of("CF", "LW", "RW", "LWF", "RWF");

    public static String normalize(String rawPosition) {
        if (rawPosition == null || rawPosition.isBlank()) return "AMF";
        String firstToken = rawPosition.split(",")[0].trim();
        if (GK.contains(firstToken))    return "GK";
        if (LB_RB.contains(firstToken)) return "LB_RB";
        if (CB.contains(firstToken))    return "CB";
        if (DMF.contains(firstToken))   return "DMF";
        if (FW.contains(firstToken))    return "FW";
        return "AMF";
    }
}
