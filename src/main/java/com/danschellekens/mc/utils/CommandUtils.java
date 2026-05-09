package com.danschellekens.mc.utils;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

public class CommandUtils {

  public static int success(CommandSourceStack source, String message) {
    return success(source, Component.literal(message));
  }

  public static int success(CommandSourceStack source, Component message) {
    source.sendSuccess(() -> message, true);
    return 1;
  }

  public static int success(
    CommandSourceStack source,
    String message,
    boolean tellEveryone
  ) {
    return success(
      source,
      Component.literal(message),
      thirdPartyFormattedMessage(source, message),
      tellEveryone
    );
  }

  public static int success(
    CommandSourceStack source,
    Component personalMessage,
    Component thirdPartyMessage,
    boolean tellEveryone
  ) {
    ServerPlayer player = source.getPlayer();

    source.sendSuccess(() -> personalMessage, false);

    for (ServerPlayer otherPlayer : source
      .getServer()
      .getPlayerList()
      .getPlayers()) {
      if (player != null && otherPlayer.getId() == player.getId()) {
        continue;
      }
      if (
        tellEveryone ||
        otherPlayer.permissions().hasPermission(Permissions.COMMANDS_OWNER)
      ) {
        otherPlayer.sendSystemMessage(thirdPartyMessage, false);
      }
    }

    source.getServer().sendSystemMessage(thirdPartyMessage);

    return 1;
  }

  public static int failure(CommandSourceStack source, String message) {
    source.sendSuccess(() -> Component.literal(message), false);
    return 0;
  }

  public static int failure(CommandSourceStack source, Component message) {
    source.sendSuccess(() -> message, false);
    return 0;
  }

  public static int successWithUndoCommand(
    CommandSourceStack source,
    String message,
    String undoCommand
  ) {
    Component personalMessage = ChatUtils.highlightBracedText(
      message + " (Undo with {" + undoCommand + "}.)"
    );

    return CommandUtils.success(
      source,
      personalMessage,
      thirdPartyFormattedMessage(source, message),
      false
    );
  }

  private static Component thirdPartyFormattedMessage(
    CommandSourceStack source,
    String message
  ) {
    return ChatUtils.thirdPartyFormattedMessage(source.getTextName(), message);
  }
}
