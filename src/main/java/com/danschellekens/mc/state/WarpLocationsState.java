package com.danschellekens.mc.state;

import com.danschellekens.mc.DansUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.eclipse.jdt.annotation.Nullable;

public class WarpLocationsState extends SavedData {

  public static final int MAX_WARP_LOCATIONS_PER_PLAYER = 20;

  public enum AddResult {
    ADDED_NEW,
    UPDATED_EXISTING,
    CLASHES_WITH_GLOBAL,
    REQUIRES_OP,
    ALREADY_REACHED_MAXIMUM,
  }

  public enum RemoveResult {
    REMOVED_GLOBAL,
    REMOVED_PLAYER_SPECIFIC,
    NOT_FOUND,
    REQUIRES_OP,
  }

  private WarpLocationCollection global;
  private HashMap<UUID, WarpLocationCollection> playerSpecific;

  // Used for the /unvisit and /unwarp commands.
  private HashMap<UUID, WarpLocation> playerPriorLocations;

  private WarpLocationsState() {
    this.global = new WarpLocationCollection(new HashMap<>());
    this.playerSpecific = new HashMap<>();
    this.playerPriorLocations = new HashMap<>();
  }

  private WarpLocationsState(
    WarpLocationCollection global,
    HashMap<UUID, WarpLocationCollection> playerSpecific,
    HashMap<UUID, WarpLocation> playerPriorLocations
  ) {
    this.global = global;
    this.playerSpecific = playerSpecific;
    this.playerPriorLocations = playerPriorLocations;
  }

  public AddResult add(
    UUID playerUUID,
    String name,
    WarpLocation location,
    boolean global,
    boolean isPlayerOp
  ) {
    if (global) {
      if (isPlayerOp) {
        boolean alreadyExisted = this.global.contains(name);

        this.global.add(name, location);
        for (WarpLocationCollection collection : this.playerSpecific.values()) {
          collection.remove(name);
        }
        this.setDirty();

        return alreadyExisted
          ? AddResult.UPDATED_EXISTING
          : AddResult.ADDED_NEW;
      } else {
        return AddResult.REQUIRES_OP;
      }
    } else {
      if (this.global.contains(name)) {
        return AddResult.CLASHES_WITH_GLOBAL;
      }

      WarpLocationCollection collection = this.playerSpecific.get(playerUUID);
      if (collection == null) {
        collection = new WarpLocationCollection(new HashMap<>());
        this.playerSpecific.put(playerUUID, collection);
      }

      boolean alreadyExisted = collection.contains(name);

      if (
        collection.size() >= MAX_WARP_LOCATIONS_PER_PLAYER && !alreadyExisted
      ) {
        return AddResult.ALREADY_REACHED_MAXIMUM;
      }

      collection.add(name, location);
      this.setDirty();

      return alreadyExisted ? AddResult.UPDATED_EXISTING : AddResult.ADDED_NEW;
    }
  }

  public RemoveResult remove(UUID playerUUID, String name, boolean isPlayerOp) {
    if (this.global.contains(name)) {
      if (isPlayerOp) {
        this.global.remove(name);
        this.setDirty();
        return RemoveResult.REMOVED_GLOBAL;
      } else {
        return RemoveResult.REQUIRES_OP;
      }
    }

    WarpLocationCollection collection = this.playerSpecific.get(playerUUID);
    if (collection != null && collection.contains(name)) {
      collection.remove(name);
      this.setDirty();
      return RemoveResult.REMOVED_PLAYER_SPECIFIC;
    }

    return RemoveResult.NOT_FOUND;
  }

  public @Nullable WarpLocation get(UUID playerUUID, String name) {
    if (this.global.contains(name)) {
      return this.global.get(name);
    }

    WarpLocationCollection collection = this.playerSpecific.get(playerUUID);
    if (collection != null && collection.contains(name)) {
      return collection.get(name);
    }

    return null;
  }

