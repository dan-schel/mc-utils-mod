package com.danschellekens.mc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.*;

public class DansUtils implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("dan-schel-utils");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		CommandRegistrationCallback.EVENT.register(
			(dispatcher, registryAccess, environment) -> dispatcher.register(literal("visit").then(argument("target", EntityArgumentType.player()).executes(
				context -> {
					final ServerCommandSource source = context.getSource();
					
					if (!source.isExecutedByPlayer()) {
						context.getSource().sendFeedback(() -> Text.literal("Not executed by a player."), false);
						return 1;
					}
					
					final ServerPlayerEntity player = source.getPlayerOrThrow();	
					final ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "target");
					
					if (player.getId() == target.getId()) {
						context.getSource().sendFeedback(() -> Text.literal("You can't teleport to yourself."), false);
						return 1;
					}

					final Vec3d targetPosition = target.getPos();
					player.teleport(targetPosition.x, targetPosition.y, targetPosition.z, false);
					context.getSource().sendFeedback(() -> Text.literal(player.getName().getString() + " teleported to " + target.getName().getString() + "."), true);
					return 1;
				}
			))
		));
	}
}