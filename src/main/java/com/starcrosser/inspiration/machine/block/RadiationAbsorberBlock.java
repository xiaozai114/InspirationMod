package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.machine.blockentity.RadiationAbsorberTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 辐射吸收器。
 */
public class RadiationAbsorberBlock extends RotatableBlock<RadiationAbsorberTile> {

    public RadiationAbsorberBlock() {
        super("radiation_absorber",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(4.5F, 9.0F)
                        .requiresCorrectToolForDrops(),
                RadiationAbsorberTile.class);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<RadiationAbsorberTile> getTileEntityFactory() {
        return RadiationAbsorberTile::new;
    }
}
