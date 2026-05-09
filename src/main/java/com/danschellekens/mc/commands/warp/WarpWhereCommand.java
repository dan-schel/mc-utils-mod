package com.danschellekens.mc.commands.warp;

import com.danschellekens.mc.state.WarpLocation;
import com.danschellekens.mc.state.WarpLocationsState;
import com.danschellekens.mc.utils.CommandUtils;
import com.danschellekens.mc.utils.WarpLocationSuggestionProvider;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class WarpWhereCommand {

  public static RequiredArgumentBuilder<CommandSourceStack, String> COMMAND =
    Commands.argument("where", StringArgumentType.word())
      .suggests(new WarpLocationSuggestionProvider(false))
      .executes(WarpWhereCommand::execute);

  private static int execute(CommandContext<CommandSourceStack> context)
    throws CommandSyntaxException {
    CommandSourceStack source = context.getSource();
    ServerPlayer player = source.getPlayer();

    if (player == null) {
      return CommandUtils.failure(source, "Not executed by a player.");
    }

    String name = StringArgumentType.getString(context, "where");
    WarpLocationsState locations = WarpLocationsState.getServerState(
      source.getServer()
    );
    WarpLocation location = locations.get(player.getUUID(), name);

    if (location == null) {
      return CommandUtils.failure(
        source,
        "Warp point \"" + name + "\" not found."
      );
    }

    WarpLocation priorLocation = WarpLocation.fromWorld(
      player.position(),
      player.level()
    );
    locations.savePriorLocation(player.getUUID(), priorLocation);

    ServerLevel world = source
      .getServer()
      .getLevel(location.getDimension().getWorldRegistryKey());
    double x = location.getPosition().getX() + 0.5;
    double y = location.getPosition().getY();
    double z = location.getPosition().getZ() + 0.5;
    float yaw = player.getYRot();
    float pitch = player.getXRot();
    player.teleportTo(world, x, y, z, Set.of(), yaw, pitch, true);

    return CommandUtils.successWithUndoCommand(
      source,
      "Warped to \"" + name + "\".",
      "/unwarp"
    );
  }
}
