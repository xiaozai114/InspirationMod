package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.starcrosser.inspiration.machine.blockentity.EnergizedMatterFusionMachineTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 强能物质融合机方块：可水平朝向四个方向。
 * <p>
 * 是 {@link MatterFusionMachineBlock} 的上位机型，外壳更硬（爆炸抗性略高），
 * 其余行为完全一致。
 */
public class EnergizedMatterFusionMachineBlock extends RotatableBlock<EnergizedMatterFusionMachineTile> {

    public EnergizedMatterFusionMachineBlock() {
        super("energized_matter_fusion_machine",
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(3.5F, 8.0F)
                        .requiresCorrectToolForDrops(),
                EnergizedMatterFusionMachineTile.class);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<EnergizedMatterFusionMachineTile> getTileEntityFactory() {
        return EnergizedMatterFusionMachineTile::new;
    }
}
