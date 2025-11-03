package com.danschellekens.mc.commands;

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

}
