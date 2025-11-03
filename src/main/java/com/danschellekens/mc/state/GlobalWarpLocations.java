package com.danschellekens.mc.state;

import java.util.HashMap;
import java.util.Map.Entry;

import com.danschellekens.mc.DansUtils;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public class GlobalWarpLocations extends PersistentState {
  private HashMap<String, WarpLocation> locations;

  private GlobalWarpLocations() {
    this.locations = new HashMap<>();
  }
 
  private GlobalWarpLocations(WarpLocation[] locations) {
    this.locations = new HashMap<>();
    for (WarpLocation location : locations) {
      this.locations.put(location.name, location);
    }
  }

  public WarpLocation get(String name) {
    return this.locations.get(name);
  }

  public String[] getAllNames() {
    String[] names = new String[this.locations.size()];
    int i = 0;

    for (Entry<String, WarpLocation> entry : this.locations.entrySet()) {
      names[i] = entry.getKey();
      i++;
    }

    return names;
  }

  public void add(WarpLocation location) {
    this.locations.put(location.name, location);
    this.markDirty();
  }

  public boolean hasWithName(String name) {
    return this.locations.containsKey(name);
  }

  public void remove(String name) {
    this.locations.remove(name);
    this.markDirty();
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
    NbtList list = new NbtList();
    for (WarpLocation location : this.locations.values()) {
      list.add(location.toNbt());
    }
    nbt.put("Locations", list);
    return nbt;
  }

  public static GlobalWarpLocations fromNbt(NbtCompound nbt) {
    NbtList list = nbt.getList("Locations", 10); // 10 = NbtCompound
    WarpLocation[] locations = new WarpLocation[list.size()];
    for (int i = 0; i < list.size(); i++) {
      NbtCompound locationNbt = list.getCompound(i);
      locations[i] = WarpLocation.fromNbt(locationNbt);
    }
    return new GlobalWarpLocations(locations);
  }

  public static PersistentState.Type<GlobalWarpLocations> type = new PersistentState.Type<GlobalWarpLocations>(
    () -> new GlobalWarpLocations(), 
    (nbt, registryLookup) -> GlobalWarpLocations.fromNbt(nbt), 
    null
  );
 
  public static GlobalWarpLocations getServerState(MinecraftServer server) {
    return server
      .getWorld(World.OVERWORLD)
      .getPersistentStateManager()
      .getOrCreate(type, DansUtils.MOD_ID + "_global_warp_locations");
  }
}
