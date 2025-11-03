package com.danschellekens.mc.commands;

import com.danschellekens.mc.state.GlobalWarpLocations;
import com.danschellekens.mc.state.WarpLocation;
import com.danschellekens.mc.utils.WarpLocationSuggestionProvider;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.argument.EntityArgumentType;
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
				.then(
					CommandManager
						.argument("where", StringArgumentType.word())
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
		ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "who");
		
		if (player.getId() == target.getId()) {
			source.sendFeedback(() -> Text.literal("You can't teleport to yourself."), false);
			return 0;
		}

		ServerWorld world = target.getServerWorld();
		double x = target.getX();
		double y = target.getY();
		double z = target.getZ();
		float yaw = player.getYaw();
		float pitch = player.getPitch();		
		player.teleport(world, x, y, z, yaw, pitch);

    String message = player.getName().getString() + " teleported to " + target.getName().getString() + ".";
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

		String name = StringArgumentType.getString(context, "where");
		// Do a proper regex check.
		if (name.contains(" ")) {
			source.sendFeedback(() -> Text.literal("Warp location names cannot contain spaces."), false);
			return 0;
		}

		String dimension = WarpLocation.getStringForDimension(source.getWorld().getRegistryKey());
		String dimensionDisplayString = WarpLocation.getDimensionDisplayString(dimension);

		GlobalWarpLocations locations = GlobalWarpLocations.getServerState(source.getServer());
		locations.add(new WarpLocation(name, position, dimension));

		String message = "Saved global warp location \"" + name + "\" (" + dimensionDisplayString + " at " + position.getX() + ", " + position.getY() + ", " + position.getZ() + ")."; 
		source.sendFeedback(() -> Text.literal(message), true);
		return 1;
	}
}
