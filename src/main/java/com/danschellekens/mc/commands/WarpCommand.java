package com.danschellekens.mc.commands;

import com.danschellekens.mc.commands.warp.WarpAddCommand;
import com.danschellekens.mc.commands.warp.WarpWhereCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class WarpCommand {
  public static LiteralArgumentBuilder<ServerCommandSource> COMMAND = CommandManager
    .literal("warp")
    .then(WarpWhereCommand.COMMAND)
		.then(WarpAddCommand.COMMAND);
}
