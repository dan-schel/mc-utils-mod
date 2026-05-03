package com.danschellekens.mc.utils;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.eclipse.jdt.annotation.Nullable;

public class PlayerSuggestionProvider
  implements SuggestionProvider<CommandSourceStack>
{

  private boolean includeSelf;

  public PlayerSuggestionProvider(boolean includeSelf) {
    this.includeSelf = includeSelf;
  }

  @Override
  public CompletableFuture<Suggestions> getSuggestions(
    @Nullable CommandContext<CommandSourceStack> context,
    @Nullable SuggestionsBuilder builder
  ) throws CommandSyntaxException {
    CommandSourceStack source = Objects.requireNonNull(
      Objects.requireNonNull(context).getSource()
    );
    SuggestionsBuilder checkedBuilder = Objects.requireNonNull(builder);
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

      checkedBuilder.suggest(playerName);
    }

    return Objects.requireNonNull(checkedBuilder.buildFuture());
  }
}
