package com.danschellekens.mc.utils;

import net.minecraft.command.DefaultPermissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CommandUtils {
  public static int success(ServerCommandSource source, String message) {
    source.sendFeedback(() -> Text.literal(message), true);
    return 1;
  }

  public static int failure(ServerCommandSource source, String message) {
    source.sendFeedback(() -> Text.literal(message), false);
    return 0;
  }

  public static int successAndTellEveryone(ServerCommandSource source, String message) {
    ServerPlayerEntity player = source.getPlayer();

    source.sendFeedback(() -> Text.literal(message), true);

    for (ServerPlayerEntity onlinePlayer : source.getServer().getPlayerManager().getPlayerList()) {
      if (onlinePlayer.getPermissions().hasPermission(DefaultPermissions.OWNERS)) {
        continue;
      }
      if (player != null && onlinePlayer.getId() == player.getId()) {
        continue;
      }

      onlinePlayer.sendMessageToClient(Text.literal("[" + source.getName() + ": " + message + "]").formatted(Formatting.GRAY, Formatting.ITALIC), false);
    }

    return 1;
  }
}
