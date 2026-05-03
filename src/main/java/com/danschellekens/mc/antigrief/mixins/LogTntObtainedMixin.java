package com.danschellekens.mc.antigrief.mixins;

import com.danschellekens.mc.antigrief.TntLogging;
import java.util.Objects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.eclipse.jdt.annotation.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class LogTntObtainedMixin {

  @Shadow
  public @Nullable Player player;

  @Inject(
    // Specifically want to target the method with (int, ItemStack) signature.
    method = "addResource(ILnet/minecraft/world/item/ItemStack;)I",
    at = @At("HEAD")
  )
  private void logOnAddStack(
    int slot,
    ItemStack stack,
    CallbackInfoReturnable<Integer> info
  ) {
    TntLogging.onItemStackSet(Objects.requireNonNull(player), stack);
  }

  @Inject(method = "setItem", at = @At("HEAD"))
  public void logOnInsertStack(int slot, ItemStack stack, CallbackInfo info) {
    TntLogging.onItemStackSet(Objects.requireNonNull(player), stack);
  }
}
