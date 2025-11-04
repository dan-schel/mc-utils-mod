package com.danschellekens.mc.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;

import com.danschellekens.mc.DansUtils;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public class WarpLocationsState extends PersistentState {
  public static final int MAX_WARP_LOCATIONS_PER_PLAYER = 5;

  public enum AddResult {
    SUCCESS,
    CLASHES_WITH_GLOBAL,
    REQUIRES_OP,
    ALREADY_REACHED_MAXIMUM
  }

  public enum RemoveResult {
    REMOVED_GLOBAL,
    REMOVED_PLAYER_SPECIFIC,
    NOT_FOUND,
    REQUIRES_OP
  }

  private WarpLocationCollection global;
  private HashMap<UUID, WarpLocationCollection> playerSpecific;

  private WarpLocationsState() {
    this.global = new WarpLocationCollection(new HashMap<>());
    this.playerSpecific = new HashMap<>();
  }
 
  private WarpLocationsState(WarpLocationCollection global, HashMap<UUID, WarpLocationCollection> playerSpecific) {
    this.global = global;
    this.playerSpecific = playerSpecific;
  }

  public AddResult add(UUID playerUUID, String name, WarpLocation location, boolean global, boolean isPlayerOp) {
    if (global) {
      if (isPlayerOp) {
        this.global.add(name, location);
        for (WarpLocationCollection collection : this.playerSpecific.values()) {
          collection.remove(name);
        }
        this.markDirty();
        
        return AddResult.SUCCESS;
      }
      else {
        return AddResult.REQUIRES_OP;
      }
    } 
    else {
      if (this.global.contains(name)) {
        return AddResult.CLASHES_WITH_GLOBAL;
      }

      WarpLocationCollection collection = this.playerSpecific.get(playerUUID);
      if (collection == null) {
        collection = new WarpLocationCollection(new HashMap<>());
        this.playerSpecific.put(playerUUID, collection);
      }
      
      if (collection.size() >= MAX_WARP_LOCATIONS_PER_PLAYER && !collection.contains(name)) {
        return AddResult.ALREADY_REACHED_MAXIMUM;
      }
      
      collection.add(name, location);
      this.markDirty();

      return AddResult.SUCCESS;
    }
  }

  public RemoveResult remove(UUID playerUUID, String name, boolean isPlayerOp) {
    if (this.global.contains(name)) {
      if (isPlayerOp) {
        this.global.remove(name);
        this.markDirty();
        return RemoveResult.REMOVED_GLOBAL;
      }
      else {
        return RemoveResult.REQUIRES_OP;
      }
    }

    WarpLocationCollection collection = this.playerSpecific.get(playerUUID);
    if (collection != null && collection.contains(name)) {
      collection.remove(name);
      this.markDirty();
      return RemoveResult.REMOVED_PLAYER_SPECIFIC;
    }

    return RemoveResult.NOT_FOUND;
  }

  public WarpLocation get(UUID playerUUID, String name) {
    if (this.global.contains(name)) {
      return this.global.get(name);
    }

    WarpLocationCollection collection = this.playerSpecific.get(playerUUID);
    if (collection != null && collection.contains(name)) {
      return collection.get(name);
    }

    return null;
  }

  public String[] getPossibleWarps(UUID playerUUID) {
    ArrayList<String> result = new ArrayList<>();

    for (String key : this.global.keys()) {
      result.add(key);
    }

    WarpLocationCollection collection = this.playerSpecific.get(playerUUID);
    if (collection != null) {
      for (String key : collection.keys()) {
        if (result.contains(key)) {
          continue;
        }
        result.add(key);
      }
    }

    return result.toArray(new String[0]);
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
    nbt.put("Global", this.global.toNbt());
    
    NbtCompound playerSpecificNbt = new NbtCompound();
    for (Entry<UUID, WarpLocationCollection> entry : this.playerSpecific.entrySet()) {
      playerSpecificNbt.put(entry.getKey().toString(), entry.getValue().toNbt());
    }
    nbt.put("PlayerSpecific", playerSpecificNbt);

    return nbt;
  }

  public static WarpLocationsState fromNbt(NbtCompound nbt) {
    WarpLocationCollection global = WarpLocationCollection.fromNbt(nbt.getCompound("Global"));

    HashMap<UUID, WarpLocationCollection> playerSpecific = new HashMap<>();
    NbtCompound playerSpecificNbt = nbt.getCompound("PlayerSpecific");
    for (String key : playerSpecificNbt.getKeys()) {
      playerSpecific.put(UUID.fromString(key), WarpLocationCollection.fromNbt(playerSpecificNbt.getCompound(key)));
    }

    return new WarpLocationsState(global, playerSpecific);
  }

  public static PersistentState.Type<WarpLocationsState> type = new PersistentState.Type<WarpLocationsState>(
    () -> new WarpLocationsState(), 
    (nbt, registryLookup) -> WarpLocationsState.fromNbt(nbt), 
    null
  );
 
  public static WarpLocationsState getServerState(MinecraftServer server) {
    return server
      .getWorld(World.OVERWORLD)
      .getPersistentStateManager()
      .getOrCreate(type, DansUtils.MOD_ID + "_warp_locations");
  }
}
