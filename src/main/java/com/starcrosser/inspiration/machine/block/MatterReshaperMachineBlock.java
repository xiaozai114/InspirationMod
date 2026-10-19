package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.machine.blockentity.MatterReshaperMachineTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 物质重塑机方块：可水平朝向四个方向，绿色主题。
 */
public class MatterReshaperMachineBlock extends RotatableBlock<MatterReshaperMachineTile> {

    public MatterReshaperMachineBlock() {
        super("matter_reshaper",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(4.5F, 9.0F)
                        .requiresCorrectToolForDrops(),
                MatterReshaperMachineTile.class);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<MatterReshaperMachineTile> getTileEntityFactory() {
        return MatterReshaperMachineTile::new;
    }
}
