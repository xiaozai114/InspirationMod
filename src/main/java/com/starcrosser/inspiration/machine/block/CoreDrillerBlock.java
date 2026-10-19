package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.machine.blockentity.CoreDrillerTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 地核钻探者方块：可水平朝向四个方向，绿机身 + 紫钻芯。
 * <p>
 * 朝向只决定 GUI 里各槽位的排布方向，不影响挖掘方向 —— 这台机器永远向下作业。
 */
public class CoreDrillerBlock extends RotatableBlock<CoreDrillerTile> {

    public CoreDrillerBlock() {
        super("core_driller",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(6.0F, 12.0F)
                        .requiresCorrectToolForDrops(),
                CoreDrillerTile.class);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<CoreDrillerTile> getTileEntityFactory() {
        return CoreDrillerTile::new;
    }
}
