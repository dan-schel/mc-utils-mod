package com.danschellekens.mc.antigrief;

import com.danschellekens.mc.utils.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

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
        "TNT Logging",
        entityTypeName + " spawned. " + nearbyPlayerNames + " nearby."
      )
    );
  }
}
