package com.starcrosser.inspiration.multiblock.blockentity.part;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.starcrosser.inspiration.machine.charge.InspirationCharge;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * 灵感提供仓：装灵感充能的罐子，只进不出。
 * <p>
 * 8 mB/刻的消耗对单台机器来说不算什么，但模拟动辄跑一万刻以上，所以容量按
 * 「一份数据跑完一整轮」来定：12000 刻 × 8 mB = 96,000 mB，这里给 128,000 mB。
 * 罐体设为 {@code FILL}，控制器抽的时候必须走 {@link #drainForced}。
 * <p>
 * 补充方式有两种，与小机器一致：
 * <ul>
 *   <li>玩家右键打开界面，把「灵感 / 灵感碎片」放进充能槽，每刻自动折算成充能；</li>
 *   <li>管道直接灌入灵感充能液体（罐体允许任何一侧灌入，但不能被抽走）。</li>
 * </ul>
 * 因此它继承 {@link HatchTile} 而不是 {@code ActiveTile}：只有 {@code HatchTile}
 * 覆写了 {@code onActivated} 去调 {@code openGui}，否则右键没有任何反应。
 */
public class InspirationHatchTile extends InventoryHatchTile<InspirationHatchTile> {

    public static final int CAPACITY = 128_000;

    /** 充能槽在 GUI 中的位置：与输入仓的槽位同一行，玩家一眼能对应上。 */
    private static final int CHARGE_SLOT_X = 44;
    private static final int CHARGE_SLOT_Y = 30;

    /** 充能罐摆在充能槽右侧。 */
    private static final int TANK_X = 116;
    private static final int TANK_Y = 30;

    @Save
    private final FluidTankComponent<InspirationHatchTile> tank;

    public InspirationHatchTile(BlockPos pos, BlockState state) {
        // 充能槽交给基类建（1 格、只收灵感 / 灵感碎片），本类只多一个罐子
        super((BasicTileBlock<InspirationHatchTile>) ModMultiblocks.INSPIRATION_HATCH.getBlock(),
                ModMultiblocks.INSPIRATION_HATCH.type().get(), pos, state,
                "inspiration", CHARGE_SLOT_X, CHARGE_SLOT_Y, 1, 1, 1,
                (stack, integer) -> InspirationCharge.isChargeSource(stack));

        // 注意参数顺序：容量在第 2 位（name, capacity, posX, posY），与 SidedFluidTankComponent 一致
        FluidTankComponent<InspirationHatchTile> tank = new FluidTankComponent<>(
                "inspiration_charge", CAPACITY, TANK_X, TANK_Y);
        tank.setTankType(FluidTankComponent.Type.NORMAL);
        tank.setTankAction(FluidTankComponent.Action.FILL);
        tank.setValidator(InspirationCharge::isChargeFluid);
        this.tank = tank;
        addTank(this.tank);
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, InspirationHatchTile self) {
        super.serverTick(level, pos, state, self);
        convertInspiration();
    }

    /**
     * 把槽内的灵感或灵感碎片折成充能。
     * 一次尽可能多转化，但不会超过罐体剩余容量，避免溢出丢失。
     */
    private void convertInspiration() {
        ItemStack stack = getInventory().getStackInSlot(0);
        int perItem = InspirationCharge.chargeValue(stack);
        if (perItem <= 0) {
            return;
        }
        int room = tank.getCapacity() - tank.getFluidAmount();
        int items = Math.min(stack.getCount(), room / perItem);
        if (items <= 0) {
            return;
        }
        tank.fill(InspirationCharge.stack(items * perItem), IFluidHandler.FluidAction.EXECUTE);
        getInventory().extractItem(0, items, false);
        markForUpdate();
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
