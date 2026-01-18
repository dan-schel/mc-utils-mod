package com.danschellekens.mc.antigrief.mixins;

import net.minecraft.entity.mob.EndermanEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EndermanEntity.PlaceBlockGoal.class)
public class PreventEndermanPlaceBlockMixin {

  @Overwrite
  public boolean canStart() {
    return false;
  }
}
