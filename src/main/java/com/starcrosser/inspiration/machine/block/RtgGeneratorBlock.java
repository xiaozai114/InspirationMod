package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.machine.blockentity.RtgGeneratorTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * RTG反应堆方块：可水平朝向四个方向，机身与反应核心同为红色系。
 */
public class RtgGeneratorBlock extends RotatableBlock<RtgGeneratorTile> {

    public RtgGeneratorBlock() {
        super("rtg_generator",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(4.5F, 9.0F)
                        .requiresCorrectToolForDrops(),
                RtgGeneratorTile.class);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<RtgGeneratorTile> getTileEntityFactory() {
        return RtgGeneratorTile::new;
    }
}
