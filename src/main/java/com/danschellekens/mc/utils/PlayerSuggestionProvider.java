package com.danschellekens.mc.utils;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class PlayerSuggestionProvider
  implements SuggestionProvider<CommandSourceStack>
{

  private boolean includeSelf;

  public PlayerSuggestionProvider(boolean includeSelf) {
    this.includeSelf = includeSelf;
  }

  @Override
  public CompletableFuture<Suggestions> getSuggestions(
    CommandContext<CommandSourceStack> context,
    SuggestionsBuilder builder
  ) throws CommandSyntaxException {
    CommandSourceStack source = context.getSource();
    Collection<String> playerNames = source.getOnlinePlayerNames();

    ServerPlayer currentPlayer = source.getPlayer();

    for (String playerName : playerNames) {
      if (
        !includeSelf &&
        currentPlayer != null &&
        playerName.equals(currentPlayer.getName().getString())
      ) {
        continue;
      }

      builder.suggest(playerName);
    }

    return builder.buildFuture();
  }
}
