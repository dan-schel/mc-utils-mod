package com.danschellekens.mc.antigrief.mixins;

import com.danschellekens.mc.antigrief.TntLogging;
import java.util.Objects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.eclipse.jdt.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class LogTntEntitySpawnedMixin {

  @Shadow
  public @Nullable MinecraftServer getServer() {
    return null;
  }

  @Inject(method = "addFreshEntity", at = @At("HEAD"))
  public void logOnSpawnEntity(
    Entity entity,
    CallbackInfoReturnable<Boolean> info
  ) {
    TntLogging.onEntitySpawned(entity, Objects.requireNonNull(getServer()));
  }
}
