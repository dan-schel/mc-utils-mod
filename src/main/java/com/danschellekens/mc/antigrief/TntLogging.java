package com.danschellekens.mc.antigrief;

import com.danschellekens.mc.state.WarpLocationDimension;
import com.danschellekens.mc.utils.ChatUtils;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TntLogging {

  private static final int SUSPICIOUS_RADIUS = 20;
  private static final long TNT_LOG_COOLDOWN_MS = 60 * 60 * 1000; // 1 hour

  private static final HashMap<UUID, Long> lastTntLogTimeByPlayer =
    new HashMap<>();

  public static void onEntitySpawned(Entity entity, MinecraftServer server) {
    try {
      if (
        entity.getType() != EntityType.TNT &&
        entity.getType() != EntityType.TNT_MINECART
      ) {
        return;
      }

      String nearbyPlayerNames = "";

      for (ServerPlayer player : server.getPlayerList().getPlayers()) {
        if (
          !player.position().closerThan(entity.position(), SUSPICIOUS_RADIUS)
        ) {
          continue;
        }

        if (!nearbyPlayerNames.isEmpty()) {
          nearbyPlayerNames += ", ";
        }
        nearbyPlayerNames += player.getName().getString();
      }

      if (nearbyPlayerNames.isEmpty()) {
        nearbyPlayerNames = "No-one";
      }

      String entityTypeName = entity.getType().getDescription().getString();
      String entityLocation =
        entity.getBlockX() +
        ", " +
        entity.getBlockY() +
        ", " +
        entity.getBlockZ() +
        " in " +
        WarpLocationDimension.fromWorldRegistryKey(
          entity.level().dimension()
        ).getDisplayString();

      ChatUtils.logAndTellOps(
        server,
        ChatUtils.thirdPartyFormattedMessage(
          "Server",
          entityTypeName +
            " spawned at " +
            entityLocation +
            ". " +
            nearbyPlayerNames +
            " nearby."
        )
      );
    } catch (Exception e) {
      return;
    }
  }

  public static void onItemStackSet(Player player, ItemStack stack) {
    try {
      Level world = player.level();
      if (world == null) {
        return;
      }

      MinecraftServer server = world.getServer();
      if (server == null) {
        return;
      }

      if (
        stack.getItem() != net.minecraft.world.item.Items.TNT &&
        stack.getItem() != net.minecraft.world.item.Items.TNT_MINECART
      ) {
        return;
      }

      // Skip logging if we've already logged for this player in the last hour.
      long now = System.currentTimeMillis();
      Long lastLogTime = lastTntLogTimeByPlayer.get(player.getUUID());
      if (lastLogTime != null && (now - lastLogTime) < TNT_LOG_COOLDOWN_MS) {
        return;
      }
      lastTntLogTimeByPlayer.put(player.getUUID(), now);

      ChatUtils.logAndTellOps(
        server,
        ChatUtils.thirdPartyFormattedMessage(
          "Server",
          player.getName().getString() +
            " is handling " +
            stack.getHoverName().getString()
        )
      );
    } catch (Exception e) {
      return;
    }
  }
}
