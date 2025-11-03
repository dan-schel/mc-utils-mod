package com.danschellekens.mc.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class DaytimeCommand {
  public static LiteralArgumentBuilder<ServerCommandSource> COMMAND = CommandManager
    .literal("daytime")
    .executes(DaytimeCommand::execute);

  public static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		final ServerCommandSource source = context.getSource();
		final ServerWorld world = source.getWorld();

		final long currentTime = world.getTimeOfDay() % 24000;

		if (currentTime < 10000) {
			source.sendFeedback(() -> Text.literal("It isn't night time."), false);
			return 0;
		}

		world.setTimeOfDay(0);

		source.sendFeedback(() -> Text.literal("Skipped to daytime."), true);
		return 1;
	}

}