  public String[] getPossibleWarps(UUID playerUUID, boolean includeGlobal) {
    ArrayList<String> result = new ArrayList<>();

    if (includeGlobal) {
      for (String key : this.global.keys()) {
        result.add(key);
      }
    }

    WarpLocationCollection collection = this.playerSpecific.get(playerUUID);
    if (collection != null) {
      for (String key : collection.keys()) {
        if (this.global.contains(Objects.requireNonNull(key))) {
          continue;
        }
        result.add(key);
      }
    }

    String[] array = Objects.requireNonNull(new String[result.size()]);
    for (int i = 0; i < result.size(); i++) {
      array[i] = Objects.requireNonNull(result.get(i));
    }
    return array;
  }

  public void savePriorLocation(UUID playerUUID, WarpLocation location) {
    this.playerPriorLocations.put(playerUUID, location);
    this.setDirty();
  }

  public Optional<WarpLocation> getPriorLocation(UUID playerUUID) {
    return Objects.requireNonNull(
      Optional.ofNullable(this.playerPriorLocations.get(playerUUID))
    );
  }

  public void deletePriorLocation(UUID playerUUID) {
    this.playerPriorLocations.remove(playerUUID);
    this.setDirty();
  }

  public CompoundTag writeNbt() {
    CompoundTag nbt = new CompoundTag();
    nbt.put("Global", this.global.toNbt());

    CompoundTag playerSpecificNbt = new CompoundTag();
    for (Entry<
      UUID,
      WarpLocationCollection
    > entry : this.playerSpecific.entrySet()) {
      playerSpecificNbt.put(
        entry.getKey().toString(),
        entry.getValue().toNbt()
      );
    }
    nbt.put("PlayerSpecific", playerSpecificNbt);

    CompoundTag playerPriorLocationsNbt = new CompoundTag();
    for (Entry<
      UUID,
      WarpLocation
    > entry : this.playerPriorLocations.entrySet()) {
      playerPriorLocationsNbt.put(
        entry.getKey().toString(),
        entry.getValue().toNbt()
      );
    }
    nbt.put("PlayerPriorLocations", playerPriorLocationsNbt);

    return nbt;
  }

  public static WarpLocationsState fromNbt(@Nullable CompoundTag nbt) {
    CompoundTag checkedNbt = Objects.requireNonNull(nbt);
    WarpLocationCollection global = WarpLocationCollection.fromNbt(
      Objects.requireNonNull(checkedNbt.getCompound("Global").orElseThrow())
    );

    HashMap<UUID, WarpLocationCollection> playerSpecific = new HashMap<>();
    CompoundTag playerSpecificNbt = checkedNbt
      .getCompound("PlayerSpecific")
      .orElseThrow();
    for (String key : playerSpecificNbt.keySet()) {
      playerSpecific.put(
        Objects.requireNonNull(UUID.fromString(key)),
        WarpLocationCollection.fromNbt(
          Objects.requireNonNull(
            playerSpecificNbt.getCompound(key).orElseThrow()
          )
        )
      );
    }

    HashMap<UUID, WarpLocation> playerPriorLocations = new HashMap<>();
    CompoundTag playerPriorLocationsNbt = checkedNbt
      .getCompound("PlayerPriorLocations")
      .orElse(new CompoundTag());
    for (String key : playerPriorLocationsNbt.keySet()) {
      playerPriorLocations.put(
        Objects.requireNonNull(UUID.fromString(key)),
        WarpLocation.fromNbt(
          Objects.requireNonNull(
            playerPriorLocationsNbt.getCompound(key).orElseThrow()
          )
        )
      );
    }

    return new WarpLocationsState(global, playerSpecific, playerPriorLocations);
  }

  public static final SavedDataType<@Nullable WarpLocationsState> TYPE =
    new SavedDataType<@Nullable WarpLocationsState>(
      DansUtils.MOD_ID + "_warp_locations",
      WarpLocationsState::new,
      CompoundTag.CODEC.xmap(WarpLocationsState::fromNbt, state ->
        Objects.requireNonNull(state).writeNbt()
      ),
      null
    );

  public static WarpLocationsState getServerState(MinecraftServer server) {
    ServerLevel overworld = Objects.requireNonNull(
      server.getLevel(Level.OVERWORLD)
    );
    return Objects.requireNonNull(
      overworld.getDataStorage().computeIfAbsent(TYPE)
    );
  }
}
