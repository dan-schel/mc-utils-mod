package com.danschellekens.mc.commands;

import com.danschellekens.mc.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import org.eclipse.jdt.annotation.Nullable;

public class DaytimeCommand {

  public static LiteralArgumentBuilder<CommandSourceStack> COMMAND =
    Objects.requireNonNull(
      Commands.literal("daytime").executes(DaytimeCommand::execute)
    );

  public static int execute(
    @Nullable CommandContext<CommandSourceStack> context
  ) throws CommandSyntaxException {
    CommandSourceStack source = Objects.requireNonNull(
      Objects.requireNonNull(context).getSource()
    );
    ServerLevel world = Objects.requireNonNull(source.getLevel());

    long currentTime = world.getDayTime() % 24000;

    if (currentTime < 10000) {
      return CommandUtils.failure(source, "It isn't night time.");
    }

    world.setDayTime(0);

    return CommandUtils.success(source, "Skipped to daytime.", true);
  }
}
