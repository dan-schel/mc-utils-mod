package com.danschellekens.mc.commands;

import com.danschellekens.mc.afk.AfkSystem;
import com.danschellekens.mc.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.eclipse.jdt.annotation.Nullable;

public class AfkCommand {

  public static LiteralArgumentBuilder<CommandSourceStack> COMMAND =
    Objects.requireNonNull(
      Commands.literal("afk").executes(AfkCommand::execute)
    );

  public static int execute(
    @Nullable CommandContext<CommandSourceStack> context
  ) throws CommandSyntaxException {
    CommandSourceStack source = Objects.requireNonNull(
      Objects.requireNonNull(context).getSource()
    );
    ServerPlayer player = source.getPlayer();

    if (player == null) {
      return CommandUtils.failure(source, "Not executed by a player.");
    }

    AfkSystem.getInstance().manuallyDeclareAfk(player);

    // Intentionally don't send feedback - The AfkSystem will broadcast to everyone, including this player, that they're AFK.
    return 1;
  }
}
