package com.danschellekens.mc.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

public class ChatUtils {

  private static final Pattern BRACED_TEXT_PATTERN = Pattern.compile(
    "\\{([^}]*)}"
  );

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

  public static Component highlightBracedText(String text) {
    return highlightBracedText(text, ChatFormatting.AQUA);
  }

  public static Component highlightBracedText(
    String text,
    ChatFormatting highlightColor
  ) {
    MutableComponent formatted = Component.empty();
    Matcher matcher = BRACED_TEXT_PATTERN.matcher(text);
    int currentIndex = 0;

    while (matcher.find()) {
      if (matcher.start() > currentIndex) {
        formatted.append(
          Component.literal(text.substring(currentIndex, matcher.start()))
        );
      }

      formatted.append(
        Component.literal(matcher.group(1)).withStyle(highlightColor)
      );
      currentIndex = matcher.end();
    }

    if (currentIndex < text.length()) {
      formatted.append(Component.literal(text.substring(currentIndex)));
    }

    return formatted;
  }
}
