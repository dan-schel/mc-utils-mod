package com.danschellekens.mc.commands;

import com.danschellekens.mc.state.WarpLocation;
import com.danschellekens.mc.state.WarpLocationsState;
import com.danschellekens.mc.utils.CommandUtils;
import com.danschellekens.mc.utils.PlayerSuggestionProvider;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.eclipse.jdt.annotation.Nullable;

public class VisitCommand {

  public static LiteralArgumentBuilder<CommandSourceStack> COMMAND =
    Objects.requireNonNull(
      Commands.literal("visit").then(
        Commands.argument("who", EntityArgument.player())
          .suggests(new PlayerSuggestionProvider(false))
          .executes(VisitCommand::execute)
      )
    );

  public static int execute(
    @Nullable CommandContext<CommandSourceStack> context
  ) throws CommandSyntaxException {
    CommandContext<CommandSourceStack> checkedContext = Objects.requireNonNull(
      context
    );
    CommandSourceStack source = Objects.requireNonNull(
      checkedContext.getSource()
    );
    ServerPlayer player = source.getPlayer();

    if (player == null) {
      return CommandUtils.failure(source, "Not executed by a player.");
    }

    ServerPlayer target = EntityArgument.getPlayer(checkedContext, "who");

    if (player.getId() == target.getId()) {
      return CommandUtils.failure(source, "You can't visit yourself.");
    }

    WarpLocationsState locations = WarpLocationsState.getServerState(
      Objects.requireNonNull(source.getServer())
    );
    WarpLocation priorLocation = WarpLocation.fromWorld(
      Objects.requireNonNull(player.blockPosition()),
      Objects.requireNonNull(player.level())
    );
    locations.savePriorLocation(
      Objects.requireNonNull(player.getUUID()),
      priorLocation
    );

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
