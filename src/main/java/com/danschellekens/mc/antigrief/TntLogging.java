package com.danschellekens.mc.antigrief;

import com.danschellekens.mc.utils.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class TntLogging {

  public static void onEntitySpawned(Entity entity, MinecraftServer server) {
    if (
      entity.getType() != EntityType.TNT &&
      entity.getType() != EntityType.TNT_MINECART
    ) {
      return;
    }

    String nearbyPlayerNames = "";

    for (ServerPlayerEntity player : server
      .getPlayerManager()
      .getPlayerList()) {
      if (!player.getEntityPos().isInRange(entity.getEntityPos(), 20)) {
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

    String entityTypeName = entity.getType().getName().getString();

    ChatUtils.logAndTellOps(
      server,
      ChatUtils.thirdPartyFormattedMessage(
        "Server",
        entityTypeName + " spawned. " + nearbyPlayerNames + " nearby."
      )
    );
  }

  public static void onItemStackSet(PlayerEntity player, ItemStack stack) {
    try {
      World world = player.getEntityWorld();
      if (world == null) {
        return;
      }

      MinecraftServer server = world.getServer();
      if (server == null) {
        return;
      }

      if (
        stack.getItem() != net.minecraft.item.Items.TNT ||
        stack.getItem() == net.minecraft.item.Items.TNT_MINECART
      ) {
        return;
      }

      ChatUtils.logAndTellOps(
        server,
        ChatUtils.thirdPartyFormattedMessage(
          "Server",
          player.getName().getString() +
            " is handling " +
            stack.getName().getString()
        )
      );
    } catch (Exception e) {
      return;
    }
  }
}
