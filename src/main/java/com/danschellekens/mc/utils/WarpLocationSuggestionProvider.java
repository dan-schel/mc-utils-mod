package com.danschellekens.mc.utils;

import java.util.concurrent.CompletableFuture;

import com.danschellekens.mc.state.GlobalWarpLocations;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.server.command.ServerCommandSource;

public class WarpLocationSuggestionProvider implements SuggestionProvider<ServerCommandSource>  {
  @Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
    GlobalWarpLocations warpLocations = GlobalWarpLocations.getServerState(source.getServer());
    
    // TODO: [DS] So far this is only the global warp locations. Need to update
    // later with per-player warp locations.

		for (String locationName : warpLocations.getAllNames()) {
			builder.suggest(locationName);
		}

		return builder.buildFuture();
	}
}
