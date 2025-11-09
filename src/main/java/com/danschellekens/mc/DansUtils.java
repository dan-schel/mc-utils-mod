package com.danschellekens.mc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danschellekens.mc.afk.AfkSystem;
import com.danschellekens.mc.commands.AfkCommand;
import com.danschellekens.mc.commands.DaytimeCommand;
import com.danschellekens.mc.commands.SunshineCommand;
import com.danschellekens.mc.commands.VisitCommand;
import com.danschellekens.mc.commands.WarpCommand;

public class DansUtils implements ModInitializer {
  public static final Logger LOGGER = LoggerFactory.getLogger("dan-schel-utils");

	public static final String MOD_ID = "dan-schel-utils";
	public static final String MOD_ID_SNAKE_CASE = "dan_schel_utils";

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(AfkCommand.COMMAND);
			dispatcher.register(DaytimeCommand.COMMAND);
			dispatcher.register(SunshineCommand.COMMAND);
			dispatcher.register(VisitCommand.COMMAND);
			dispatcher.register(WarpCommand.COMMAND);
		});

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			AfkSystem.getInstance().onTick(server);
		});
	}
}