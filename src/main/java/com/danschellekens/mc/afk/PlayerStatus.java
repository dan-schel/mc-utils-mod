package com.danschellekens.mc.afk;

import java.time.Instant;

public class PlayerStatus {
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
    return lastMovementTime == null || Instant.now().minusSeconds(AfkSystem.AFK_TIMEOUT_SECONDS).isAfter(lastMovementTime);
  }

  String getScoreHolderName() {
    return scoreHolderName;
  }
}
