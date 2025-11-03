package com.danschellekens.mc.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class SunshineCommand {
  public static LiteralArgumentBuilder<ServerCommandSource> COMMAND = CommandManager
    .literal("sunshine")
    .executes(SunshineCommand::execute);

  public static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
		ServerWorld world = source.getWorld();

		if (!world.isRaining() && !world.isThundering()) {
			source.sendFeedback(() -> Text.literal("The weather is already clear."), false);
			return 0;
		}

		// Clear the weather for 3 hours.
		world.setWeather(3 * 60 * 60, 0, false, false);

		source.sendFeedback(() -> Text.literal("Cleared the weather for 3 hours."), true);
		return 1;
	}
}
