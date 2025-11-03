package com.danschellekens.mc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danschellekens.mc.commands.DaytimeCommand;
import com.danschellekens.mc.commands.SunshineCommand;
import com.danschellekens.mc.commands.VisitCommand;
import com.danschellekens.mc.commands.WarpCommand;

public class DansUtils implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER = LoggerFactory.getLogger("dan-schel-utils");

	public static final String MOD_ID = "dan-schel-utils";

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(DaytimeCommand.COMMAND);
			dispatcher.register(SunshineCommand.COMMAND);
			dispatcher.register(VisitCommand.COMMAND);
			dispatcher.register(WarpCommand.COMMAND);
		});
	}
}