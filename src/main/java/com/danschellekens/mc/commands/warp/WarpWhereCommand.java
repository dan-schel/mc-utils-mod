package com.danschellekens.mc.commands.warp;

import com.danschellekens.mc.state.WarpLocation;
import com.danschellekens.mc.state.WarpLocationsState;
import com.danschellekens.mc.utils.CommandUtils;
import com.danschellekens.mc.utils.WarpLocationSuggestionProvider;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.eclipse.jdt.annotation.Nullable;

public class WarpWhereCommand {

  public static RequiredArgumentBuilder<CommandSourceStack, String> COMMAND =
    Objects.requireNonNull(
      Commands.argument("where", StringArgumentType.word())
        .suggests(new WarpLocationSuggestionProvider(false))
        .executes(WarpWhereCommand::execute)
    );

  private static int execute(
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

    String name = Objects.requireNonNull(
      StringArgumentType.getString(checkedContext, "where")
    );
    WarpLocationsState locations = WarpLocationsState.getServerState(
      Objects.requireNonNull(source.getServer())
    );
    WarpLocation location = locations.get(
      Objects.requireNonNull(player.getUUID()),
      name
    );

    if (location == null) {
      return CommandUtils.failure(
        source,
        "Warp point \"" + name + "\" not found."
      );
    }

    WarpLocation priorLocation = WarpLocation.fromWorld(
      Objects.requireNonNull(player.blockPosition()),
      Objects.requireNonNull(player.level())
    );
    locations.savePriorLocation(
      Objects.requireNonNull(player.getUUID()),
      priorLocation
    );

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
