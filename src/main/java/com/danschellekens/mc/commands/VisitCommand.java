package com.danschellekens.mc.commands;

import com.danschellekens.mc.utils.CommandUtils;
import com.danschellekens.mc.utils.PlayerSuggestionProvider;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class VisitCommand {
  public static LiteralArgumentBuilder<ServerCommandSource> COMMAND = CommandManager
    .literal("visit")
    .then(
      CommandManager
        .argument("who", EntityArgumentType.player())
        .suggests(new PlayerSuggestionProvider(false))
        .executes(VisitCommand::execute)
    );

  public static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
		ServerPlayerEntity player = source.getPlayer();
		
		if (player == null) {
			return CommandUtils.failure(source, "Not executed by a player.");
		}

		ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "who");
		
		if (player.getId() == target.getId()) {
			return CommandUtils.failure(source, "You can't visit yourself.");
		}

		ServerWorld world = target.getServerWorld();
		double x = target.getX();
		double y = target.getY();
		double z = target.getZ();
		float yaw = player.getYaw();
		float pitch = player.getPitch();		
		player.teleport(world, x, y, z, yaw, pitch);

		target.sendMessageToClient(Text.literal(player.getName().getString() + " is visiting you."), false);

		return CommandUtils.success(source, "Visiting " + target.getName().getString() + ".");
	}
}
