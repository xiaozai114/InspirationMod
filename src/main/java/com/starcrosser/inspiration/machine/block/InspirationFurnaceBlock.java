package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.starcrosser.inspiration.machine.blockentity.InspirationFurnaceTile;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class InspirationFurnaceBlock extends RotatableBlock<InspirationFurnaceTile> {

    private final MachineSpec spec;
    private final Supplier<BlockWithTile> entry;

    public InspirationFurnaceBlock(String registryName, MachineSpec spec, Supplier<BlockWithTile> entry,
                                   float hardness, float blastResistance) {
        super(registryName,
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(hardness, blastResistance)
                        .requiresCorrectToolForDrops(),
                InspirationFurnaceTile.class);
        this.spec = spec;
        this.entry = entry;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<InspirationFurnaceTile> getTileEntityFactory() {
        return (pos, state) -> new InspirationFurnaceTile(spec, this, entry.get().type().get(), pos, state);
    }
}
