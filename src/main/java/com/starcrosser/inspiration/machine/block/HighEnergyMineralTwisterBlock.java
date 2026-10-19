package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.machine.blockentity.HighEnergyMineralTwisterTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 高能矿物扭曲机方块：可水平朝向四个方向，紫色主题（与青绿色的矿物扭曲机区分开）。
 * <p>
 * 外壳比上一阶段更硬，爆炸抗性也更高 —— 高能机型统一走这一档。
 */
public class HighEnergyMineralTwisterBlock extends RotatableBlock<HighEnergyMineralTwisterTile> {

    public HighEnergyMineralTwisterBlock() {
        super("high_energy_mineral_twister",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(5.0F, 12.0F)
                        .requiresCorrectToolForDrops(),
                HighEnergyMineralTwisterTile.class);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<HighEnergyMineralTwisterTile> getTileEntityFactory() {
        return HighEnergyMineralTwisterTile::new;
    }
}
