package com.danschellekens.mc.utils;

import com.danschellekens.mc.state.WarpLocationsState;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

public class WarpLocationSuggestionProvider
  implements SuggestionProvider<CommandSourceStack>
{

  boolean hideGlobalWarpsUnlessOp;

  public WarpLocationSuggestionProvider(boolean hideGlobalWarpsUnlessOp) {
    this.hideGlobalWarpsUnlessOp = hideGlobalWarpsUnlessOp;
  }

  @Override
  public CompletableFuture<Suggestions> getSuggestions(
    CommandContext<CommandSourceStack> context,
    SuggestionsBuilder builder
  ) throws CommandSyntaxException {
    CommandSourceStack source = context.getSource();
    WarpLocationsState warpLocations = WarpLocationsState.getServerState(
      source.getServer()
    );

    ServerPlayer currentPlayer = source.getPlayer();
    if (currentPlayer == null) {
      return builder.buildFuture();
    }

    boolean isPlayerOp = currentPlayer
      .permissions()
      .hasPermission(Permissions.COMMANDS_OWNER);
    boolean includeGlobal = isPlayerOp || !this.hideGlobalWarpsUnlessOp;

    for (String warpName : warpLocations.getPossibleWarps(
      currentPlayer.getUUID(),
      includeGlobal
    )) {
      builder.suggest(warpName);
    }

    return builder.buildFuture();
  }
}
