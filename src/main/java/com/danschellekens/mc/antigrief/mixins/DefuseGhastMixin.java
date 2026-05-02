package com.danschellekens.mc.antigrief.mixins;

import net.minecraft.world.entity.monster.Ghast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Ghast.class)
public class DefuseGhastMixin {

  @Shadow
  private int explosionPower;

  @Inject(method = "getExplosionPower", at = @At("HEAD"))
  private void defuseOnGetFireballStrength(
    CallbackInfoReturnable<Integer> info
  ) {
    this.explosionPower = 0;
  }
}
