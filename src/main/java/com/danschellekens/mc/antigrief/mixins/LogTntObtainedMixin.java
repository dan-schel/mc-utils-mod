package com.danschellekens.mc.antigrief.mixins;

import com.danschellekens.mc.antigrief.TntLogging;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class LogTntObtainedMixin {

  @Shadow
  public PlayerEntity player;

  @Inject(
    // Specifically want to target the method with (int, ItemStack) signature.
    method = "addStack(ILnet/minecraft/item/ItemStack;)I",
    at = @At("HEAD")
  )
  private void logOnAddStack(
    int slot,
    ItemStack stack,
    CallbackInfoReturnable<Integer> info
  ) {
    TntLogging.onItemStackSet(player, stack);
  }

  @Inject(method = "setStack", at = @At("HEAD"))
  public void logOnInsertStack(int slot, ItemStack stack, CallbackInfo info) {
    TntLogging.onItemStackSet(player, stack);
  }
}
