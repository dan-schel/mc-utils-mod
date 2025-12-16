package com.danschellekens.mc.state;

import java.util.HashMap;

import net.minecraft.nbt.NbtCompound;

public class WarpLocationCollection {
  private HashMap<String, WarpLocation> locations;

  public WarpLocationCollection(HashMap<String, WarpLocation> locations) {
    this.locations = locations;
  }

  public String[] keys() {
    return this.locations.keySet().toArray(new String[0]);
  }

  public WarpLocation get(String name) {
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

  public NbtCompound toNbt() {
    NbtCompound nbt = new NbtCompound();
    for (String name : this.locations.keySet()) {
      nbt.put(name, this.locations.get(name).toNbt());
    }
    return nbt;
  }

  public static WarpLocationCollection fromNbt(NbtCompound nbt) {
    HashMap<String, WarpLocation> locations = new HashMap<>();
    for (String name : nbt.getKeys()) {
      locations.put(name, WarpLocation.fromNbt(nbt.getCompound(name).orElseThrow()));
    }
    return new WarpLocationCollection(locations);
  }
}
