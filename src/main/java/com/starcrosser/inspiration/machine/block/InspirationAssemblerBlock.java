package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.starcrosser.inspiration.machine.blockentity.InspirationAssemblerTile;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

/**
 * 灵感装配台一族的方块：可水平朝向四个方向，黄绿色主题，
 * 机身为同心圆舷窗与横向散热格栅，与另外两台绿色机器区分开。
 * <p>
 * 灵感装配台（等级 1）与高能灵感装配台（等级 2）共用这一份实现：
 * 4 物品槽 + 1 流体槽 / 6 物品槽 + 2 流体槽的差别全部来自注册时的 {@link MachineSpec}，
 * 与硬度参数一起在注册处填好。
 */
public class InspirationAssemblerBlock extends RotatableBlock<InspirationAssemblerTile> {

    private final MachineSpec spec;

    /** 延迟取注册条目：{@code ModMachines} 的静态字段要等 register() 跑完才有值。 */
    private final Supplier<BlockWithTile> entry;

    public InspirationAssemblerBlock(String registryName, MachineSpec spec, Supplier<BlockWithTile> entry,
                                     float hardness, float blastResistance) {
        super(registryName,
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(hardness, blastResistance)
                        .requiresCorrectToolForDrops(),
                InspirationAssemblerTile.class);
        this.spec = spec;
        this.entry = entry;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<InspirationAssemblerTile> getTileEntityFactory() {
        return (pos, state) -> new InspirationAssemblerTile(spec, this, entry.get().type().get(), pos, state);
    }
}
