package com.danschellekens.mc;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class AfkSystem {
  private static AfkSystem INSTANCE;
  private static final int AFK_TIMEOUT_SECONDS = 10; // 5 minutes
  
  class PlayerStatus {
    boolean isDeclaredAfk;
    double yaw;
    double pitch;
    Instant lastMovementTime;

    public PlayerStatus(boolean isDeclaredAfk, double yaw, double pitch, Instant lastMovementTime) {
      this.isDeclaredAfk = isDeclaredAfk;
      this.yaw = yaw;
      this.pitch = pitch;
      this.lastMovementTime = lastMovementTime;
    }

    void update(double yaw, double pitch) {
      if (this.yaw == yaw && this.pitch == pitch) {
        return;
      }

      this.yaw = yaw;
      this.pitch = pitch;
      this.lastMovementTime = Instant.now();
    }

    void setDeclaredAfk(boolean isDeclaredAfk) {
      this.isDeclaredAfk = isDeclaredAfk;
    }

    boolean shouldDeclareAfk() {
      return !isDeclaredAfk && isInactive();
    }

    boolean shouldDeclareActive() {
      return isDeclaredAfk && !isInactive();
    }

    boolean isInactive() {
      return Instant.now().minusSeconds(AFK_TIMEOUT_SECONDS).isAfter(lastMovementTime);
    }
  }

  private HashMap<UUID, PlayerStatus> players;

  private AfkSystem() {
    this.players = new HashMap<>();
  }

  public static AfkSystem getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new AfkSystem();
    }
    return INSTANCE;
  }

  public void onTick(MinecraftServer server) {
    // Run every 10 ticks (0.5 seconds).
    if (server.getTicks() % 10 != 0) {
      return;
    }

    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
      if (!players.containsKey(player.getUuid())) {
        onNewPlayer(player);
      }
      else {
        updatePlayerStatus(player);
      }
    }

    // TODO: Remove players from the map when they leave.
  }

  public void manuallyDeclareAfk(ServerPlayerEntity player) {
    onPlayerBecomesAfk(player);
  }

  private PlayerStatus getOrCreateStatus(ServerPlayerEntity player) {
    PlayerStatus status = players.get(player.getUuid());
    if (status == null) {
      status = new PlayerStatus(false, player.getYaw(), player.getPitch(), Instant.now());
      players.put(player.getUuid(), status);
    }
    return status;
  }

  private void updatePlayerStatus(ServerPlayerEntity player) {
    double currentYaw = player.getYaw();
    double currentPitch = player.getPitch();

    PlayerStatus status = getOrCreateStatus(player);
    status.update(currentYaw, currentPitch);

    if (status.shouldDeclareAfk()) {
      onPlayerBecomesAfk(player);
    }
 
    if (status.shouldDeclareActive()) {
      onPlayerBecomesActive(player);
    }
  }

  private void onNewPlayer(ServerPlayerEntity player) {
    getOrCreateStatus(player);

    int afkPlayers = getAfkPlayerCount();
    if (afkPlayers >= 1) {
      String message = "Welcome " + player.getName().getString() + ". " + afkPlayers + (afkPlayers == 1 ? " player" : " players") + " are currently AFK (press TAB).";

      // TODO: Haven't tested, but this probably doesn't work.
      player.sendMessage(Text.literal(message));
    }
  }

  private void onPlayerBecomesAfk(ServerPlayerEntity player) {
    PlayerStatus status = getOrCreateStatus(player);
    status.setDeclaredAfk(true);

    // TODO: This doesn't appear in chat like I want it to.
    player.getServer().sendMessage(Text.literal(player.getName().getString() + " is AFK."));

    // TODO: Need to assign them to a scoreboard team so it becomes blue.
    // player.getServer().getScoreboard().getTeam("dfgdfg").getPlayerList()
  }

  private void onPlayerBecomesActive(ServerPlayerEntity player) {
    PlayerStatus status = getOrCreateStatus(player);
    status.setDeclaredAfk(false);

    // TODO: This doesn't appear in chat like I want it to.
    player.getServer().sendMessage(Text.literal(player.getName().getString() + " is no longer AFK."));

    // TODO: Need to remove them from blue team.
  }

  private int getAfkPlayerCount() {
    int count = 0;
    for (PlayerStatus status : players.values()) {
      if (status.isDeclaredAfk) {
        count++;
      }
    }
    return count;
  }
}
