package com.example.tiertagger.commands;

import com.example.tiertagger.ConfigManager;
import com.example.tiertagger.TierManager;
import com.example.tiertagger.TierTaggerMod;
import com.example.tiertagger.util.TeamUtil;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TierCommands {
    public static void register(com.mojang.brigadier.CommandDispatcher<ServerCommandSource> dispatcher, TierManager manager, ConfigManager configManager) {
        dispatcher.register(
            literal("tiertagger")
            .then(literal("addmode")
                .then(argument("player", StringArgumentType.word())
                    .then(argument("gamemode", StringArgumentType.word())
                        .then(argument("tier", StringArgumentType.word())
                            .executes(ctx -> {
                                ServerCommandSource src = ctx.getSource();
                                if (!isAuthorized(src, configManager)) {
                                    src.sendError(Text.of("You are not authorized to modify tiers."));
                                    return 0;
                                }
                                String playerName = StringArgumentType.getString(ctx, "player");
                                String gamemode = StringArgumentType.getString(ctx, "gamemode");
                                String tier = StringArgumentType.getString(ctx, "tier");

                                manager.setPlayerGamemodeTier(playerName, gamemode, tier);

                                ServerPlayerEntity target = src.getServer().getPlayerManager().getPlayer(playerName);
                                if (target != null) {
                                    TeamUtil.applyPrefixToPlayer(src.getServer(), target, tier);
                                }

                                src.sendFeedback(new LiteralText("Set " + playerName + " -> " + gamemode + " = " + tier), false);
                                return 1;
                            })
                        )
                    )
                )
            )
            .then(literal("set-owner")
                .then(argument("player", StringArgumentType.word())
                    .requires(src -> src.hasPermissionLevel(2))
                    .executes(ctx -> {
                        ServerCommandSource src = ctx.getSource();
                        String name = StringArgumentType.getString(ctx, "player");
                        ServerPlayerEntity target = src.getServer().getPlayerManager().getPlayer(name);
                        if (target == null) {
                            src.sendError(Text.of("Player not online to set owner."));
                            return 0;
                        }
                        UUID uuid = target.getUuid();
                        configManager.setOwnerUuid(uuid);
                        src.sendFeedback(Text.of("Set Tier GUI owner to " + target.getEntityName()), false);
                        return 1;
                    })
                )
            )
            .then(literal("set-verifier")
                .then(argument("username", StringArgumentType.word())
                    .then(argument("code", StringArgumentType.word())
                        .requires(src -> src.hasPermissionLevel(2))
                        .executes(ctx -> {
                            String u = StringArgumentType.getString(ctx, "username");
                            String c = StringArgumentType.getString(ctx, "code");
                            configManager.setVerifier(u, c);
                            ctx.getSource().sendFeedback(Text.of("Verifier set."), false);
                            return 1;
                        })
                    )
                )
            )
            .then(literal("list")
                .requires(src -> src.hasPermissionLevel(2))
                .executes(ctx -> {
                    manager.getAll().forEach((k, v) -> {
                        ctx.getSource().sendFeedback(Text.of(k + " -> " + v.gamemodeToTier.toString()), false);
                    });
                    return 1;
                })
            )
            .then(literal("clear-all")
                .then(literal("confirm").executes(ctx -> {
                    ServerCommandSource src = ctx.getSource();
                    if (!isAuthorized(src, configManager)) { src.sendError(Text.of("Not authorized.")); return 0; }
                    manager.backup();
                    manager.clearAll();
                    src.sendFeedback(Text.of("Cleared all tiers (backup saved)."), false);
                    return 1;
                }))
            )
        );
    }

    private static boolean isAuthorized(ServerCommandSource src, ConfigManager cfg) {
        UUID owner = cfg.getOwnerUuid();
        if (owner == null) {
            return src.hasPermissionLevel(2);
        }
        try {
            ServerPlayerEntity player = src.getPlayer();
            if (player.getUuid().equals(owner)) return true;
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException ignored) {
            if (src.hasPermissionLevel(4)) return true;
        }
        return src.hasPermissionLevel(4);
    }
}
