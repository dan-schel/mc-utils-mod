package com.danschellekens.mc.state;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class WarpLocation {
  String name;
  BlockPos position;
  String dimension;

  public WarpLocation(String name, BlockPos position, String dimension) {
    this.name = name;
    this.position = position;
    this.dimension = dimension;
  }

  BlockPos getPosition() {
    return position;
  }

  String getDimension() {
    return dimension;
  }

  public NbtCompound toNbt() {
    NbtCompound nbt = new NbtCompound();
    nbt.putString("Name", this.name);
    nbt.putInt("X", this.position.getX());
    nbt.putInt("Y", this.position.getY());
    nbt.putInt("Z", this.position.getZ());
    nbt.putString("Dimension", this.dimension);
    return nbt;
  }

  public static WarpLocation fromNbt(NbtCompound nbt) {
    String name = nbt.getString("Name");
    BlockPos position = new BlockPos(
      nbt.getInt("X"),
      nbt.getInt("Y"),
      nbt.getInt("Z")
    );
    String dimension = nbt.getString("Dimension");
    return new WarpLocation(name, position, dimension);
  }
}
