package com.danschellekens.mc.commands;

import com.danschellekens.mc.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

public class SunshineCommand {
  public static LiteralArgumentBuilder<ServerCommandSource> COMMAND = CommandManager
    .literal("sunshine")
    .executes(SunshineCommand::execute);

  public static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
		ServerWorld world = source.getWorld();

		if (!world.isRaining() && !world.isThundering()) {
			return CommandUtils.failure(source, "The weather is already clear.");
		}

		// Clear the weather for 3 hours (the docs say it's measured in seconds, but in reality it's ticks).
		world.setWeather(3 * 60 * 60 * 20, 0, false, false);

		return CommandUtils.successAndTellEveryone(source, "Cleared the weather for ~3 hours.");
	}
}
