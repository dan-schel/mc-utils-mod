package com.danschellekens.mc.commands.warp;

import com.danschellekens.mc.state.WarpLocationsState;
import com.danschellekens.mc.state.WarpLocation;
import com.danschellekens.mc.utils.CommandUtils;
import com.danschellekens.mc.utils.WarpLocationSuggestionProvider;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class WarpWhereCommand {
  public static RequiredArgumentBuilder<ServerCommandSource,String> COMMAND = CommandManager
		.argument("where", StringArgumentType.word())
		.suggests(new WarpLocationSuggestionProvider(false))
		.executes(WarpWhereCommand::execute);

  private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
		ServerPlayerEntity player = source.getPlayer();
		
		if (player == null) {
			return CommandUtils.failure(source, "Not executed by a player.");
		}
		
		String name = StringArgumentType.getString(context, "where");
		WarpLocationsState locations = WarpLocationsState.getServerState(source.getServer());
		WarpLocation location = locations.get(player.getUuid(), name);

		if (location == null) {
			return CommandUtils.failure(source, "Warp point \"" + name + "\" not found.");
		}

		ServerWorld world = source.getServer().getWorld(location.getDimension().getWorldRegistryKey());
		double x = location.getPosition().getX() + 0.5;
		double y = location.getPosition().getY();
		double z = location.getPosition().getZ() + 0.5;
		float yaw = player.getYaw();
		float pitch = player.getPitch();		
		player.teleport(world, x, y, z, yaw, pitch);

		return CommandUtils.success(source, "Warped to \"" + name + "\".");
	}
}
