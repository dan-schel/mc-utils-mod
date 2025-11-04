package com.danschellekens.mc.commands.warp;

import com.danschellekens.mc.state.WarpLocationsState;
import com.danschellekens.mc.state.WarpLocationsState.AddResult;
import com.danschellekens.mc.state.WarpLocation;
import com.danschellekens.mc.utils.CommandUtils;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class WarpAddCommand {
  public static LiteralArgumentBuilder<ServerCommandSource> COMMAND = CommandManager
    .literal("add")
    .then(
      CommandManager
        .argument("name", StringArgumentType.word())
        .then(
          CommandManager
            .argument("global", BoolArgumentType.bool())
            .requires(source -> source.hasPermissionLevel(4))
            .executes(WarpAddCommand::withGlobalArg)
        )
        .executes(WarpAddCommand::withoutGlobalArg)
    );

  private static int withGlobalArg(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
    return WarpAddCommand.execute(context, true);
	}

  private static int withoutGlobalArg(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
    return WarpAddCommand.execute(context, false);
	}

	private static int execute(CommandContext<ServerCommandSource> context, boolean hasGlobalArgument) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
    ServerPlayerEntity player = source.getPlayer();
		
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
    
    WarpLocation location = WarpLocation.fromWorld(player.getBlockPos(), source.getWorld());
		WarpLocationsState locations = WarpLocationsState.getServerState(source.getServer());
    boolean isPlayerOp = player.hasPermissionLevel(4);

    AddResult result = locations.add(player.getUuid(), name, location, global, isPlayerOp);

    switch (result) {
      case ADDED_NEW:
        return CommandUtils.success(source, "Added " + (global ? "global " : "personal ") + "warp point \"" + name + "\" (" + location.getDisplayString() + ").");
      case UPDATED_EXISTING:
        return CommandUtils.success(source, "Moved " + (global ? "global " : "personal ") + "warp point \"" + name + "\" to " + location.getDisplayString() + ".");
      case CLASHES_WITH_GLOBAL:
        return CommandUtils.failure(source, "There is already global warp point with this name.");
      case REQUIRES_OP:
        return CommandUtils.failure(source, "Only server operators can add global warp points.");
      case ALREADY_REACHED_MAXIMUM:
        return CommandUtils.failure(source, "Players are limited to " + WarpLocationsState.MAX_WARP_LOCATIONS_PER_PLAYER + " warp points each.");
      default:
        throw new RuntimeException("Unknown AddResult value.");
    }
	}
}
