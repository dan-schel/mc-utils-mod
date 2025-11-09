package com.danschellekens.mc.antigrief;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.mob.GhastEntity;

@Mixin(GhastEntity.class)
public class DefuseGhastMixin {
  @Shadow
  private int fireballStrength;

  @Inject(method = "getFireballStrength", at = @At("HEAD"))
	private void defuseOnGetFireballStrength(CallbackInfoReturnable<Integer> info) {
    this.fireballStrength = 0;
	}
}
