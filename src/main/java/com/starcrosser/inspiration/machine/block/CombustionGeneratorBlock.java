package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.machine.blockentity.CombustionGeneratorTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 简易火力发电机。
 */
public class CombustionGeneratorBlock extends RotatableBlock<CombustionGeneratorTile> {

    public CombustionGeneratorBlock() {
        super("combustion_generator",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(4.5F, 9.0F)
                        .requiresCorrectToolForDrops(),
                CombustionGeneratorTile.class);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<CombustionGeneratorTile> getTileEntityFactory() {
        return CombustionGeneratorTile::new;
    }
}
