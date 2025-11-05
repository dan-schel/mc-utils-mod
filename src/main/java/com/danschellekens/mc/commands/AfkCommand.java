package com.danschellekens.mc.commands;

import com.danschellekens.mc.AfkSystem;
import com.danschellekens.mc.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class AfkCommand {
  public static LiteralArgumentBuilder<ServerCommandSource> COMMAND = CommandManager
    .literal("afk")
    .executes(AfkCommand::execute);

  public static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
		ServerPlayerEntity player = source.getPlayer();
		
		if (player == null) {
			return CommandUtils.failure(source, "Not executed by a player.");
		}

		AfkSystem.getInstance().manuallyDeclareAfk(player);

		// Intentionally don't send feedback - The AfkSystem will broadcast to everyone, including this player, that they're AFK.
		return 1;
	}
}
