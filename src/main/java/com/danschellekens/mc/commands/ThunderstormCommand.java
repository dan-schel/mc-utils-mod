package com.danschellekens.mc.commands;

import com.danschellekens.mc.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;

public class ThunderstormCommand {

  private static final int DURATION_TICKS = 20 * 60 * 20; // 20 mins

  public static LiteralArgumentBuilder<CommandSourceStack> COMMAND =
    Commands.literal("thunderstorm").executes(ThunderstormCommand::execute);

  public static int execute(CommandContext<CommandSourceStack> context)
    throws CommandSyntaxException {
    CommandSourceStack source = context.getSource();
    ServerLevel world = source.getLevel();

    if (world.isRaining() && world.isThundering()) {
      return CommandUtils.failure(source, "It's already thundering.");
    }

    world.setWeatherParameters(0, DURATION_TICKS, true, true);

    return CommandUtils.success(
      source,
      "Requested a 20 minute thunderstorm.",
      true
    );
  }
}
