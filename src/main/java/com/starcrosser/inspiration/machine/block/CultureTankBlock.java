package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.starcrosser.inspiration.machine.blockentity.CultureTankTile;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;

import java.util.function.Supplier;

public class CultureTankBlock extends RotatableBlock<CultureTankTile> {

    private final MachineSpec spec;
    private final Supplier<BlockWithTile> entry;

    public CultureTankBlock(String registryName, MachineSpec spec, Supplier<BlockWithTile> entry,
                            float hardness, float blastResistance) {
        super(registryName,
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(hardness, blastResistance)
                        .requiresCorrectToolForDrops(),
                CultureTankTile.class);
        this.spec = spec;
        this.entry = entry;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof CultureTankTile tank
                && FluidUtil.interactWithFluidHandler(player, hand, tank.nutrientHandler())) {
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<CultureTankTile> getTileEntityFactory() {
        return (pos, state) -> new CultureTankTile(spec, this, entry.get().type().get(), pos, state);
    }
}
