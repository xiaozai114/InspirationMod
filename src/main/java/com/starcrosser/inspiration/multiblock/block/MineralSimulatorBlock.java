package com.starcrosser.inspiration.multiblock.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.multiblock.blockentity.MineralSimulatorTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 矿物模拟机的主方块（控制器）：3×5×3 结构正面中层的那一格。
 */
public class MineralSimulatorBlock extends RotatableBlock<MineralSimulatorTile> {

    public MineralSimulatorBlock() {
        super("mineral_simulator",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(5.0F, 10.0F)
                        .requiresCorrectToolForDrops(),
                MineralSimulatorTile.class);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<MineralSimulatorTile> getTileEntityFactory() {
        return MineralSimulatorTile::new;
    }
}
