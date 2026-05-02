package com.danschellekens.mc.commands.warp;

import com.danschellekens.mc.state.WarpLocationsState;
import com.danschellekens.mc.state.WarpLocationsState.RemoveResult;
import com.danschellekens.mc.utils.CommandUtils;
import com.danschellekens.mc.utils.WarpLocationSuggestionProvider;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

public class WarpRemoveCommand {

  public static LiteralArgumentBuilder<CommandSourceStack> COMMAND =
    Commands.literal("remove").then(
      Commands.argument("name", StringArgumentType.word())
        .suggests(new WarpLocationSuggestionProvider(true))
        .executes(WarpRemoveCommand::execute)
    );

  private static int execute(CommandContext<CommandSourceStack> context)
    throws CommandSyntaxException {
    CommandSourceStack source = context.getSource();
    ServerPlayer player = source.getPlayer();

    if (player == null) {
      return CommandUtils.failure(source, "Not executed by a player.");
    }

    String name = StringArgumentType.getString(context, "name");
    boolean isPlayerOp = player
      .permissions()
      .hasPermission(Permissions.COMMANDS_OWNER);
    WarpLocationsState locations = WarpLocationsState.getServerState(
      source.getServer()
    );

    RemoveResult result = locations.remove(player.getUUID(), name, isPlayerOp);

    switch (result) {
      case REMOVED_GLOBAL:
        return CommandUtils.success(
          source,
          "Removed global warp point \"" + name + "\"."
        );
      case REMOVED_PLAYER_SPECIFIC:
        return CommandUtils.success(
          source,
          "Removed personal warp point \"" + name + "\"."
        );
      case NOT_FOUND:
        return CommandUtils.failure(
          source,
          "Cannot find warp point called \"" + name + "\"."
        );
      case REQUIRES_OP:
        return CommandUtils.failure(
          source,
          "Only server operators can remove global warp points."
        );
      default:
        throw new RuntimeException("Unknown RemoveResult value.");
    }
  }
}
