package com.danschellekens.mc.commands.mixins;

import java.util.Collection;
import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.danschellekens.mc.utils.CommandUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ClearCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(ClearCommand.class)
public class DisableClearCommandMixin {
  @Overwrite()
  private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Predicate<ItemStack> item, int maxCount) throws CommandSyntaxException {
    return CommandUtils.success(
      source, 
      Text.literal("Did absolutely nothing (command is disabled)."), 
      Text.literal(source.getName() + " attempted to run disabled clear command.").formatted(Formatting.GRAY, Formatting.ITALIC), 
      false
    );
  }
}
