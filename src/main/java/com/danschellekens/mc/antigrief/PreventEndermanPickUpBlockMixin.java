package com.danschellekens.mc.antigrief;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.entity.mob.EndermanEntity;

@Mixin(EndermanEntity.PickUpBlockGoal.class)
public class PreventEndermanPickUpBlockMixin {
  @Overwrite()
	public boolean canStart() {
    return false;
	}
}
