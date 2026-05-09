package com.danschellekens.mc.state;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class WarpLocation {

  BlockPos position;
  WarpLocationDimension dimension;

  public WarpLocation(BlockPos position, WarpLocationDimension dimension) {
    this.position = position;
    this.dimension = dimension;
  }

  public BlockPos getPosition() {
    return position;
  }

  public WarpLocationDimension getDimension() {
    return dimension;
  }

  public String getDisplayString() {
    return (
      position.getX() +
      ", " +
      position.getY() +
      ", " +
      position.getZ() +
      " in " +
      dimension.getDisplayString()
    );
  }

  public CompoundTag toNbt() {
    CompoundTag nbt = new CompoundTag();
    nbt.putInt("X", this.position.getX());
    nbt.putInt("Y", this.position.getY());
    nbt.putInt("Z", this.position.getZ());
    nbt.putString("Dimension", this.dimension.name());
    return nbt;
  }

  public static WarpLocation fromNbt(CompoundTag nbt) {
    BlockPos position = new BlockPos(
      nbt.getInt("X").orElseThrow(),
      nbt.getInt("Y").orElseThrow(),
      nbt.getInt("Z").orElseThrow()
    );
    WarpLocationDimension dimension = WarpLocationDimension.valueOf(
      nbt.getString("Dimension").orElseThrow()
    );
    return new WarpLocation(position, dimension);
  }

  public static WarpLocation fromWorld(Vec3 position, Level world) {
    BlockPos blockPos = new BlockPos(
      (int) Math.floor(position.x),
      // Round y-coordinate (rather than floor) in case the player is standing
      // on a half-slab.
      (int) Math.round(position.y),
      (int) Math.floor(position.z)
    );

    return new WarpLocation(
      blockPos,
      WarpLocationDimension.fromWorldRegistryKey(world.dimension())
    );
  }
}
