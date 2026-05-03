package com.danschellekens.mc.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

public class ChatUtils {

  public static void logAndTellOps(MinecraftServer server, Component message) {
    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      if (player.permissions().hasPermission(Permissions.COMMANDS_OWNER)) {
        player.sendSystemMessage(message, false);
      }
    }

    server.sendSystemMessage(message);
  }

  public static Component thirdPartyFormattedMessage(
    String source,
    String message
  ) {
    return Component.literal("[" + source + ": " + message + "]").withStyle(
      ChatFormatting.GRAY,
      ChatFormatting.ITALIC
    );
  }
}
