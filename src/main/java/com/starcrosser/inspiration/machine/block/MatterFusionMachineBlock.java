package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.machine.blockentity.MatterFusionMachineTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 物质融合器方块：可水平朝向四个方向。
 */
public class MatterFusionMachineBlock extends RotatableBlock<MatterFusionMachineTile> {

    public MatterFusionMachineBlock() {
        super("matter_fusion_machine",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(3.5F, 6.0F)
                        .requiresCorrectToolForDrops(),
                MatterFusionMachineTile.class);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<MatterFusionMachineTile> getTileEntityFactory() {
        return MatterFusionMachineTile::new;
    }
}
