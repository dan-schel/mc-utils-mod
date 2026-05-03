package com.danschellekens.mc.utils;

import com.danschellekens.mc.state.WarpLocationsState;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import org.eclipse.jdt.annotation.Nullable;

public class WarpLocationSuggestionProvider
  implements SuggestionProvider<CommandSourceStack>
{

  boolean hideGlobalWarpsUnlessOp;

  public WarpLocationSuggestionProvider(boolean hideGlobalWarpsUnlessOp) {
    this.hideGlobalWarpsUnlessOp = hideGlobalWarpsUnlessOp;
  }

  @Override
  public CompletableFuture<Suggestions> getSuggestions(
    @Nullable CommandContext<CommandSourceStack> context,
    @Nullable SuggestionsBuilder builder
  ) throws CommandSyntaxException {
    CommandContext<CommandSourceStack> checkedContext = Objects.requireNonNull(
      context
    );
    SuggestionsBuilder checkedBuilder = Objects.requireNonNull(builder);
    CommandSourceStack source = Objects.requireNonNull(
      checkedContext.getSource()
    );
    WarpLocationsState warpLocations = WarpLocationsState.getServerState(
      Objects.requireNonNull(source.getServer())
    );

    ServerPlayer currentPlayer = source.getPlayer();
    if (currentPlayer == null) {
      return Objects.requireNonNull(checkedBuilder.buildFuture());
    }

    boolean isPlayerOp = currentPlayer
      .permissions()
      .hasPermission(Permissions.COMMANDS_OWNER);
    boolean includeGlobal = isPlayerOp || !this.hideGlobalWarpsUnlessOp;

    for (String warpName : warpLocations.getPossibleWarps(
      Objects.requireNonNull(currentPlayer.getUUID()),
      includeGlobal
    )) {
      checkedBuilder.suggest(warpName);
    }

    return Objects.requireNonNull(checkedBuilder.buildFuture());
  }
}
