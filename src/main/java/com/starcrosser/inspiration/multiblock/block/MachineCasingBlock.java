package com.starcrosser.inspiration.multiblock.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 高能机器外壳：结构模拟机的机体材料，外观是「淡紫色的铁块」。
 * <p>
 * 它本身没有任何逻辑，只是多方块结构里可以被替换成各类仓室的那个方块 ——
 * 结构成型时，外壳与五种仓室在 {@code BlockPattern} 里是同一个字符。
 */
public class MachineCasingBlock extends Block {

    public MachineCasingBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .strength(5.0F, 10.0F)
                .requiresCorrectToolForDrops());
    }
}
