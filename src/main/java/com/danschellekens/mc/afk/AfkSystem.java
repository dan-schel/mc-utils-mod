package com.danschellekens.mc.afk;

import com.danschellekens.mc.DansUtils;
import com.danschellekens.mc.utils.ChatUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;

public class AfkSystem {

  private static AfkSystem INSTANCE;
  public static final int AFK_TIMEOUT_SECONDS = 300; // 5 minutes

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
    if (server.getTickCount() % 10 != 0) {
      return;
    }

    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      if (!players.containsKey(player.getUUID())) {
        onNewPlayer(player);
      } else {
        updatePlayerStatus(player);
      }
    }

    ArrayList<UUID> playersNoLongerOnline = new ArrayList<>();
    for (UUID playerId : players.keySet()) {
      if (server.getPlayerList().getPlayer(playerId) == null) {
        playersNoLongerOnline.add(playerId);
      }
    }
    for (UUID playerId : playersNoLongerOnline) {
      removeFromAfkTeam(server, players.get(playerId).getScoreHolderName());
      players.remove(playerId);
    }
  }

  public void manuallyDeclareAfk(ServerPlayer player) {
    PlayerStatus status = getOrCreateStatus(player);
    status.setDeclaredAfk(true, true);
    onPlayerBecomesAfk(player);
  }

  private PlayerStatus getOrCreateStatus(ServerPlayer player) {
    PlayerStatus status = players.get(player.getUUID());
    if (status == null) {
      status = new PlayerStatus(
        false,
        player.getYRot(),
        player.getXRot(),
        Instant.now(),
        player.getScoreboardName()
      );
      players.put(player.getUUID(), status);
    }
    return status;
  }

  private void updatePlayerStatus(ServerPlayer player) {
    double currentYaw = player.getYRot();
    double currentPitch = player.getXRot();

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

  private void onNewPlayer(ServerPlayer player) {
    getOrCreateStatus(player);
    removeFromAfkTeam(player.level().getServer(), player.getScoreboardName());

    int afkPlayers = getAfkPlayerCount();
    if (afkPlayers >= 1) {
      String quantityText =
        afkPlayers + (afkPlayers == 1 ? " player is" : " players are");
      player.sendSystemMessage(
        ChatUtils.highlightBracedText(
          "Welcome! " + quantityText + " currently {AFK} (press TAB).",
          ChatFormatting.BLUE
        ),
        false
      );
    }
  }

  private void onPlayerBecomesAfk(ServerPlayer afkPlayer) {
    MinecraftServer server = afkPlayer.level().getServer();

    Component firstPerson = ChatUtils.highlightBracedText(
      "You're marked as {AFK}.",
      ChatFormatting.BLUE
    );
    Component thirdPerson = ChatUtils.highlightBracedText(
      afkPlayer.getName().getString() + " is {AFK}.",
      ChatFormatting.BLUE
    );

    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      player.sendSystemMessage(
        player.getUUID().equals(afkPlayer.getUUID())
          ? firstPerson
          : thirdPerson,
        false
      );
    }

    server.sendSystemMessage(thirdPerson);

    addToAfkTeam(server, afkPlayer.getScoreboardName());
  }

  private void onPlayerBecomesActive(ServerPlayer activePlayer) {
    MinecraftServer server = activePlayer.level().getServer();

    Component firstPerson = ChatUtils.highlightBracedText(
      "You're no longer marked as {AFK}.",
      ChatFormatting.BLUE
    );
    Component thirdPerson = ChatUtils.highlightBracedText(
      activePlayer.getName().getString() + " is no longer {AFK}.",
      ChatFormatting.BLUE
    );

    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
      player.sendSystemMessage(
        player.getUUID().equals(activePlayer.getUUID())
          ? firstPerson
          : thirdPerson,
        false
      );
    }

    server.sendSystemMessage(thirdPerson);

    removeFromAfkTeam(server, activePlayer.getScoreboardName());
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

  private static void addToAfkTeam(
    MinecraftServer server,
    String scoreHolderName
  ) {
    PlayerTeam afkTeam = createOrGetScoreboardTeam(server);
    if (
      !afkTeam.isAlliedTo(
        server.getScoreboard().getPlayersTeam(scoreHolderName)
      )
    ) {
      server.getScoreboard().addPlayerToTeam(scoreHolderName, afkTeam);
    }
  }

  private static void removeFromAfkTeam(
    MinecraftServer server,
    String scoreHolderName
  ) {
    PlayerTeam afkTeam = createOrGetScoreboardTeam(server);
    if (
      afkTeam.isAlliedTo(server.getScoreboard().getPlayersTeam(scoreHolderName))
    ) {
      server.getScoreboard().removePlayerFromTeam(scoreHolderName, afkTeam);
    }
  }

  private static PlayerTeam createOrGetScoreboardTeam(MinecraftServer server) {
    String teamName = DansUtils.MOD_ID_SNAKE_CASE + "_afk";

    if (server.getScoreboard().getPlayerTeam(teamName) == null) {
      server
        .getScoreboard()
        .addPlayerTeam(teamName)
        .setColor(ChatFormatting.BLUE);
    }

    return server.getScoreboard().getPlayerTeam(teamName);
  }
}
