package com.danschellekens.mc.state;

import java.util.HashMap;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import org.eclipse.jdt.annotation.Nullable;

public class WarpLocationCollection {

  private HashMap<String, WarpLocation> locations;

  public WarpLocationCollection(HashMap<String, WarpLocation> locations) {
    this.locations = locations;
  }

  public String[] keys() {
    return Objects.requireNonNull(
      this.locations.keySet().toArray(new String[0])
    );
  }

  public @Nullable WarpLocation get(String name) {
    return this.locations.get(name);
  }

  public boolean contains(String name) {
    return this.locations.containsKey(name);
  }

  public void add(String name, WarpLocation location) {
    this.locations.put(name, location);
  }

  public void remove(String name) {
    this.locations.remove(name);
  }

  public int size() {
    return this.locations.size();
  }

  public CompoundTag toNbt() {
    CompoundTag nbt = new CompoundTag();
    for (String name : this.locations.keySet()) {
      nbt.put(name, Objects.requireNonNull(this.locations.get(name)).toNbt());
    }
    return nbt;
  }

  public static WarpLocationCollection fromNbt(CompoundTag nbt) {
    HashMap<String, WarpLocation> locations = new HashMap<>();
    for (String name : nbt.keySet()) {
      locations.put(
        name,
        WarpLocation.fromNbt(
          Objects.requireNonNull(nbt.getCompound(name).orElseThrow())
        )
      );
    }
    return new WarpLocationCollection(locations);
  }
}
