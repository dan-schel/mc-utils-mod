package com.danschellekens.mc.antigrief;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.entity.mob.EndermanEntity;

@Mixin(EndermanEntity.PlaceBlockGoal.class)
public class PreventEndermanPlaceBlockMixin {
  @Overwrite()
	public boolean canStart() {
    return false;
	}
}
