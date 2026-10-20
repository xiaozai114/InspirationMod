package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.starcrosser.inspiration.machine.blockentity.MatterReshaperMachineTile;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

/**
 * 物质重塑机一族的方块：可水平朝向四个方向。
 * <p>
 * 物质重塑机（等级 1）与高能物质重塑机（等级 2）共用这一份实现，差别由注册时传入的
 * {@link MachineSpec}（等级、进度条颜色）与硬度参数决定。
 * 注意本族两档的进度条颜色不同（绿 / 紫），是历史遗留，为了不改观感原样保留。
 */
public class MatterReshaperMachineBlock extends RotatableBlock<MatterReshaperMachineTile> {

    private final MachineSpec spec;

    /** 延迟取注册条目：{@code ModMachines} 的静态字段要等 register() 跑完才有值。 */
    private final Supplier<BlockWithTile> entry;

    public MatterReshaperMachineBlock(String registryName, MachineSpec spec, Supplier<BlockWithTile> entry,
                                      float hardness, float blastResistance) {
        super(registryName,
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(hardness, blastResistance)
                        .requiresCorrectToolForDrops(),
                MatterReshaperMachineTile.class);
        this.spec = spec;
        this.entry = entry;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<MatterReshaperMachineTile> getTileEntityFactory() {
        return (pos, state) -> new MatterReshaperMachineTile(spec, this, entry.get().type().get(), pos, state);
    }
}
