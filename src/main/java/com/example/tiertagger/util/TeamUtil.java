package com.example.tiertagger.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TeamUtil {
    public static void applyPrefixToPlayer(MinecraftServer server, ServerPlayerEntity target, String tier) {
        try {
            Scoreboard scoreboard = server.getScoreboard();
            String uuidPart = target.getUuidAsString().replace("-", "");
            String teamName = "cctl_" + uuidPart.substring(0, Math.min(10, uuidPart.length()));

            Team team = scoreboard.getTeam(teamName);
            if (team == null) {
                scoreboard.addTeam(teamName);
                team = scoreboard.getTeam(teamName);
            }

            Formatting color = tier.startsWith("HT") ? Formatting.GOLD : Formatting.GRAY;
            Text prefix = Text.literal("[" + tier + "] ").styled(s -> s.withColor(color));

            team.setPrefix(prefix);

            // remove player from other cctl_ teams
            for (Team t : scoreboard.getTeams()) {
                if (t.getName().startsWith("cctl_") && !t.getName().equals(teamName)) {
                    scoreboard.removePlayerFromTeam(target.getEntityName(), t.getName());
                }
            }

            scoreboard.addPlayerToTeam(target.getEntityName(), teamName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
