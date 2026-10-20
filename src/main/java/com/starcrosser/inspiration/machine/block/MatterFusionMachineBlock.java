package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.starcrosser.inspiration.machine.blockentity.MatterFusionMachineTile;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

/**
 * 物质融合机一族的方块：可水平朝向四个方向。
 * <p>
 * 物质融合机（等级 1）与强能物质融合机（等级 2）共用这一份实现 ——
 * 差别由注册时传入的 {@link MachineSpec}（等级、输入槽数）与硬度参数决定。
 * 两个方块的注册名各自独立，所以方块 id 与老存档都不受影响。
 */
public class MatterFusionMachineBlock extends RotatableBlock<MatterFusionMachineTile> {

    private final MachineSpec spec;

    /** 延迟取注册条目：{@code ModMachines} 的静态字段要等 register() 跑完才有值。 */
    private final Supplier<BlockWithTile> entry;

    public MatterFusionMachineBlock(String registryName, MachineSpec spec, Supplier<BlockWithTile> entry,
                                    float hardness, float blastResistance) {
        super(registryName,
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(hardness, blastResistance)
                        .requiresCorrectToolForDrops(),
                MatterFusionMachineTile.class);
        this.spec = spec;
        this.entry = entry;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<MatterFusionMachineTile> getTileEntityFactory() {
        return (pos, state) -> new MatterFusionMachineTile(spec, this, entry.get().type().get(), pos, state);
    }
}
