package com.danschellekens.mc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

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

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("foo")
        .executes(context -> {
      // For versions below 1.19, replace "Text.literal" with "new LiteralText".
      // For versions below 1.20, remode "() ->" directly.
      context.getSource().sendFeedback(() -> Text.literal("Called /foo with no arguments."), false);
			if (context.getSource().isExecutedByPlayer()) {
				ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
				CommandManager commands = player.getServer().getCommandManager();
				commands.executeWithPrefix(context.getSource(), "teleport ~ ~10 ~");
			}
			else {
				context.getSource().sendFeedback(() -> Text.literal("Not executed by a player."), false);
			}
      return 1;
    })));
	}
}