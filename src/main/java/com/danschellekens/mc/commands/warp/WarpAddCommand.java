package com.danschellekens.mc.commands.warp;

import com.danschellekens.mc.state.WarpLocation;
import com.danschellekens.mc.state.WarpLocationsState;
import com.danschellekens.mc.state.WarpLocationsState.AddResult;
import com.danschellekens.mc.utils.CommandUtils;
import com.mojang.brigadier.arguments.BoolArgumentType;
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

public class WarpAddCommand {

  public static LiteralArgumentBuilder<CommandSourceStack> COMMAND =
    Objects.requireNonNull(
      Commands.literal("add").then(
        Commands.argument("name", StringArgumentType.word())
          .then(
            Commands.argument("global", BoolArgumentType.bool())
              .requires(Commands.hasPermission(Commands.LEVEL_OWNERS))
              .executes(WarpAddCommand::withGlobalArg)
          )
          .executes(WarpAddCommand::withoutGlobalArg)
      )
    );

  private static int withGlobalArg(
    @Nullable CommandContext<CommandSourceStack> context
  ) throws CommandSyntaxException {
    return WarpAddCommand.execute(Objects.requireNonNull(context), true);
  }

  private static int withoutGlobalArg(
    @Nullable CommandContext<CommandSourceStack> context
  ) throws CommandSyntaxException {
    return WarpAddCommand.execute(Objects.requireNonNull(context), false);
  }

  private static int execute(
    CommandContext<CommandSourceStack> context,
    boolean hasGlobalArgument
  ) throws CommandSyntaxException {
    CommandSourceStack source = context.getSource();
    ServerPlayer player = source.getPlayer();

    if (player == null) {
      return CommandUtils.failure(source, "Not executed by a player.");
    }

    String name = StringArgumentType.getString(context, "name");
    if (!name.matches("^[a-z][a-z0-9_]{0,49}$")) {
      return CommandUtils.failure(source, "Invalid warp point name.");
    }

    boolean global = false;
    if (hasGlobalArgument) {
      global = BoolArgumentType.getBool(context, "global");
    }

    WarpLocation location = WarpLocation.fromWorld(
      Objects.requireNonNull(player.blockPosition()),
      Objects.requireNonNull(source.getLevel())
    );
    WarpLocationsState locations = WarpLocationsState.getServerState(
      Objects.requireNonNull(source.getServer())
    );
    boolean isPlayerOp = player
      .permissions()
      .hasPermission(Permissions.COMMANDS_OWNER);

    AddResult result = locations.add(
      Objects.requireNonNull(player.getUUID()),
      name,
      location,
      global,
      isPlayerOp
    );

    switch (result) {
      case ADDED_NEW:
        return CommandUtils.success(
          source,
          "Added " +
            (global ? "global " : "personal ") +
            "warp point \"" +
            name +
            "\" (" +
            location.getDisplayString() +
            ")."
        );
      case UPDATED_EXISTING:
        return CommandUtils.success(
          source,
          "Moved " +
            (global ? "global " : "personal ") +
            "warp point \"" +
            name +
            "\" to " +
            location.getDisplayString() +
            "."
        );
      case CLASHES_WITH_GLOBAL:
        return CommandUtils.failure(
          source,
          "There is already global warp point with this name."
        );
      case REQUIRES_OP:
        return CommandUtils.failure(
          source,
          "Only server operators can add global warp points."
        );
      case ALREADY_REACHED_MAXIMUM:
        return CommandUtils.failure(
          source,
          "Players are limited to " +
            WarpLocationsState.MAX_WARP_LOCATIONS_PER_PLAYER +
            " warp points each."
        );
      default:
        throw new RuntimeException("Unknown AddResult value.");
    }
  }
}
