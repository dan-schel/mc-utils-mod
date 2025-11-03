package com.danschellekens.mc;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerSuggestionProvider implements SuggestionProvider<ServerCommandSource>  {
  private boolean includeSelf;

  public PlayerSuggestionProvider(boolean includeSelf) {
    this.includeSelf = includeSelf;
  }

  @Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		ServerCommandSource source = context.getSource();
		Collection<String> playerNames = source.getPlayerNames();
    
    ServerPlayerEntity currentPlayer = source.getPlayer();
    
		for (String playerName : playerNames) {
      if (!includeSelf && currentPlayer != null && playerName.equals(currentPlayer.getName().getString())) {
        continue;
      }

			builder.suggest(playerName);
		}

		return builder.buildFuture();
	}
}
