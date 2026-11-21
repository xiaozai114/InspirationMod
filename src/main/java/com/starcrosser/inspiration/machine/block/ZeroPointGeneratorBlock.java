package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.starcrosser.inspiration.machine.blockentity.ZeroPointGeneratorTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class ZeroPointGeneratorBlock extends RotatableBlock<ZeroPointGeneratorTile> {

    private final Supplier<BlockWithTile> entry;

    public ZeroPointGeneratorBlock(String registryName, Supplier<BlockWithTile> entry,
                                   float hardness, float blastResistance) {
        super(registryName,
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(hardness, blastResistance)
                        .requiresCorrectToolForDrops(),
                ZeroPointGeneratorTile.class);
        this.entry = entry;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<ZeroPointGeneratorTile> getTileEntityFactory() {
        return (pos, state) -> new ZeroPointGeneratorTile(this, entry.get().type().get(), pos, state);
    }
}
