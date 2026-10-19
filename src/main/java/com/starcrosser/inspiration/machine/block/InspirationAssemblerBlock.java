package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.machine.blockentity.InspirationAssemblerTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 灵感装配台方块：可水平朝向四个方向，黄绿色主题，
 * 机身为同心圆舷窗与横向散热格栅，与另外两台绿色机器区分开。
 */
public class InspirationAssemblerBlock extends RotatableBlock<InspirationAssemblerTile> {

    public InspirationAssemblerBlock() {
        super("inspiration_assembler",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(5.0F, 10.0F)
                        .requiresCorrectToolForDrops(),
                InspirationAssemblerTile.class);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<InspirationAssemblerTile> getTileEntityFactory() {
        return InspirationAssemblerTile::new;
    }
}
