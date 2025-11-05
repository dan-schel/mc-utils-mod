package com.danschellekens.mc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AfkSystem {
  private static AfkSystem INSTANCE;
  private static final int AFK_TIMEOUT_SECONDS = 300; // 5 minutes
  
  class PlayerStatus {
    boolean isDeclaredAfk;
    double yaw;
    double pitch;
    Instant lastMovementTime;
    String scoreHolderName;

    public PlayerStatus(boolean isDeclaredAfk, double yaw, double pitch, Instant lastMovementTime, String scoreHolderName) {
      this.isDeclaredAfk = isDeclaredAfk;
      this.yaw = yaw;
      this.pitch = pitch;
      this.lastMovementTime = lastMovementTime;
      this.scoreHolderName = scoreHolderName;
    }

    void update(double yaw, double pitch) {
      if (this.yaw == yaw && this.pitch == pitch) {
        return;
      }

      this.yaw = yaw;
      this.pitch = pitch;
      this.lastMovementTime = Instant.now();
    }

    void setDeclaredAfk(boolean isDeclaredAfk, boolean manually) {
      this.isDeclaredAfk = isDeclaredAfk;
      if (manually) {
        this.lastMovementTime = null;
      }
    }

    boolean shouldDeclareAfk() {
      return !isDeclaredAfk && isInactive();
    }

    boolean shouldDeclareActive() {
      return isDeclaredAfk && !isInactive();
    }

    boolean isInactive() {
      return lastMovementTime == null || Instant.now().minusSeconds(AFK_TIMEOUT_SECONDS).isAfter(lastMovementTime);
    }

    String getScoreHolderName() {
      return scoreHolderName;
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

    ArrayList<UUID> playersNoLongerOnline = new ArrayList<>();
    for (UUID playerId : players.keySet()) {
      if (server.getPlayerManager().getPlayer(playerId) == null) {
        playersNoLongerOnline.add(playerId);
      }
    }
    for (UUID playerId : playersNoLongerOnline) {
      removeFromAfkTeam(server, players.get(playerId).getScoreHolderName());
      players.remove(playerId);
    }
  }

  public void manuallyDeclareAfk(ServerPlayerEntity player) {
    PlayerStatus status = getOrCreateStatus(player);
    status.setDeclaredAfk(true, true);
    onPlayerBecomesAfk(player);
  }

  private PlayerStatus getOrCreateStatus(ServerPlayerEntity player) {
    PlayerStatus status = players.get(player.getUuid());
    if (status == null) {
      status = new PlayerStatus(false, player.getYaw(), player.getPitch(), Instant.now(), player.getNameForScoreboard());
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
      status.setDeclaredAfk(true, false);
      onPlayerBecomesAfk(player);
    }
 
    if (status.shouldDeclareActive()) {
      status.setDeclaredAfk(false, false);
      onPlayerBecomesActive(player);
    }
  }

  private void onNewPlayer(ServerPlayerEntity player) {
    getOrCreateStatus(player);
    removeFromAfkTeam(player.getServer(), player.getNameForScoreboard());

    int afkPlayers = getAfkPlayerCount();
    if (afkPlayers >= 1) {
      String quantityText = afkPlayers + (afkPlayers == 1 ? " player is" : " players are");
      player.sendMessageToClient(createBlueCenterText("Welcome! " + quantityText + " currently ", "AFK", " (press TAB)."), false);
    }
  }

  private void onPlayerBecomesAfk(ServerPlayerEntity afkPlayer) {
    MinecraftServer server = afkPlayer.getServer();

    Text firstPerson = createBlueCenterText("You're marked as ", "AFK", ".");
    Text thirdPerson = createBlueCenterText(afkPlayer.getName().getString() + " is ", "AFK", ".");

    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
      player.sendMessageToClient(player.getUuid().equals(afkPlayer.getUuid()) ? firstPerson : thirdPerson, false);
    }

    server.sendMessage(thirdPerson);

    addToAfkTeam(server, afkPlayer.getNameForScoreboard());
  }

  private void onPlayerBecomesActive(ServerPlayerEntity activePlayer) {
    MinecraftServer server = activePlayer.getServer();

    Text firstPerson = createBlueCenterText("You're no longer marked as ", "AFK", ".");
    Text thirdPerson = createBlueCenterText(activePlayer.getName().getString() + " is no longer ", "AFK", ".");

    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
      player.sendMessageToClient(player.getUuid().equals(activePlayer.getUuid()) ? firstPerson : thirdPerson, false);
    }

    server.sendMessage(thirdPerson);

    removeFromAfkTeam(server, activePlayer.getNameForScoreboard());
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

  public static void addToAfkTeam(MinecraftServer server, String scoreHolderName) {
    Team afkTeam = createOrGetScoreboardTeam(server);
    if (!afkTeam.isEqual(server.getScoreboard().getScoreHolderTeam(scoreHolderName))) {
      server.getScoreboard().addScoreHolderToTeam(scoreHolderName, afkTeam);
    }
  }

  public static void removeFromAfkTeam(MinecraftServer server, String scoreHolderName) {
    Team afkTeam = createOrGetScoreboardTeam(server);
    if (afkTeam.isEqual(server.getScoreboard().getScoreHolderTeam(scoreHolderName))) {
      server.getScoreboard().removeScoreHolderFromTeam(scoreHolderName, afkTeam);
    }
  }

  private static Team createOrGetScoreboardTeam(MinecraftServer server) {
    String teamName = DansUtils.MOD_ID_SNAKE_CASE + "_afk";

    if (server.getScoreboard().getTeam(teamName) == null) {
      server.getScoreboard().addTeam(teamName);
      server.getScoreboard().getTeam(teamName).setColor(Formatting.BLUE);
    }

    return server.getScoreboard().getTeam(teamName);
  }

  private static Text createBlueCenterText(String prefix, String blueText, String suffix) {
    return Text.literal(prefix)
      .append(Text.literal(blueText).formatted(Formatting.BLUE))
      .append(Text.literal(suffix));
  }
}
