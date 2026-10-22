package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.block.tile.MachineTile;
import com.starcrosser.inspiration.client.util.FacingLabels;
import com.starcrosser.inspiration.machine.plugin.PluginEffects;
import com.starcrosser.inspiration.machine.plugin.PluginEnergyStorage;
import com.starcrosser.inspiration.machine.plugin.PluginHost;
import com.starcrosser.inspiration.machine.plugin.PluginInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;

/**
 * 发电机的公共基类。
 * <p>
 * 与 {@link InspiredMachineTile}（用电机）是并列的两条链：发电机**不消耗灵感充能**，
 * 能量缓冲是「只出不进」（{@code maxReceive = 0}），除了自用之外还会主动推给相邻的方块。
 * 界面沿用同一套网格（能量条 (8,14)、燃料槽 (44,30)、进度条 (78,38)、输出槽 (116,39)），
 * 右侧同样是四个插件槽。
 * <p>
 * 子类只需回答四个问题：什么东西算燃料（{@link #isFuel} / {@link #fuelValue}）、
 * 一份烧多久（{@link #burnTicks}）、每刻发多少电（{@link #energyPerTick}）、
 * 以及缓冲与输出上限（{@link #baseEnergyCapacity} / {@link #baseMaxExtract}）。
 * 烧完要做的事（例如吐出核废料）与每分钟的副作用（例如释放辐射）分别覆写
 * {@link #onBurnComplete} 与 {@link #onGenerationMinute}。
 * <p>
 * 后四个方法会在**基类构造期间**被调用（{@link #createEnergyStorage} 与进度条构造），
 * 因此实现里只能返回常量，不能读子类字段。
 */
public abstract class PowerGeneratorTile<T extends PowerGeneratorTile<T>> extends MachineTile<T> {

    /** 能量条与侧向按钮的位置，与用电机保持一致。 */
    private static final int ENERGY_X = 8;
    private static final int ENERGY_Y = 14;
    private static final int FUEL_SLOT_X = 44;
    private static final int FUEL_SLOT_Y = 30;
    private static final int PROGRESS_X = 78;
    private static final int PROGRESS_Y = 38;
    private static final int FACING_HANDLER_Y = 84;
    private static final int FUEL_FACING_X = 8;

    /** 侧栏序号：燃料槽 0，输出槽（子类添加）顺延为 1。 */
    private static final int FUEL_SLOT_POS = 0;

    /** 每 1200 个「燃烧单位」结算一次每分钟副作用；一分钟 = 1200 刻。 */
    private static final int TICKS_PER_MINUTE = 1200;

    @Save
    private SidedInventoryComponent<T> fuelInventory;
    @Save
    private ProgressBarComponent<T> progressBar;

    /** 当前这份燃料的强度（碱性值或辐射值）；没在烧就是 0。 */
    private int burningValue;

    /** 每分钟副作用的累计器，单位是「燃烧单位」，速度插件会让它涨得更快。 */
    private int minuteCounter;

    /** 这份燃料已经烧掉的燃烧单位，与进度条同步增长，用来判断何时烧完。 */
    private int burnedTicks;

    private final PluginHost plugins;
    private PluginEffects pluginEffects = PluginEffects.NONE;

    protected PowerGeneratorTile(BasicTileBlock<T> block, BlockEntityType<?> type,
                                 BlockPos pos, BlockState state) {
        super(block, type, pos, state);

        SidedInventoryComponent<T> fuel = new SidedInventoryComponent<>(
                "fuel", FUEL_SLOT_X, FUEL_SLOT_Y, 1, FUEL_SLOT_POS);
        fuel.setRange(1, 1);
        fuel.setColor(DyeColor.ORANGE);
        fuel.setInputFilter((stack, slot) -> isFuel(stack));
        fuel.setFacingHandlerPos(FUEL_FACING_X, FACING_HANDLER_Y);
        this.fuelInventory = fuel;
        addInventory(this.fuelInventory);

        ProgressBarComponent<T> bar = new ProgressBarComponent<>(PROGRESS_X, PROGRESS_Y, burnTicks());
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        bar.setColor(DyeColor.ORANGE);
        bar.setCanIncrease(this::canBurn);
        bar.setCanReset(tile -> ((PowerGeneratorTile<?>) tile).isBurnFinished());
        bar.setOnStart(this::startBurn);
        bar.setOnTickWork(this::generate);
        bar.setOnFinishWork(this::finishBurn);
        this.progressBar = bar;
        addProgressBar(this.progressBar);

        PluginInventory.configure(getAugmentInventory());
        this.plugins = new PluginHost(getAugmentInventory(), this::applyPlugins);
    }

