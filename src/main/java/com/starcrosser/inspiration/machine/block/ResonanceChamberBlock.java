package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.machine.blockentity.ResonanceChamberTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 谐振腔。
 */
public class ResonanceChamberBlock extends RotatableBlock<ResonanceChamberTile> {

    public ResonanceChamberBlock() {
        super("resonance_chamber",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(4.5F, 9.0F)
                        .requiresCorrectToolForDrops(),
                ResonanceChamberTile.class);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState replacement, boolean moving) {
        if (!level.isClientSide && !state.is(replacement.getBlock())
                && level.getBlockEntity(pos) instanceof ResonanceChamberTile chamber) {
            popResource(level, pos, chamber.releaseWorkpiece());
        }
        super.onRemove(state, level, pos, replacement, moving);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<ResonanceChamberTile> getTileEntityFactory() {
        return ResonanceChamberTile::new;
    }
}
