package com.danschellekens.mc.commands;

import com.danschellekens.mc.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

public class DaytimeCommand {
  public static LiteralArgumentBuilder<ServerCommandSource> COMMAND = CommandManager
    .literal("daytime")
    .executes(DaytimeCommand::execute);

  public static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
		ServerWorld world = source.getWorld();

		long currentTime = world.getTimeOfDay() % 24000;

		if (currentTime < 10000) {
			return CommandUtils.failure(source, "It isn't night time.");
		}

		world.setTimeOfDay(0);

		return CommandUtils.successAndTellEveryone(source, "Skipped to daytime.");
	}
}
