package com.starcrosser.inspiration.machine.block;

import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.starcrosser.inspiration.machine.blockentity.MineralTwisterTile;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

/**
 * 矿物扭曲机一族的方块：可水平朝向四个方向，青绿色主题（与物质重塑机的黄绿色区分开）。
 * <p>
 * 矿物扭曲机（等级 1）与高能矿物扭曲机（等级 2）共用这一份实现，
 * 差别由注册时传入的 {@link MachineSpec}（等级）与硬度参数决定。
 */
public class MineralTwisterBlock extends RotatableBlock<MineralTwisterTile> {

    private final MachineSpec spec;

    /** 延迟取注册条目：{@code ModMachines} 的静态字段要等 register() 跑完才有值。 */
    private final Supplier<BlockWithTile> entry;

    public MineralTwisterBlock(String registryName, MachineSpec spec, Supplier<BlockWithTile> entry,
                               float hardness, float blastResistance) {
        super(registryName,
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                        .strength(hardness, blastResistance)
                        .requiresCorrectToolForDrops(),
                MineralTwisterTile.class);
        this.spec = spec;
        this.entry = entry;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<MineralTwisterTile> getTileEntityFactory() {
        return (pos, state) -> new MineralTwisterTile(spec, this, entry.get().type().get(), pos, state);
    }
}
