package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.starcrosser.inspiration.machine.blockentity.CoreDrillerTile;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

/**
 * 地核钻探者方块：可水平朝向四个方向，绿机身 + 紫钻芯。
 * <p>
 * 朝向只决定 GUI 里各槽位的排布方向，不影响挖掘方向 —— 这台机器永远向下作业。
 * <p>
 * 它是目前唯一没有上位机型的机器，但仍然走 {@link MachineSpec} 这一套：
 * 等级固定为 1，将来要加高阶钻探者时直接再注册一份配置即可。
 */
public class CoreDrillerBlock extends RotatableBlock<CoreDrillerTile> {

    private final MachineSpec spec;

    /** 延迟取注册条目：{@code ModMachines} 的静态字段要等 register() 跑完才有值。 */
    private final Supplier<BlockWithTile> entry;

    public CoreDrillerBlock(String registryName, MachineSpec spec, Supplier<BlockWithTile> entry,
                            float hardness, float blastResistance) {
        super(registryName,
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(hardness, blastResistance)
                        .requiresCorrectToolForDrops(),
                CoreDrillerTile.class);
        this.spec = spec;
        this.entry = entry;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<CoreDrillerTile> getTileEntityFactory() {
        return (pos, state) -> new CoreDrillerTile(spec, this, entry.get().type().get(), pos, state);
    }
}
