package com.danschellekens.mc.commands;

import com.danschellekens.mc.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;

public class RainCommand {

  private static final int DURATION_TICKS = 20 * 60 * 20; // 20 mins

  public static LiteralArgumentBuilder<CommandSourceStack> COMMAND =
    Commands.literal("rain").executes(RainCommand::execute);

  public static int execute(CommandContext<CommandSourceStack> context)
    throws CommandSyntaxException {
    CommandSourceStack source = context.getSource();
    ServerLevel world = source.getLevel();

    if (world.isRaining() && !world.isThundering()) {
      return CommandUtils.failure(source, "It's already raining.");
    }

    world.setWeatherParameters(0, DURATION_TICKS, true, false);

    return CommandUtils.success(source, "Requested 20 minutes of rain.", true);
  }
}
