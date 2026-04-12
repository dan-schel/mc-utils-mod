package com.danschellekens.mc.antigrief.mixins;

import com.danschellekens.mc.antigrief.TntLogging;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public class LogTntEntitySpawnedMixin {

  @Shadow
  public MinecraftServer getServer() {
    return null;
  }

  @Inject(method = "spawnEntity", at = @At("HEAD"))
  public void logOnSpawnEntity(
    Entity entity,
    CallbackInfoReturnable<Boolean> info
  ) {
    TntLogging.onEntitySpawned(entity, getServer());
  }
}
