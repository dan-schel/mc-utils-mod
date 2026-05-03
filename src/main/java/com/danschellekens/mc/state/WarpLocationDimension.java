package com.danschellekens.mc.state;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public enum WarpLocationDimension {
  THE_OVERWORLD("The Overworld", Level.OVERWORLD),
  THE_NETHER("The Nether", Level.NETHER),
  THE_END("The End", Level.END);

  private final String displayString;
  private final ResourceKey<Level> worldRegistryKey;

  private WarpLocationDimension(
    String displayString,
    ResourceKey<Level> worldRegistryKey
  ) {
    this.displayString = displayString;
    this.worldRegistryKey = worldRegistryKey;
  }

  public String getDisplayString() {
    return this.displayString;
  }

  public ResourceKey<Level> getWorldRegistryKey() {
    return this.worldRegistryKey;
  }

  public static WarpLocationDimension fromWorldRegistryKey(
    ResourceKey<Level> worldRegistryKey
  ) {
    for (WarpLocationDimension d : WarpLocationDimension.values()) {
      if (d.getWorldRegistryKey() == worldRegistryKey) {
        return d;
      }
    }
    throw new RuntimeException(
      "Unknown world registry key: " + worldRegistryKey.toString()
    );
  }
}
