package com.danschellekens.mc.antigrief;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.mob.CreeperEntity;

@Mixin(CreeperEntity.class)
public class DefuseCreeperMixin {
  // Allows us to access explosionRadius from CreeperEntity within our mixin.
  @Shadow
  private int explosionRadius;

  // This code is injected into the start of CreeperEntity.explode(). The mixin 
  // method must match the signature of it's target with the addition of a 
  // CallbackInfo parameter at the end. In this case, explode() has no arguments.
  @Inject(method = "explode", at = @At("HEAD"))
	private void defuseOnExplode(CallbackInfo info) {
    this.explosionRadius = 0;
	}
}
