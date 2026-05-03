package com.danschellekens.mc.commands.mixins;

import com.danschellekens.mc.utils.CommandUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.ClearInventoryCommands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ClearInventoryCommands.class)
public class DisableClearCommandMixin {

  @Overwrite
  private static int clearInventory(
    CommandSourceStack source,
    Collection<ServerPlayer> targets,
    Predicate<ItemStack> item,
    int maxCount
  ) throws CommandSyntaxException {
    return CommandUtils.success(
      source,
      Objects.requireNonNull(
        Component.literal("Did absolutely nothing (command is disabled).")
      ),
      Objects.requireNonNull(
        Component.literal(
          source.getTextName() + " attempted to run disabled clear command."
        ).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
      ),
      false
    );
  }
}
