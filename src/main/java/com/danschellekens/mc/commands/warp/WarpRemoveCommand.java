package com.danschellekens.mc.commands.warp;

import com.danschellekens.mc.state.WarpLocationsState;
import com.danschellekens.mc.state.WarpLocationsState.RemoveResult;
import com.danschellekens.mc.utils.CommandUtils;
import com.danschellekens.mc.utils.WarpLocationSuggestionProvider;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class WarpRemoveCommand {
  public static LiteralArgumentBuilder<ServerCommandSource> COMMAND = CommandManager
    .literal("remove")
    .then(
      CommandManager
        .argument("name", StringArgumentType.word())
		    .suggests(new WarpLocationSuggestionProvider())
        .executes(WarpRemoveCommand::execute)
    );


	private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
    ServerPlayerEntity player = source.getPlayer();
		
		if (player == null) {
      return CommandUtils.failure(source, "Not executed by a player.");
		}

		String name = StringArgumentType.getString(context, "name");
    boolean isPlayerOp = player.hasPermissionLevel(4);
		WarpLocationsState locations = WarpLocationsState.getServerState(source.getServer());
    
    RemoveResult result = locations.remove(player.getUuid(), name, isPlayerOp);

    switch (result) {
      case REMOVED_GLOBAL:
        return CommandUtils.success(source, "Removed global warp point \"" + name + "\".");
      case REMOVED_PLAYER_SPECIFIC:
        return CommandUtils.success(source, "Removed personal warp point \"" + name + "\".");
      case NOT_FOUND:
        return CommandUtils.failure(source, "Cannot find warp point called \"" + name + "\".");
      case REQUIRES_OP:
        return CommandUtils.failure(source, "Only server operators can remove global warp points.");
      default:
        throw new RuntimeException("Unknown RemoveResult value.");
    }
	}
}
