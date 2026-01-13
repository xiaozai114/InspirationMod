package com.starcrosser.inspiration.mixin;

import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpawnerBlock.class)
public class SpawnerBlockMixin {
    @Inject(method = "spawnAfterBreak", at = @At("HEAD"), cancellable = true)
    private void onSpawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropExperience, CallbackInfo ci) {
        ItemEntity coreEntity = new ItemEntity(
                level,
                pos.getX()+0.5,
                pos.getY()+0.5+1,
                pos.getZ()+0.5,
                new ItemStack(ModItems.UNSTABLE_CORE.get(), 1)
        );
        level.addFreshEntity(coreEntity);
    }
}

