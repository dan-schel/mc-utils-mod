package com.danschellekens.mc.commands;

import com.danschellekens.mc.state.GlobalWarpLocations;
import com.danschellekens.mc.state.WarpLocation;
import com.danschellekens.mc.utils.WarpLocationSuggestionProvider;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class WarpCommand {
  public static LiteralArgumentBuilder<ServerCommandSource> COMMAND = CommandManager
    .literal("warp")
    .then(
      CommandManager
        .argument("where", StringArgumentType.word())
        .suggests(new WarpLocationSuggestionProvider())
        .executes(WarpCommand::executeWarp)
    )
		.then(
			CommandManager
				.literal("saveglobal")
				.requires(source -> source.hasPermissionLevel(4))
				.then(
					CommandManager
						.argument("name", StringArgumentType.word())
						.executes(WarpCommand::executeSaveGlobal)
				)
		);

  public static int executeWarp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
						
		if (!source.isExecutedByPlayer()) {
			source.sendFeedback(() -> Text.literal("Not executed by a player."), false);
			return 0;
		}
		
		ServerPlayerEntity player = source.getPlayerOrThrow();
		String name = StringArgumentType.getString(context, "where");
		GlobalWarpLocations locations = GlobalWarpLocations.getServerState(source.getServer());
		
		if (!locations.hasWithName(name)) {
			source.sendFeedback(() -> Text.literal("Warp point \"" + name + "\" not found."), false);
			return 0;
		}

		WarpLocation location = locations.get(name);

		ServerWorld world = source.getServer().getWorld(WarpLocation.getDimensionForString(location.getDimension()));
		double x = location.getPosition().getX() + 0.5;
		double y = location.getPosition().getY();
		double z = location.getPosition().getZ() + 0.5;
		float yaw = player.getYaw();
		float pitch = player.getPitch();		
		player.teleport(world, x, y, z, yaw, pitch);

    String message = player.getName().getString() + " warped to \"" + name + "\".";
		source.sendFeedback(() -> Text.literal(message), true);
		
    return 1;
	}

  public static int executeSaveGlobal(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();

		if (!source.isExecutedByPlayer()) {
			source.sendFeedback(() -> Text.literal("Not executed by a player."), false);
			return 0;
		}

		ServerPlayerEntity player = source.getPlayerOrThrow();
		BlockPos position = player.getBlockPos();

		String name = StringArgumentType.getString(context, "name");
		// Do a proper regex check.
		if (name.contains(" ")) {
			source.sendFeedback(() -> Text.literal("Warp point names cannot contain spaces."), false);
			return 0;
		}

		GlobalWarpLocations locations = GlobalWarpLocations.getServerState(source.getServer());
		if (locations.hasWithName(name)) {
			source.sendFeedback(() -> Text.literal("\"" + name + "\" is already taken."), false);
			return 0;
		}

		String dimension = WarpLocation.getStringForDimension(source.getWorld().getRegistryKey());
		locations.add(new WarpLocation(name, position, dimension));
		
		String dimensionDisplayString = WarpLocation.getDimensionDisplayString(dimension);
		String message = "Saved global warp point \"" + name + "\" (" + dimensionDisplayString + " at " + position.getX() + ", " + position.getY() + ", " + position.getZ() + ")."; 
		source.sendFeedback(() -> Text.literal(message), true);
		return 1;
	}
}
