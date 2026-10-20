package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.item.custom.DrillBitItem;
import com.starcrosser.inspiration.machine.plugin.PluginEnergyStorage;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * 地核钻探者的方块实体。
 * <p>
 * 与其它机器的差别在于：它不跑任何机器配方，作业内容是「把正下方的整条竖井掏到基岩」。
 * 一次作业的代价是一次性结清的 —— 全部电量 + 满罐灵感充能 + 1 点钻头耐久，
 * 因此这里没有每刻结算的逻辑，资源都在 {@link #startRun} 里扣：
 * <ul>
 *     <li>必须先充满电才能开工，而电容插件会改变容量上限，所以「充满」的含义随插件浮动；
 *         这正是电容降级插件在这台机器上的用处 —— 上限被压低后，攒满电量要容易得多。</li>
 *     <li>进度条的最大值由本轮的待挖格数决定（见 {@link #columnCount}），
 *         拖动过程中每一步对应竖井里的一格。</li>
 * </ul>
 * 掉落一律按精准采集结算（见 {@link #dropsFor}），并优先送进机器上方的容器，
 * 容器满了或压根没有容器时，剩余产物就地掉出。
 */
public class CoreDrillerTile extends InspiredMachineTile<CoreDrillerTile> {

    /** 能量缓冲：一次作业就要抽干，所以容量与输入功率都远高于其它机器。 */
    public static final int ENERGY_CAPACITY = 4_000_000;
    public static final int MAX_ENERGY_INPUT = 20_000;

    /** 一次作业消耗的灵感充能（mB），正好等于基准罐容量。 */
    public static final int CHARGE_COST = 16_000;

    /** 每挖掉一格需要的刻数；速度插件会按比例缩短。 */
    public static final int TICKS_PER_BLOCK = 2;

    /** 待挖格数的缓存刷新间隔（刻）。数一列最深 380 格，没必要每刻都数。 */
    private static final int COLUMN_CACHE_TICKS = 20;

    @Save
    private SidedInventoryComponent<CoreDrillerTile> drillSlot;
    @Save
    private ProgressBarComponent<CoreDrillerTile> progressBar;

    /** 下方竖井的扫描结果，20 刻刷新一次（见 {@link #COLUMN_CACHE_TICKS}）。 */
    private Column column;
    private int columnAge = COLUMN_CACHE_TICKS;

    /**
     * @param total 从机器下方一格数到第一个挖不动的方块为止的格数，进度条按它换算耗时
     * @param solid 其中真正有方块的格数，为 0 说明这条竖井已经掏空了，不必再开工
     */
    private record Column(int total, int solid) {

        boolean hasWork() {
            return solid > 0;
        }
    }

    /** 本次作业已经处理过的格数。 */
    private int processedBlocks;

    /** 精准采集用的虚拟镐，绑定于当前维度，懒创建。 */
    private ItemStack silkTool;

    public CoreDrillerTile(MachineSpec spec, BasicTileBlock<CoreDrillerTile> block,
                           BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(spec, block, type, pos, state);

        SidedInventoryComponent<CoreDrillerTile> slot =
                new SidedInventoryComponent<>("drill", 44, 30, 1, 0);
        slot.setRange(1, 1);
        slot.setColor(DyeColor.GRAY);
        slot.setInputFilter((stack, integer) -> stack.getItem() instanceof DrillBitItem);
        slot.setFacingHandlerPos(8, 84);
        this.drillSlot = slot;
        addInventory(this.drillSlot);

        // 向下走的进度条：没有输出槽，箭头就放在钻头槽正下方
        ProgressBarComponent<CoreDrillerTile> bar =
                new ProgressBarComponent<>(42, 54, TICKS_PER_BLOCK);
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_DOWN);
        bar.setColor(DyeColor.PURPLE);
        bar.setCanIncrease(this::canRun);
        bar.setCanReset(tile -> tile.isFinished() || !tile.canRun(tile));
        bar.setOnStart(this::startRun);
        bar.setOnTickWork(this::drillStep);
        bar.setOnFinishWork(this::endRun);
        this.progressBar = bar;
        addProgressBar(this.progressBar);
    }

    @Nonnull
    @Override
    protected PluginEnergyStorage<CoreDrillerTile> createEnergyStorage() {
        // 只进不出：maxExtract 为 0，能量只能被机器自身消耗
        return new PluginEnergyStorage<>(ENERGY_CAPACITY, MAX_ENERGY_INPUT, 0, 8, 14);
    }

    @Override
    public int chargePerTick() {
        // 这台机器不是按刻结账，而是一次作业扣满罐，所以这里恒为 0
        return 0;
    }

    @Override
    protected int baseEnergyCapacity() {
        return ENERGY_CAPACITY;
    }

    @Override
    protected int maxEnergyInput() {
        return MAX_ENERGY_INPUT;
    }

    @Override
    protected int baseChargeCapacity() {
        return CHARGE_COST;
    }

    @Override
    public boolean isActive() {
        return isRunning();
    }

    @Nonnull
    @Override
    public CoreDrillerTile getSelf() {
        return this;
    }

    @Override
    public ProgressBarComponent<CoreDrillerTile> getProgressBar() {
        return progressBar;
    }

    /* ============================ 作业流程 ============================ */

    /** 已经在作业中，或者资源与钻头都够再开一轮。 */
    private boolean canRun(CoreDrillerTile tile) {
        return tile.isRunning() || tile.canStartRun();
    }

    private boolean isRunning() {
        return progressBar != null && progressBar.getProgress() > 0;
    }

    private boolean isFinished() {
        return progressBar != null && progressBar.getProgress() >= progressBar.getMaxProgress();
    }

    /** 开工条件：钻头在位、电量与灵感充能都拉满、下方还有能挖的东西。 */
    private boolean canStartRun() {
        if (level == null || level.isClientSide) {
            return false;
        }
        ItemStack drill = drillSlot.getStackInSlot(0);
        if (!(drill.getItem() instanceof DrillBitItem)) {
            return false;
        }
        if (isEnergyFull() && hasCharge(CHARGE_COST)) {
            return columnCount().hasWork();
        }
        return false;
    }

    /**
     * 电量必须充满容量。
     * 容量会被插件改写，所以这里比的是当前上限而不是常量 —— 装了电容降级插件之后
     * 要攒满的电就少得多。
     */
    private boolean isEnergyFull() {
        int max = getEnergyStorage().getMaxEnergyStored();
        return max > 0 && getEnergyStorage().getEnergyStored() >= max;
    }

    /** 开工：定好本轮进度上限，并把三种消耗一次性结清。 */
    private void startRun() {
        if (level == null || level.isClientSide) {
            return;
        }
        Column scan = columnCount();
        if (!scan.hasWork()) {
            return;
        }
        processedBlocks = 0;
        progressBar.setMaxProgress(scan.total() * TICKS_PER_BLOCK);
        PluginEnergyStorage<CoreDrillerTile> storage =
                (PluginEnergyStorage<CoreDrillerTile>) getEnergyStorage();
        storage.setEnergyStored(0);
        consumeCharge(CHARGE_COST);
        ItemStack drill = drillSlot.getStackInSlot(0);
        // 耐久耗尽时原版会把这一格直接抽走，也就是钻头报废
        drill.hurtAndBreak(1, (ServerLevel) level, null, item -> {
        });
        markForUpdate();
    }

    /** 每刻追着进度挖：一格拍 {@link #TICKS_PER_BLOCK} 刻，速度插件加成会一次追好几格。 */
    private void drillStep() {
        if (level == null || level.isClientSide) {
            return;
        }
        int due = Math.min(columnCount().total(), progressBar.getProgress() / TICKS_PER_BLOCK);
        while (processedBlocks < due) {
            BlockPos target = getBlockPos().below(processedBlocks + 1);
            if (!dig(target)) {
                // 到底了（挖不动的方块），把进度直接推到终点触发收工
                progressBar.setProgress(progressBar.getMaxProgress());
                return;
            }
            processedBlocks++;
        }
    }

    private void endRun() {
        processedBlocks = 0;
        column = null;
        columnAge = COLUMN_CACHE_TICKS;
        markForUpdate();
    }

    /* ============================ 竖井遍历 ============================ */

    /**
     * 处理一格，返回能否继续往下。
     * 空气与流体原样穿过（不抽水也不填），挖不动的方块（基岩、末地传送门、
     * 各种只能在创造模式获取的方块）直接终止本轮作业。
     */
    private boolean dig(BlockPos target) {
        BlockState state = level.getBlockState(target);
        if (!isPassThrough(state, target)) {
            return false;
        }
        if (state.isAir() || !state.getFluidState().isEmpty()) {
            return true;
        }
        List<ItemStack> drops = dropsFor(state, target);
        level.removeBlock(target, false);
        deliver(drops);
        markForUpdate();
        return true;
    }

    /** 能否继续往下：空气、流体与一切可破坏方块都算能，挖不动的方块不行。 */
    private boolean isPassThrough(BlockState state, BlockPos target) {
        if (state.isAir()) {
            return true;
        }
        return state.getDestroySpeed(level, target) >= 0F;
    }

    /**
     * 扫一遍机器下方的竖井。
     * 空气与流体也算一格，是故意的：空洞同样要花时间穿过，而且这样
     * 「第 k 步 == 下方 k+1 格」这条对应关系能一路保持成立。
     * 扫描止于第一个挖不动的方块，也止于未加载的区块 —— 不为数格子去生成区块。
     */
    private Column scanColumn() {
        BlockPos origin = getBlockPos();
        int total = 0;
        int solid = 0;
        for (int y = origin.getY() - 1; y >= level.getMinBuildHeight(); y--) {
            BlockPos target = new BlockPos(origin.getX(), y, origin.getZ());
            if (!level.isLoaded(target)) {
                break;
            }
            BlockState state = level.getBlockState(target);
            if (!isPassThrough(state, target)) {
                break;
            }
            total++;
            if (!state.isAir() && state.getFluidState().isEmpty()) {
                solid++;
            }
        }
        return new Column(total, solid);
    }

    private Column columnCount() {
        if (column == null || columnAge >= COLUMN_CACHE_TICKS) {
            column = scanColumn();
            columnAge = 0;
        } else {
            columnAge++;
        }
        return column;
    }

    /* ============================ 掉落处理 ============================ */

    /**
     * 结算一格方块在精准采集下的掉落。
     * 两处刻意不走掉落表：刷怪笼不掉落自身（只在被破坏时给经验也不给物品），
     * 强化深板岩则必定掉落自身 —— 它在生存模式里的掉落表本来就是空的。
     */
    private List<ItemStack> dropsFor(BlockState state, BlockPos target) {
        if (state.is(Blocks.SPAWNER)) {
            return List.of();
        }
        if (state.is(Blocks.REINFORCED_DEEPSLATE)) {
            return List.of(new ItemStack(state.getBlock()));
        }
        ServerLevel serverLevel = (ServerLevel) level;
        return Block.getDrops(state, serverLevel, target, level.getBlockEntity(target),
                null, silkTool(serverLevel));
    }

    /** 一把带精准采集的虚拟镐，只用于构造战利品上下文。 */
    private ItemStack silkTool(ServerLevel serverLevel) {
        if (silkTool == null) {
            ItemStack tool = new ItemStack(Items.NETHERITE_PICKAXE);
            HolderLookup.RegistryLookup<Enchantment> enchantments =
                    serverLevel.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            mutable.set(enchantments.getOrThrow(Enchantments.SILK_TOUCH), 1);
            EnchantmentHelper.setEnchantments(tool, mutable.toImmutable());
            silkTool = tool;
        }
        return silkTool;
    }

    /** 优先塞进机器上方的容器，塞不下的就地掉落到机器上方那一格。 */
    private void deliver(List<ItemStack> drops) {
        BlockPos above = getBlockPos().above();
        List<ItemStack> leftovers = new ArrayList<>();
        IItemHandler target = level.isLoaded(above)
                ? level.getCapability(Capabilities.ItemHandler.BLOCK, above, null)
                : null;
        if (target == null) {
            leftovers.addAll(drops);
        } else {
            for (ItemStack drop : drops) {
                ItemStack rest = ItemHandlerHelper.insertItem(target, drop, false);
                if (!rest.isEmpty()) {
                    leftovers.add(rest);
                }
            }
        }
        for (ItemStack stack : leftovers) {
            Block.popResource(level, above, stack);
        }
    }

    /** 调试与自检用：钻头槽里的物品。 */
    public ItemStack getDrillStack() {
        return drillSlot.getStackInSlot(0);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        addFacingLabels(drillSlot);
    }
}
