package com.danschellekens.mc.antigrief;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.entity.mob.EndermanEntity;

// PickUpBlockGoal is a private class by default. I've had to add an "access 
// widener" to allow us to override it here.
@Mixin(EndermanEntity.PickUpBlockGoal.class)
public class PreventEndermanPickUpBlockMixin {
  // Instead of @Inject to add code to a method, @Overwrite replaces the entire
  // method body with our own code. The method signature must match exactly.
  @Overwrite()
	public boolean canStart() {
    return false;
	}
}
