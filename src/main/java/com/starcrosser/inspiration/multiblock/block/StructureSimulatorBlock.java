package com.starcrosser.inspiration.multiblock.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.multiblock.blockentity.StructureSimulatorTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 结构模拟机的主方块（控制器）：放在结构正面中层偏左的那一格。
 * <p>
 * 朝向决定结构怎么摆 —— {@link StructureSimulatorTile} 会用这个朝向去尝试成型，
 * 所以必须让玩家可以水平转向四个方向。
 */
public class StructureSimulatorBlock extends RotatableBlock<StructureSimulatorTile> {

    public StructureSimulatorBlock() {
        super("structure_simulator",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(5.0F, 10.0F)
                        .requiresCorrectToolForDrops(),
                StructureSimulatorTile.class);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<StructureSimulatorTile> getTileEntityFactory() {
        return StructureSimulatorTile::new;
    }
}
