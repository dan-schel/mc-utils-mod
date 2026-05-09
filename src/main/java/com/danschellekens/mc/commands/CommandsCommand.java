package com.danschellekens.mc.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class CommandsCommand {

  private static final String[] COMMAND_LINES = {
    "Custom commands:",
    "- {/visit <username>} - Teleport to another player",
    "- {/warp <place>} - Teleport to predefined location",
    "- {/warp add <place>} - Save warp point",
    "- {/unwarp}, {/unvisit} - Teleport back after {/warp} or {/visit}",
    "- {/daytime}, {/nighttime} - Change time of day",
    "- {/sunshine}, {/rain}, {/thunder} - Change weather",
    "- {/afk} - Manually declare yourself AFK",
  };

  public static LiteralArgumentBuilder<CommandSourceStack> COMMAND =
    Commands.literal("commands").executes(CommandsCommand::execute);

  public static int execute(CommandContext<CommandSourceStack> context)
    throws CommandSyntaxException {
    CommandSourceStack source = context.getSource();

    source.sendSystemMessage(commandListMessage());

    return 1;
  }

  private static Component commandListMessage() {
    MutableComponent message = Component.empty();

    for (int i = 0; i < COMMAND_LINES.length; i++) {
      String line = COMMAND_LINES[i];

      if (i != 0) {
        message.append(Component.literal("\n"));
      }

      message.append(formatCommandLine(line));
    }

    return message;
  }

  private static Component formatCommandLine(String line) {
    MutableComponent formatted = Component.empty();
    int currentIndex = 0;

    while (currentIndex < line.length()) {
      int commandStart = line.indexOf("{", currentIndex);
      if (commandStart < 0) {
        formatted.append(Component.literal(line.substring(currentIndex)));
        break;
      }

      if (commandStart > currentIndex) {
        formatted.append(
          Component.literal(line.substring(currentIndex, commandStart))
        );
      }

      int commandEnd = line.indexOf('}', commandStart);
      if (commandEnd < 0) {
        formatted.append(Component.literal(line.substring(commandStart)));
        break;
      }

      formatted.append(
        commandText(line.substring(commandStart + 1, commandEnd))
      );
      currentIndex = commandEnd + 1;
    }

    return formatted;
  }

  private static Component commandText(String command) {
    return Component.literal(command).withStyle(ChatFormatting.AQUA);
  }
}
