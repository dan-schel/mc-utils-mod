package com.danschellekens.mc.utils;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class CommandUtils {
  public static int success(ServerCommandSource source, String message) {
    source.sendFeedback(() -> Text.literal(message), true);
    return 1;
  }

  public static int failure(ServerCommandSource source, String message) {
    source.sendFeedback(() -> Text.literal(message), false);
    return 0;
  }
}
