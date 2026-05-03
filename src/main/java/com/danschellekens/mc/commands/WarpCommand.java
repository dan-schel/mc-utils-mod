package com.danschellekens.mc.commands;

import com.danschellekens.mc.commands.warp.WarpAddCommand;
import com.danschellekens.mc.commands.warp.WarpRemoveCommand;
import com.danschellekens.mc.commands.warp.WarpWhereCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class WarpCommand {

  public static LiteralArgumentBuilder<CommandSourceStack> COMMAND =
    Commands.literal("warp")
      .then(WarpWhereCommand.COMMAND)
      .then(WarpAddCommand.COMMAND)
      .then(WarpRemoveCommand.COMMAND);
}
