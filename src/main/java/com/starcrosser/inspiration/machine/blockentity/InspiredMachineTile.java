package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.MachineTile;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.starcrosser.inspiration.client.util.FacingLabels;
import com.starcrosser.inspiration.machine.charge.InspirationCharge;
import com.starcrosser.inspiration.machine.plugin.PluginEffects;
import com.starcrosser.inspiration.machine.plugin.PluginEnergyStorage;
import com.starcrosser.inspiration.machine.plugin.PluginFluidTank;
import com.starcrosser.inspiration.machine.plugin.PluginHost;
import com.starcrosser.inspiration.machine.plugin.PluginInventory;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * 需要消耗灵感充能、并且可以安装插件的机器的公共基类。
 * <p>
 * 在普通机器之上追加三样东西：一个只收充能来源物品（灵感 / 灵感碎片）的输入槽、
 * 一个只装灵感充能的罐体，以及机器右侧四个插件槽（Titanium 的 augment 槽）的倍率结算。
 * 服务器每刻把槽内的物品按 {@link InspirationCharge#chargeValue} 折算成充能，
 * 子类只需声明自己的每刻消耗速率，并在配方校验与结算时调用
 * {@link #hasCharge} / {@link #consumeCharge} 以及两个带插件加成取值方法。
 */
public abstract class InspiredMachineTile<T extends InspiredMachineTile<T>> extends MachineTile<T> {

    /** 充能槽在 GUI 中的位置：输出槽右侧。只接受能折算成灵感充能的物品。 */
    private static final int CHARGE_SLOT_X = 150;
    private static final int CHARGE_SLOT_Y = 74;

    /** 充能罐在 GUI 中的位置：与能量条同高，便于对比两种消耗品。 */
    private static final int CHARGE_TANK_X = 150;
    private static final int CHARGE_TANK_Y = 14;

    /** 侧向配置按钮排在同一行，输入槽占 8、输出槽占 26，这里顺延。 */
    private static final int FACING_HANDLER_Y = 84;
    private static final int CHARGE_SLOT_FACING_X = 44;
    private static final int CHARGE_TANK_FACING_X = 62;

    /** 侧栏序号：输入槽 0、输出槽 1，充能槽与充能罐顺延。 */
    private static final int CHARGE_SLOT_POS = 2;
    private static final int CHARGE_TANK_POS = 3;

    @Save
    private SidedInventoryComponent<T> inspirationSlot;
    @Save
    private SidedFluidTankComponent<T> chargeTank;

    /** 插件槽的倍率结算器。 */
    private final PluginHost plugins;

    /** 当前生效的插件倍率，由 {@link #applyPlugins} 写回。 */
    private PluginEffects pluginEffects = PluginEffects.NONE;

    /** 本机在注册时填的等级与槽数配置。 */
    private final MachineSpec spec;

    protected InspiredMachineTile(MachineSpec spec, BasicTileBlock<T> block, BlockEntityType<?> type,
                                  BlockPos pos, BlockState state) {
        super(block, type, pos, state);
        this.spec = spec;

        SidedInventoryComponent<T> slot = new SidedInventoryComponent<>(
                "inspiration", CHARGE_SLOT_X, CHARGE_SLOT_Y, 1, CHARGE_SLOT_POS);
        slot.setRange(1, 1);
        slot.setColor(DyeColor.PURPLE);
        slot.setInputFilter((stack, integer) -> InspirationCharge.isChargeSource(stack));
        slot.setFacingHandlerPos(CHARGE_SLOT_FACING_X, FACING_HANDLER_Y);
        this.inspirationSlot = slot;
        addInventory(this.inspirationSlot);

        // 罐头容量要能被灵感插件改写，并且把新容量同步给客户端，所以用 PluginFluidTank
        PluginFluidTank<T> tank = new PluginFluidTank<>(
                "inspiration_charge", InspirationCharge.TANK_CAPACITY,
                CHARGE_TANK_X, CHARGE_TANK_Y, CHARGE_TANK_POS);
        tank.setTankType(FluidTankComponent.Type.NORMAL);
        // 只进不出：允许管道灌入，但不允许被抽走
        tank.setTankAction(FluidTankComponent.Action.FILL);
        tank.setValidator(InspirationCharge::isChargeFluid);
        tank.setColor(DyeColor.MAGENTA);
        tank.setFacingHandlerPos(CHARGE_TANK_FACING_X, FACING_HANDLER_Y);
        this.chargeTank = tank;
        addTank(this.chargeTank);

        // Titanium 默认只允许带有 augment 数据组件的物品入槽，这里按本模组的插件要求重配一遍
        PluginInventory.configure(getAugmentInventory());
        this.plugins = new PluginHost(getAugmentInventory(), this::applyPlugins);

        // ⚠️ 必须放在构造体**最后**：能量缓冲是 PoweredTile 在构造期建的，那时 spec 还没赋值，
        // 只能先按「tier 1 基准值」建，等这里再补正。而充能罐是本类上面刚建好的字段，
        // 在 super() 之后立刻补正的话 chargeTank 还是 null。
        // 子类字段此刻同样还没赋值，所以 applyTier 用到的取值方法只允许返回静态常量。
        applyTier();
    }

    /**
     * 给充能槽与充能罐的侧向按钮补标签。子类要补自己那几个槽时，
     * 只要在覆写里沿着 {@link #addFacingLabels} 传进去。
     * <p>
     * 必须标 {@link OnlyIn}：addon 属于客户端类，不标的话服务端做类校验时会去加载它。
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        addFacingLabels(this.inspirationSlot, this.chargeTank);
    }

    /**
     * 给一组侧向组件各加一行按钮标签，标签内容按组件名查 {@code gui.inspiration.facing.<名字>}。
     * <p>
     * 必须标 {@link OnlyIn}，理由同 {@link #initClient}。
     */
    @OnlyIn(Dist.CLIENT)
    protected final void addFacingLabels(IFacingComponent... components) {
        FacingLabels.add(this, components);
    }

    /**
     * 本机每运行一刻消耗的灵感充能（mB），**tier 1 的基准值**。
     * ⚠️ 只准返回静态常量：本方法会在构造期被调用（见 {@link #applyTier}）。
     */
    public abstract int chargePerTick();

    /** 本机的进度条：插件要按倍率改写它的每刻推进量。 */
    protected abstract ProgressBarComponent<T> getProgressBar();

    /**
     * 本机的能量缓冲基准容量，**tier 1 的基准值**。
     * ⚠️ 只准返回静态常量：本方法会在构造期被调用（见 {@link #applyTier}）。
     */
    protected abstract int baseEnergyCapacity();

    /**
     * 本机每刻的最大输入功率，**tier 1 的基准值**。
     * ⚠️ 只准返回静态常量：本方法会在构造期被调用（见 {@link #applyTier}）。
     */
    protected abstract int maxEnergyInput();

    /**
     * 本机的灵感充能槽基准容量（mB），**tier 1 的基准值**；默认是 {@link InspirationCharge#TANK_CAPACITY}。
     * ⚠️ 只准返回静态常量：本方法会在构造期被调用（见 {@link #applyTier}）。
     */
    protected int baseChargeCapacity() {
        return InspirationCharge.TANK_CAPACITY;
    }

    /**
     * 本机的基准运行速度，**tier 1 的基准值**；默认是 1。
     * ⚠️ 只准返回静态常量：本方法会在构造期被调用（见 {@link #applyTier}）。
     */
    protected int baseSpeed() {
        return 1;
    }

    /* ============================ 等级换算 ============================ */

    /** 本机的等级与槽数配置。 */
    public final MachineSpec spec() {
        return spec;
    }

    /** 含等级加成的能量容量（不含插件加成）。 */
    protected final int tieredEnergyCapacity() {
        return spec.scale(baseEnergyCapacity());
    }

    /** 含等级加成后每刻的最大输入功率。 */
    protected final int tieredMaxEnergyInput() {
        return spec.scale(maxEnergyInput());
    }

    /** 含等级加成的灵感罐容量（mB）。 */
    protected final int tieredChargeCapacity() {
        return spec.scale(baseChargeCapacity());
    }

    /** 含等级加成的运行速度。 */
    protected final int tieredSpeed() {
        return spec.scale(baseSpeed());
    }

    /** 含等级加成后每刻消耗的灵感充能（mB）。 */
    protected final int tieredChargePerTick() {
        return spec.scale(chargePerTick());
    }

    /**
     * 把等级倍率补写到能量缓冲与灵感罐上。
     * <p>
     * 只在构造末尾调一次：因为 {@code PoweredTile} 在构造期就创建了能量缓冲，
     * 那时 {@link #spec} 还是 null，容量只能按基准值建。
     */
    private void applyTier() {
        if (getEnergyStorage() instanceof PluginEnergyStorage<?> storage) {
            storage.setCapacity(tieredEnergyCapacity());
            storage.setMaxReceive(tieredMaxEnergyInput());
        }
        setChargeCapacity(tieredChargeCapacity());
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, T self) {
        super.serverTick(level, pos, state, self);
        convertInspiration();
        refreshPlugins();
    }

    /**
     * 把槽内的灵感或灵感碎片折成充能。
     * 一次尽可能多转化，但不会超过罐体剩余容量，避免溢出丢失。
     */
    private void convertInspiration() {
        ItemStack stack = inspirationSlot.getStackInSlot(0);
        int perItem = InspirationCharge.chargeValue(stack);
        if (perItem <= 0) {
            return;
        }
        int room = chargeTank.getCapacity() - chargeTank.getFluidAmount();
        int items = Math.min(stack.getCount(), room / perItem);
        if (items <= 0) {
            return;
        }
        chargeTank.fill(InspirationCharge.stack(items * perItem), IFluidHandler.FluidAction.EXECUTE);
        inspirationSlot.extractItem(0, items, false);
        markForUpdate();
    }

    /* ============================ 插件结算 ============================ */

    /** 读取插件槽，内容变化时由 {@link PluginHost} 回调 {@link #applyPlugins}。 */
    private void refreshPlugins() {
        plugins.refresh();
    }

    /** 把当前倍率写回能量缓冲、灵感罐与进度条。等级倍率已含在三个 tiered 取值里。 */
    private void applyPlugins(PluginEffects effects) {
        this.pluginEffects = effects;
        if (getEnergyStorage() instanceof PluginEnergyStorage<?> storage) {
            storage.setCapacity(pluginEffects.energyCapacity(tieredEnergyCapacity()));
        }
        setChargeCapacity(pluginEffects.chargeCapacity(tieredChargeCapacity()));
        getProgressBar().setProgressIncrease(pluginEffects.progressIncrease(tieredSpeed()));
        markForUpdate();
    }

    /** 改写充能罐容量；容量变小时截断多余部分。 */
    private void setChargeCapacity(int capacity) {
        chargeTank.setCapacity(capacity);
        FluidStack fluid = chargeTank.getFluid();
        if (!fluid.isEmpty() && fluid.getAmount() > capacity) {
            chargeTank.setFluid(fluid.copyWithAmount(capacity));
        }
    }

    /** 当前插件倍率，供调试与界面读取。 */
    public PluginEffects pluginEffects() {
        return pluginEffects;
    }

    /** 含等级与插件加成后每刻实际消耗的灵感充能（mB）。 */
    protected int effectiveChargePerTick() {
        return pluginEffects.chargePerTick(tieredChargePerTick());
    }

    /**
     * 含等级与插件加成后每刻实际消耗的电量（FE）。
     * <p>
     * 传进来的 {@code base} 是**配方声明的基准值**（不带机器等级），
     * 等级倍率在这里统一乘上，所以上位机型不需要再自己写一份耗电倍率。
     */
    protected int effectiveEnergyPerTick(int base) {
        return pluginEffects.energyPerTick(spec.scale(base));
    }

    /* ============================ 充能读写 ============================ */

    /** 罐内充能是否够用。 */
    protected boolean hasCharge(int amount) {
        return chargeTank.getFluidAmount() >= amount;
    }

    /**
     * 扣除机器运行所需的充能。
     * 走 {@code drainForced} 而不是 {@code drain}，因为罐体被配置为只进不出，
     * 普通 {@code drain} 会被动作校验拦掉。
     */
    protected void consumeCharge(int amount) {
        chargeTank.drainForced(amount, IFluidHandler.FluidAction.EXECUTE);
    }

    /** 当前剩余充能（mB），供 GUI 或调试读取。 */
    public int getChargeAmount() {
        return chargeTank.getFluidAmount();
    }

    /** 当前充能上限（mB），含插件加成。 */
    public int getChargeCapacity() {
        return chargeTank.getCapacity();
    }

}
