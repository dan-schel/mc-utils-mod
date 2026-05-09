package com.danschellekens.mc.commands;

import com.danschellekens.mc.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;

public class DaytimeCommand {

  public static LiteralArgumentBuilder<CommandSourceStack> COMMAND =
    Commands.literal("daytime").executes(DaytimeCommand::execute);

  public static int execute(CommandContext<CommandSourceStack> context)
    throws CommandSyntaxException {
    CommandSourceStack source = context.getSource();
    ServerLevel world = source.getLevel();

    long currentTime = world.getDayTime() % 24000;

    if (currentTime < 10000) {
      return CommandUtils.failure(source, "It's already daytime.");
    }

    world.setDayTime(0);

    return CommandUtils.success(source, "Skipped to daytime.", true);
  }
}
