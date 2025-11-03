package com.danschellekens.mc.state;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WarpLocation {
  String name;
  BlockPos position;
  String dimension;

  public WarpLocation(String name, BlockPos position, String dimension) {
    this.name = name;
    this.position = position;
    this.dimension = dimension;
  }

  public BlockPos getPosition() {
    return position;
  }

  public String getDimension() {
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

  public static String getStringForDimension(RegistryKey<World> worldKey) {
    if (worldKey == World.NETHER) {
      return "the_nether";
    } else if (worldKey == World.END) {
      return "the_end";
    } else {
      return "overworld";
    }
  }

  public static RegistryKey<World> getDimensionForString(String dimensionString) {
    if (dimensionString.equals("the_nether")) {
      return World.NETHER;
    } else if (dimensionString.equals("the_end")) {
      return World.END;
    } else {
      return World.OVERWORLD;
    }
  }

  public static String getDimensionDisplayString(String dimensionString) {
    if (dimensionString.equals("the_nether")) {
      return "The Nether";
    } else if (dimensionString.equals("the_end")) {
      return "The End";
    } else {
      return "The Overworld";
    }
  }
}
