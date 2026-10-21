package com.starcrosser.inspiration.multiblock.blockentity.part;

import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.PoweredTile;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 能量端口：结构模拟机唯一进电的地方。
 * <p>
 * 注意：这里继承 {@link PoweredTile} 而不是 {@link InventoryHatchTile} —— 能量缓冲只有
 * {@code PoweredTile} 那条链上有，而它不提供 {@code openGui}，正好这个仓室也不需要界面。
 * <p>
 * 它是个纯缓冲 —— 控制器每刻从这里抽走本刻要用的电，抽不出来就停机等待，
 * 所以「电力不足」永远表现为进度条不动，而不是机器把电偷偷扣成负数。
 * 容量给得比控制器内部缓冲大得多，玩家可以一次性灌满然后让它慢慢跑。
 */
public class EnergyPortTile extends PoweredTile<EnergyPortTile> {

    /** 端口容量：2,000,000 FE，够跑满 2000 刻 1k FE/t 的模拟。 */
    public static final int CAPACITY = 2_000_000;

    /** 单刻进出上限，取得比较宽松，避免高功率配方被端口卡住。 */
    public static final int TRANSFER = 100_000;

    public EnergyPortTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<EnergyPortTile>) ModMultiblocks.ENERGY_PORT.getBlock(),
                ModMultiblocks.ENERGY_PORT.type().get(), pos, state);
    }

    @Override
    public EnergyPortTile getSelf() {
        return this;
    }

    @Override
    protected EnergyStorageComponent<EnergyPortTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(CAPACITY, TRANSFER, TRANSFER, 8, 14);
    }
}
