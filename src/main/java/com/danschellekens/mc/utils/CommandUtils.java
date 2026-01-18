package com.danschellekens.mc.utils;

import net.minecraft.command.DefaultPermissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CommandUtils {

  public static int success(ServerCommandSource source, String message) {
    return success(source, Text.literal(message));
  }

  public static int success(ServerCommandSource source, Text message) {
    source.sendFeedback(() -> message, true);
    return 1;
  }

  public static int success(
    ServerCommandSource source,
    String message,
    boolean tellEveryone
  ) {
    return success(
      source,
      Text.literal(message),
      thirdPartyFormattedMessage(source, message),
      tellEveryone
    );
  }

  public static int success(
    ServerCommandSource source,
    Text personalMessage,
    Text thirdPartyMessage,
    boolean tellEveryone
  ) {
    ServerPlayerEntity player = source.getPlayer();

    source.sendFeedback(() -> personalMessage, false);

    for (ServerPlayerEntity otherPlayer : source
      .getServer()
      .getPlayerManager()
      .getPlayerList()) {
      if (player != null && otherPlayer.getId() == player.getId()) {
        continue;
      }
      if (
        tellEveryone ||
        otherPlayer.getPermissions().hasPermission(DefaultPermissions.OWNERS)
      ) {
        otherPlayer.sendMessageToClient(thirdPartyMessage, false);
      }
    }

    return 1;
  }

  public static int failure(ServerCommandSource source, String message) {
    source.sendFeedback(() -> Text.literal(message), false);
    return 0;
  }

  public static int failure(ServerCommandSource source, Text message) {
    source.sendFeedback(() -> message, false);
    return 0;
  }

  public static int successWithUndoCommand(
    ServerCommandSource source,
    String message,
    String undoCommand
  ) {
    Text personalMessage = Text.literal(message + " (Undo with ")
      .append(Text.literal(undoCommand).formatted(Formatting.AQUA))
      .append(Text.literal(".)"));

    return CommandUtils.success(
      source,
      personalMessage,
      thirdPartyFormattedMessage(source, message),
      false
    );
  }

  private static Text thirdPartyFormattedMessage(
    ServerCommandSource source,
    String message
  ) {
    return Text.literal(
      "[" + source.getName() + ": " + message + "]"
    ).formatted(Formatting.GRAY, Formatting.ITALIC);
  }
}
