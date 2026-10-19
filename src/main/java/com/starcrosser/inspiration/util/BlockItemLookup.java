package com.starcrosser.inspiration.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FluidState;

/**
 * 方块到「代表性物品」的映射。
 * <p>
 * 右键转化配方以方块为主体，但机器与 JEI 都只能用物品来表示它。
 * 绝大多数方块直接取 {@link Block#asItem()}；水、岩浆这类流体方块没有方块物品，
 * 退回到对应的桶；两者都没有时返回空物品，由调用方跳过该槽位。
 */
public final class BlockItemLookup {

    public static ItemStack iconOf(Block block) {
        if (block == Blocks.AIR) {
            return ItemStack.EMPTY;
        }
        Item item = block.asItem();
        if (item != Items.AIR) {
            return new ItemStack(item);
        }
        FluidState fluidState = block.defaultBlockState().getFluidState();
        if (!fluidState.isEmpty()) {
            return new ItemStack(fluidState.getType().getBucket());
        }
        return ItemStack.EMPTY;
    }

    private BlockItemLookup() {
    }
}
