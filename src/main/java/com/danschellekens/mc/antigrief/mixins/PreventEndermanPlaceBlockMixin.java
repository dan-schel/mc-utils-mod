package com.danschellekens.mc.antigrief.mixins;

import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EnderMan.EndermanLeaveBlockGoal.class)
public class PreventEndermanPlaceBlockMixin {

  @Overwrite
  public boolean canUse() {
    return false;
  }
}
