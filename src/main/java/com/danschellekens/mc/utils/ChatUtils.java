package com.danschellekens.mc.utils;

import net.minecraft.command.DefaultPermissions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChatUtils {

  public static void logAndTellOps(MinecraftServer server, Text message) {
    for (ServerPlayerEntity player : server
      .getPlayerManager()
      .getPlayerList()) {
      if (player.getPermissions().hasPermission(DefaultPermissions.OWNERS)) {
        player.sendMessageToClient(message, false);
      }
    }

    server.sendMessage(message);
  }

  public static Text thirdPartyFormattedMessage(String source, String message) {
    return Text.literal("[" + source + ": " + message + "]").formatted(
      Formatting.GRAY,
      Formatting.ITALIC
    );
  }
}
