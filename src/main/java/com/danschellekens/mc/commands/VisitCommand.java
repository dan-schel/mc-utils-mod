package com.danschellekens.mc.commands;

import com.danschellekens.mc.state.WarpLocation;
import com.danschellekens.mc.state.WarpLocationsState;
import com.danschellekens.mc.utils.CommandUtils;
import com.danschellekens.mc.utils.PlayerSuggestionProvider;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class VisitCommand {

  public static LiteralArgumentBuilder<CommandSourceStack> COMMAND =
    Commands.literal("visit").then(
      Commands.argument("who", EntityArgument.player())
        .suggests(new PlayerSuggestionProvider(false))
        .executes(VisitCommand::execute)
    );

  public static int execute(CommandContext<CommandSourceStack> context)
    throws CommandSyntaxException {
    CommandSourceStack source = context.getSource();
    ServerPlayer player = source.getPlayer();

    if (player == null) {
      return CommandUtils.failure(source, "Not executed by a player.");
    }

    ServerPlayer target = EntityArgument.getPlayer(context, "who");

    if (player.getId() == target.getId()) {
      return CommandUtils.failure(source, "You can't visit yourself.");
    }

    WarpLocationsState locations = WarpLocationsState.getServerState(
      source.getServer()
    );
    WarpLocation priorLocation = WarpLocation.fromWorld(
      player.position(),
      player.level()
    );
    locations.savePriorLocation(player.getUUID(), priorLocation);

    ServerLevel world = target.level();
    double x = target.getX();
    double y = target.getY();
    double z = target.getZ();
    float yaw = player.getYRot();
    float pitch = player.getXRot();
    player.teleportTo(world, x, y, z, Set.of(), yaw, pitch, true);

    target.sendSystemMessage(
      Component.literal(player.getName().getString() + " is visiting you."),
      false
    );

    return CommandUtils.successWithUndoCommand(
      source,
      "Visiting " + target.getName().getString() + ".",
      "/unvisit"
    );
  }
}
