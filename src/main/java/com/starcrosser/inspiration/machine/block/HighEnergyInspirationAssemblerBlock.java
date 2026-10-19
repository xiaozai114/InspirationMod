package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.machine.blockentity.HighEnergyInspirationAssemblerTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 高能灵感装配台方块：可水平朝向四个方向，紫色主题（与橄榄绿的灵感装配台区分开）。
 * <p>
 * 是本阶段最重要的合成机器，因此外壳也按最高档来做：比上一阶段更硬、抗爆更高。
 */
public class HighEnergyInspirationAssemblerBlock extends RotatableBlock<HighEnergyInspirationAssemblerTile> {

    public HighEnergyInspirationAssemblerBlock() {
        super("high_energy_inspiration_assembler",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(5.0F, 12.0F)
                        .requiresCorrectToolForDrops(),
                HighEnergyInspirationAssemblerTile.class);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<HighEnergyInspirationAssemblerTile> getTileEntityFactory() {
        return HighEnergyInspirationAssemblerTile::new;
    }
}
