package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.machine.blockentity.AlkalineReactorTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 碱性发电机方块：可水平朝向四个方向，机身与发电核心同为绿色系。
 */
public class AlkalineReactorBlock extends RotatableBlock<AlkalineReactorTile> {

    public AlkalineReactorBlock() {
        super("alkaline_reactor",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(4.5F, 9.0F)
                        .requiresCorrectToolForDrops(),
                AlkalineReactorTile.class);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<AlkalineReactorTile> getTileEntityFactory() {
        return AlkalineReactorTile::new;
    }
}
