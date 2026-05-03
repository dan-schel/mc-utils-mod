package com.danschellekens.mc.commands;

import com.danschellekens.mc.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;

public class SunshineCommand {

  public static LiteralArgumentBuilder<CommandSourceStack> COMMAND =
    Commands.literal("sunshine").executes(SunshineCommand::execute);

  public static int execute(CommandContext<CommandSourceStack> context)
    throws CommandSyntaxException {
    CommandSourceStack source = context.getSource();
    ServerLevel world = source.getLevel();

    if (!world.isRaining() && !world.isThundering()) {
      return CommandUtils.failure(source, "The weather is already clear.");
    }

    // Clear the weather for 3 hours (the docs say it's measured in seconds, but in reality it's ticks).
    world.setWeatherParameters(3 * 60 * 60 * 20, 0, false, false);

    return CommandUtils.success(
      source,
      "Cleared the weather for 3 hours.",
      true
    );
  }
}
