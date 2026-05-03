package com.danschellekens.mc.commands.warp;

import com.danschellekens.mc.state.WarpLocationsState;
import com.danschellekens.mc.state.WarpLocationsState.RemoveResult;
import com.danschellekens.mc.utils.CommandUtils;
import com.danschellekens.mc.utils.WarpLocationSuggestionProvider;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import org.eclipse.jdt.annotation.Nullable;

public class WarpRemoveCommand {

  public static LiteralArgumentBuilder<CommandSourceStack> COMMAND =
    Objects.requireNonNull(
      Commands.literal("remove").then(
        Commands.argument("name", StringArgumentType.word())
          .suggests(new WarpLocationSuggestionProvider(true))
          .executes(WarpRemoveCommand::execute)
      )
    );

  private static int execute(
    @Nullable CommandContext<CommandSourceStack> context
  ) throws CommandSyntaxException {
    CommandContext<CommandSourceStack> checkedContext = Objects.requireNonNull(
      context
    );
    CommandSourceStack source = Objects.requireNonNull(
      checkedContext.getSource()
    );
    ServerPlayer player = source.getPlayer();

    if (player == null) {
      return CommandUtils.failure(source, "Not executed by a player.");
    }

    String name = Objects.requireNonNull(
      StringArgumentType.getString(checkedContext, "name")
    );
    boolean isPlayerOp = player
      .permissions()
      .hasPermission(Permissions.COMMANDS_OWNER);
    WarpLocationsState locations = WarpLocationsState.getServerState(
      Objects.requireNonNull(source.getServer())
    );

    RemoveResult result = locations.remove(
      Objects.requireNonNull(player.getUUID()),
      name,
      isPlayerOp
    );

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