    /* ============================ 子类契约 ============================ */

    /** 该物品能不能当本机燃料。 */
    protected abstract boolean isFuel(ItemStack stack);

    /** 一份燃料的强度；碱性发电机取碱性值，RTG 取辐射值。 */
    protected abstract int fuelValue(ItemStack stack);

    /** 一份燃料烧多少刻。 */
    protected abstract int burnTicks();

    /** 强度为 {@code value} 的燃料每刻发多少电（不含插件加成）。 */
    protected abstract int energyPerTick(int value);

    /** 能量缓冲基准容量（不含插件加成）。 */
    protected abstract int baseEnergyCapacity();

    /** 每刻对外输出上限；要留得下满速时的产能，否则会被自己的输出卡住。 */
    protected abstract int baseMaxExtract();

    /** 一份燃料烧完时调用；默认什么都不产出。 */
    protected void onBurnComplete(int value) {
    }

    /**
     * 每烧满一分钟（1200 个燃烧单位）调用一次。
     * <p>
     * 用「燃烧单位」而不是真实刻数来计时，是因为速度插件会让燃烧更快：
     * 这样一来一份燃料在整个燃烧过程里触发的次数与插件无关，副作用总量保持一致。
     */
    protected void onGenerationMinute(int value) {
    }

    /** 开始烧之前预留产物空间；没有副产物的机器不用管。 */
    protected boolean canAcceptResult(int value) {
        return true;
    }

    /* ============================ 能量 ============================ */

    @Nonnull
    @Override
    protected PluginEnergyStorage<T> createEnergyStorage() {
        // 只出不进：maxReceive 为 0，能量只能往外推或被线缆抽走
        return new PluginEnergyStorage<>(baseEnergyCapacity(), 0, baseMaxExtract(), ENERGY_X, ENERGY_Y);
    }

