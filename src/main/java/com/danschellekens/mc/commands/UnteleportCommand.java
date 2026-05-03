package com.danschellekens.mc.commands;

import com.danschellekens.mc.state.WarpLocation;
import com.danschellekens.mc.state.WarpLocationsState;
import com.danschellekens.mc.utils.CommandUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.eclipse.jdt.annotation.Nullable;

public class UnteleportCommand {

  public static LiteralArgumentBuilder<CommandSourceStack> COMMAND_UNWARP =
    Objects.requireNonNull(
      Commands.literal("unwarp").executes(UnteleportCommand::execute)
    );

  // Another alias for /unwarp. Does the exact same thing.
  public static LiteralArgumentBuilder<CommandSourceStack> COMMAND_UNVISIT =
    Objects.requireNonNull(
      Commands.literal("unvisit").executes(UnteleportCommand::execute)
    );

  public static int execute(
    @Nullable CommandContext<CommandSourceStack> context
  ) throws CommandSyntaxException {
    CommandSourceStack source = Objects.requireNonNull(
      Objects.requireNonNull(context).getSource()
    );
    ServerPlayer player = source.getPlayer();

    if (player == null) {
      return CommandUtils.failure(source, "Not executed by a player.");
    }

    WarpLocationsState locations = WarpLocationsState.getServerState(
      Objects.requireNonNull(source.getServer())
    );
    Optional<WarpLocation> locationOptional = locations.getPriorLocation(
      Objects.requireNonNull(player.getUUID())
    );

    if (locationOptional.isEmpty()) {
      Component message = Objects.requireNonNull(
        Component.literal("No prior location saved. Should be used after a ")
          .append(Component.literal("/warp").withStyle(ChatFormatting.AQUA))
          .append(" or ")
          .append(Component.literal("/visit").withStyle(ChatFormatting.AQUA))
          .append(".")
      );

      return CommandUtils.failure(source, message);
    }

    WarpLocation location = locationOptional.orElseThrow();
    ServerLevel world = Objects.requireNonNull(
      Objects.requireNonNull(source.getServer()).getLevel(
        location.getDimension().getWorldRegistryKey()
      )
    );
    double x = location.getPosition().getX() + 0.5;
    double y = location.getPosition().getY();
    double z = location.getPosition().getZ() + 0.5;
    float yaw = player.getYRot();
    float pitch = player.getXRot();
    player.teleportTo(world, x, y, z, Set.of(), yaw, pitch, true);

    locations.deletePriorLocation(Objects.requireNonNull(player.getUUID()));

    return CommandUtils.success(source, "Returned to prior location.");
  }
}
