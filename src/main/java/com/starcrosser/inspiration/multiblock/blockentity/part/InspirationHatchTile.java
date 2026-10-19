package com.starcrosser.inspiration.multiblock.blockentity.part;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.starcrosser.inspiration.machine.charge.InspirationCharge;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * 灵感提供仓：装灵感充能的罐子，只进不出。
 * <p>
 * 8 mB/刻的消耗对单台机器来说不算什么，但模拟动辄跑一万刻以上，所以容量按
 * 「一份数据跑完一整轮」来定：12000 刻 × 8 mB = 96,000 mB，这里给 128,000 mB。
 * 罐体设为 {@code FILL}，控制器抽的时候必须走 {@link #drainForced}。
 */
/** 罐子由管道灌，玩家不需要在这里开界面，所以直接继承 {@link ActiveTile}。 */
public class InspirationHatchTile extends ActiveTile<InspirationHatchTile> {

    public static final int CAPACITY = 128_000;

    @Save
    private final FluidTankComponent<InspirationHatchTile> tank;

    public InspirationHatchTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<InspirationHatchTile>) ModMultiblocks.INSPIRATION_HATCH.getBlock(),
                ModMultiblocks.INSPIRATION_HATCH.type().get(), pos, state);
        // 注意参数顺序：容量在第 2 位（name, capacity, posX, posY），与 SidedFluidTankComponent 一致
        FluidTankComponent<InspirationHatchTile> tank = new FluidTankComponent<>(
                "inspiration_charge", CAPACITY, 44, 20);
        tank.setTankType(FluidTankComponent.Type.NORMAL);
        tank.setTankAction(FluidTankComponent.Action.FILL);
        tank.setValidator(InspirationCharge::isChargeFluid);
        this.tank = tank;
        addTank(this.tank);
    }

    @Override
    public InspirationHatchTile getSelf() {
        return this;
    }

    /** 罐内剩余充能（mB）。 */
    public int getChargeAmount() {
        return tank.getFluidAmount();
    }

    public int getChargeCapacity() {
        return tank.getCapacity();
    }

    /**
     * 被控制器抽走指定量。
     * 罐体是「只进不出」，普通 {@code drain} 会被动作校验拦掉，必须走强制版。
     */
    public FluidStack drainForced(int amount) {
        return tank.drainForced(amount, IFluidHandler.FluidAction.EXECUTE);
    }
}
