package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.machine.blockentity.MineralTwisterTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 矿物扭曲机方块：可水平朝向四个方向，青绿色主题（与物质重塑机的黄绿色区分开）。
 */
public class MineralTwisterBlock extends RotatableBlock<MineralTwisterTile> {

    public MineralTwisterBlock() {
        super("mineral_twister",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(5.0F, 10.0F)
                        .requiresCorrectToolForDrops(),
                MineralTwisterTile.class);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<MineralTwisterTile> getTileEntityFactory() {
        return MineralTwisterTile::new;
    }
}