    /**
     * 把缓冲里的电推给相邻的方块。
     * <p>
     * 与 Titanium 的 {@code GeneratorTile} 同一套做法：先按上限试抽，再按对方实际收下的量扣除。
     * 开发环境里没有线缆 mod 时，这就是发电机唯一的出路。
     */
    private void pushEnergy(Level level, BlockPos pos) {
        IEnergyStorage storage = getEnergyStorage();
        if (storage.getEnergyStored() <= 0) {
            return;
        }
        for (Direction direction : Direction.values()) {
            IEnergyStorage target = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                    pos.relative(direction), direction.getOpposite());
            if (target == null || !target.canReceive()) {
                continue;
            }
            int offer = storage.extractEnergy(baseMaxExtract(), true);
            if (offer <= 0) {
                return;
            }
            storage.extractEnergy(target.receiveEnergy(offer, false), false);
        }
    }

    /** 含速度倍率后每刻的实际产能；燃烧时间同步缩短，所以一份燃料的总发电量不变。 */
    private int productionPerTick(int value) {
        return energyPerTick(value) * pluginEffects.progressIncrease(1);
    }

    /* ============================ 燃烧流程 ============================ */

    /** 进度条是不是已经走完；{@code canReset} 只在走完时为真，跑到一半不会被打回去。 */
    private boolean isBurnFinished() {
        return progressBar.getProgress() >= progressBar.getMaxProgress();
    }

    /**
     * 能不能继续烧。
     * <p>
     * 燃料在**开工瞬间**就被扣掉了，所以已经在烧的时候不能再去看槽里有什么 ——
     * 否则进度条会立刻冻在第一格（燃料槽已经空了）。这里先认 {@link #burningValue}，
     * 只有它归零才回去查槽位、准备下一份燃料。
     */
    private boolean canBurn(T tile) {
        if (level == null || level.isClientSide) {
            return false;
        }
        int value = burningValue;
        if (value <= 0) {
            value = fuelValue(fuelInventory.getStackInSlot(0));
            if (value <= 0) {
                return false;
            }
            if (!canAcceptResult(value)) {
                return false;
            }
        }
        if (progressBar.getMaxProgress() != burnTicks()) {
            progressBar.setMaxProgress(burnTicks());
        }
        // 缓冲要留得下这一刻的产能，否则停下来等，避免燃料白白烧掉
        int room = getEnergyStorage().getMaxEnergyStored() - getEnergyStorage().getEnergyStored();
        return room >= productionPerTick(value);
    }

    /** 开工：吃掉一件燃料并记下它的强度，两个计数器同时归零。 */
    private void startBurn() {
        int value = fuelValue(fuelInventory.getStackInSlot(0));
        if (value <= 0) {
            return;
        }
        fuelInventory.extractItem(0, 1, false);
        this.burningValue = value;
        this.minuteCounter = 0;
        this.burnedTicks = 0;
        markForUpdate();
    }

    /** 每刻：发电 + 累计每分钟副作用。 */
    private void generate() {
        PluginEnergyStorage<T> storage = (PluginEnergyStorage<T>) getEnergyStorage();
        int produced = storage.getEnergyStored() + productionPerTick(burningValue);
        storage.setEnergyStored(Math.min(produced, storage.getMaxEnergyStored()));

        int step = pluginEffects.progressIncrease(1);
        minuteCounter += step;
        while (minuteCounter >= TICKS_PER_MINUTE) {
            minuteCounter -= TICKS_PER_MINUTE;
            onGenerationMinute(burningValue);
        }

        // 自己数烧了多少单位，不依赖进度条归零的那一步 ——
        // 缓冲满了的时候进度条会被外部直接打回 0、并且不会回调 onFinishWork。
        burnedTicks += step;
        if (burnedTicks >= burnTicks()) {
            finishBurn();
        }
    }

    /**
     * 收尾：产出副产物并清空强度。
     * <p>
     * 进度条走完与 {@link #generate} 里的计数都会调到这里，所以必须可重入：
     * 强度已经归零就直接返回，避免同一份燃料吐两次核废料。
     */
    private void finishBurn() {
        int value = burningValue;
        if (value <= 0) {
            return;
        }
        this.burningValue = 0;
        this.burnedTicks = 0;
        onBurnComplete(value);
        markForUpdate();
    }

    /** 当前这份燃料的强度，界面与调试用。 */
    public int currentFuelValue() {
        return burningValue;
    }

    /* ============================ 每刻与存档 ============================ */

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, T self) {
        super.serverTick(level, pos, state, self);
        plugins.refresh();
        pushEnergy(level, pos);
    }

    @Override
    public boolean isActive() {
        return progressBar != null && progressBar.getProgress() > 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("burningValue", burningValue);
        tag.putInt("minuteCounter", minuteCounter);
        tag.putInt("burnedTicks", burnedTicks);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.burningValue = tag.getInt("burningValue");
        this.minuteCounter = tag.getInt("minuteCounter");
        this.burnedTicks = tag.getInt("burnedTicks");
    }

    /* ============================ 插件结算 ============================ */

    /**
     * 倍率写回能量缓冲与进度条。
     * <p>
     * 速度插件对发电机的含义是「功率翻倍」：进度条每刻推进量与每刻产能同步放大，
     * 于是燃烧时间同比缩短、一份燃料的总发电量不变 —— 与高能机型「快一倍、总耗电不变」同一口径。
     */
    private void applyPlugins(PluginEffects effects) {
        this.pluginEffects = effects;
        if (getEnergyStorage() instanceof PluginEnergyStorage<?> storage) {
            storage.setCapacity(effects.energyCapacity(baseEnergyCapacity()));
        }
        progressBar.setProgressIncrease(effects.progressIncrease(1));
        markForUpdate();
    }

    /** 当前插件倍率，供调试与界面读取。 */
    public PluginEffects pluginEffects() {
        return pluginEffects;
    }

    /* ============================ 界面 ============================ */

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        FacingLabels.add(this, fuelInventory);
    }
}
