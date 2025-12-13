package com.danschellekens.mc.utils;

import java.util.concurrent.CompletableFuture;

import com.danschellekens.mc.state.WarpLocationsState;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.command.DefaultPermissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class WarpLocationSuggestionProvider implements SuggestionProvider<ServerCommandSource>  {
	boolean hideGlobalWarpsUnlessOp;

	public WarpLocationSuggestionProvider(boolean hideGlobalWarpsUnlessOp) {
		this.hideGlobalWarpsUnlessOp = hideGlobalWarpsUnlessOp;
	}

  @Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
    WarpLocationsState warpLocations = WarpLocationsState.getServerState(source.getServer());

    ServerPlayerEntity currentPlayer = source.getPlayer();
		if (currentPlayer == null) {
			return builder.buildFuture();
		}

		boolean isPlayerOp = currentPlayer.getPermissions().hasPermission(DefaultPermissions.OWNERS);
		boolean includeGlobal = isPlayerOp || !this.hideGlobalWarpsUnlessOp;
    
		for (String warpName : warpLocations.getPossibleWarps(currentPlayer.getUuid(), includeGlobal)) {
			builder.suggest(warpName);
		}

		return builder.buildFuture();
	}
}
