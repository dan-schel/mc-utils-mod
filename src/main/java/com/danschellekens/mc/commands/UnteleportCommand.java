package com.danschellekens.mc.commands;

import com.danschellekens.mc.state.WarpLocation;
import com.danschellekens.mc.state.WarpLocationsState;
import com.danschellekens.mc.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class UnteleportCommand {

  public static LiteralArgumentBuilder<ServerCommandSource> COMMAND_UNWARP =
    CommandManager.literal("unwarp").executes(UnteleportCommand::execute);

  // Another alias for /unwarp. Does the exact same thing.
  public static LiteralArgumentBuilder<ServerCommandSource> COMMAND_UNVISIT =
    CommandManager.literal("unvisit").executes(UnteleportCommand::execute);

  public static int execute(CommandContext<ServerCommandSource> context)
    throws CommandSyntaxException {
    ServerCommandSource source = context.getSource();
    ServerPlayerEntity player = source.getPlayer();

    if (player == null) {
      return CommandUtils.failure(source, "Not executed by a player.");
    }

    WarpLocationsState locations = WarpLocationsState.getServerState(
      source.getServer()
    );
    Optional<WarpLocation> locationOptional = locations.getPriorLocation(
      player.getUuid()
    );

    if (locationOptional.isEmpty()) {
      Text message = Text.literal(
        "No prior location saved. Should be used after a "
      )
        .append(Text.literal("/warp").formatted(Formatting.AQUA))
        .append(" or ")
        .append(Text.literal("/visit").formatted(Formatting.AQUA))
        .append(".");

      return CommandUtils.failure(source, message);
    }

    WarpLocation location = locationOptional.orElseThrow();
    ServerWorld world = source
      .getServer()
      .getWorld(location.getDimension().getWorldRegistryKey());
    double x = location.getPosition().getX() + 0.5;
    double y = location.getPosition().getY();
    double z = location.getPosition().getZ() + 0.5;
    float yaw = player.getYaw();
    float pitch = player.getPitch();
    player.teleport(world, x, y, z, Set.of(), yaw, pitch, true);

    locations.deletePriorLocation(player.getUuid());

    return CommandUtils.success(source, "Returned to prior location.");
  }
}
