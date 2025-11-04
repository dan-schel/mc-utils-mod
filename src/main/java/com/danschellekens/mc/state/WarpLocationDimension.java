package com.danschellekens.mc.state;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

public enum WarpLocationDimension {
  THE_OVERWORLD("The Overworld", World.OVERWORLD),
  THE_NETHER("The Nether", World.NETHER),
  THE_END("The End", World.END);

  private final String displayString;
  private final RegistryKey<World> worldRegistryKey;  

  private WarpLocationDimension(String displayString, RegistryKey<World> worldRegistryKey) {
    this.displayString = displayString;
    this.worldRegistryKey = worldRegistryKey;
  }

  public String getDisplayString() {
    return this.displayString;
  }

  public RegistryKey<World> getWorldRegistryKey() {
    return this.worldRegistryKey;
  }

  public static WarpLocationDimension fromWorldRegistryKey(RegistryKey<World> worldRegistryKey) {
    for (WarpLocationDimension d : WarpLocationDimension.values()) {
      if (d.getWorldRegistryKey() == worldRegistryKey) {
        return d;
      }
    }
    throw new RuntimeException("Unknown world registry key: " + worldRegistryKey.toString());
  }
}
