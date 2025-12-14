package com.danschellekens.mc.state;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    return position.getX() + ", " + position.getY() + ", " + position.getZ() + " in " + dimension.getDisplayString();
  }

  public NbtCompound toNbt() {
    NbtCompound nbt = new NbtCompound();
    nbt.putInt("X", this.position.getX());
    nbt.putInt("Y", this.position.getY());
    nbt.putInt("Z", this.position.getZ());
    nbt.putString("Dimension", this.dimension.name());
    return nbt;
  }

  public static WarpLocation fromNbt(NbtCompound nbt) {
    BlockPos position = new BlockPos(
      nbt.getInt("X").orElseThrow(),
      nbt.getInt("Y").orElseThrow(),
      nbt.getInt("Z").orElseThrow()
    );
    WarpLocationDimension dimension = WarpLocationDimension.valueOf(nbt.getString("Dimension").orElseThrow());
    return new WarpLocation(position, dimension);
  }

  public static WarpLocation fromWorld(BlockPos position, World world) {
    return new WarpLocation(position, WarpLocationDimension.fromWorldRegistryKey(world.getRegistryKey()));
  }
}
