package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.machine.blockentity.HighEnergyMatterReshaperMachineTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 高能物质重塑机方块：可水平朝向四个方向，紫色主题（与黄绿色的物质重塑机区分开）。
 * <p>
 * 外壳强度与其余高能机型同一档。
 */
public class HighEnergyMatterReshaperMachineBlock extends RotatableBlock<HighEnergyMatterReshaperMachineTile> {

    public HighEnergyMatterReshaperMachineBlock() {
        super("high_energy_matter_reshaper",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(5.0F, 12.0F)
                        .requiresCorrectToolForDrops(),
                HighEnergyMatterReshaperMachineTile.class);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<HighEnergyMatterReshaperMachineTile> getTileEntityFactory() {
        return HighEnergyMatterReshaperMachineTile::new;
    }
}
