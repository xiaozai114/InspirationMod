package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.client.util.FacingLabels;
import com.starcrosser.inspiration.radiation.Radiation;
import com.starcrosser.inspiration.radiation.RadiationSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * RTG反应堆（放射性同位素电池）：烧放射性物质发电，代价是往区块里放辐射。
 * <p>
 * 一份放射性物质烧 24,000 刻（20 分钟），每刻发 {@code 1000 × 辐射值} FE；
 * 烧完按辐射值 x 吐出 x 个核废料。辐射值走数据地图
 * {@code data_maps/item/radiation.json}，所以任何带辐射的物品都能烧。
 * <p>
 * 每分钟（1200 个燃烧单位）结算一次副作用：向所在区块释放 {@code (x-1)^3} 点辐射；
 * x &gt; 9 时还有 {@code (x-9)/1000} 的概率当场爆炸 —— 高辐射燃料越快烧完越容易出事，
 * 这是刻意保留的风险，不要加保护。
 */
public class RtgGeneratorTile extends PowerGeneratorTile<RtgGeneratorTile> {

    /** 能量缓冲与每刻输出上限。 */
    public static final int ENERGY_CAPACITY = 4_000_000;
    public static final int MAX_EXTRACT = 64_000;

    /** 一份燃料烧多少刻，是碱性发电机的两倍。 */
    public static final int BURN_TICKS = 24_000;

    /** 每刻产电 = 1000 × 辐射值。 */
    public static final int FE_PER_RADIATION = 1_000;

    /** 爆炸阈值：辐射值超过这个值才开始有炸的可能。 */
    public static final int EXPLOSION_THRESHOLD = 9;

    /** 爆炸概率的分母：每分钟 {@code (x - 9) / 1000}。 */
    public static final int EXPLOSION_DIVISOR = 1_000;

    /** 爆炸威力，约为 TNT 的 1.5 倍。 */
    public static final float EXPLOSION_POWER = 6.0F;

    @Save
    private SidedInventoryComponent<RtgGeneratorTile> outputInventory;

    public RtgGeneratorTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<RtgGeneratorTile>) ModMachines.RTG_GENERATOR.getBlock(),
                ModMachines.RTG_GENERATOR.type().get(), pos, state);

        SidedInventoryComponent<RtgGeneratorTile> outputs =
                new SidedInventoryComponent<>("output", 116, 39, 1, 1);
        outputs.setRange(1, 1);
        outputs.setColor(DyeColor.LIME);
        outputs.setInputFilter((stack, slot) -> false);
        outputs.setFacingHandlerPos(26, 84);
        this.outputInventory = outputs;
        addInventory(this.outputInventory);
    }

    @Nonnull
    @Override
    public RtgGeneratorTile getSelf() {
        return this;
    }

    /**
     * 只收「每秒都在放」的辐射物（周期 = 1）。
     * <p>
     * 粒与尘埃的辐射写成「每 9 / 81 秒若干点」，平均速率只有锭的 1/9，
     * 但分子与锭相同 —— 若按分子判定，一枚铀粒会当成一枚铀锭来烧，明显不合理。
     * 所以这里按周期把它们挡在门外。
     */
    @Override
    protected boolean isFuel(ItemStack stack) {
        return Radiation.of(stack) > 0 && Radiation.denominator(stack) == 1;
    }

    @Override
    protected int fuelValue(ItemStack stack) {
        return Radiation.of(stack);
    }

    @Override
    protected int burnTicks(int fuelValue) {
        return BURN_TICKS;
    }

    @Override
    protected int energyPerTick(int value) {
        return FE_PER_RADIATION * value;
    }

    @Override
    protected int baseEnergyCapacity() {
        return ENERGY_CAPACITY;
    }

    @Override
    protected int baseMaxExtract() {
        return MAX_EXTRACT;
    }

    /** 核废料要在开工前就留出位置，否则烧到一半卡住。 */
    @Override
    protected boolean canAcceptResult(int value) {
        ItemStack current = outputInventory.getStackInSlot(0);
        if (current.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(current, waste(value))) {
            return false;
        }
        int limit = Math.min(outputInventory.getSlotLimit(0), waste(value).getMaxStackSize());
        return current.getCount() + value <= limit;
    }

    /** 烧完吐出 x 个核废料。 */
    @Override
    protected void onBurnComplete(int value) {
        if (value <= 0) {
            return;
        }
        outputInventory.insertItem(0, waste(value), false);
        markForUpdate();
    }

    /**
     * 每分钟：往区块里加 {@code (x-1)^3} 点辐射，并按 {@code (x-9)/1000} 掷一次爆炸。
     * <p>
     * 立方用 long 先算再截断，避免辐射值被数据包改大时溢出成负数。
     */
    @Override
    protected void onGenerationMinute(int value) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        long scaled = value - 1L;
        long release = scaled * scaled * scaled;
        if (release > 0) {
            RadiationSavedData.get(serverLevel)
                    .add(new ChunkPos(worldPosition), (int) Math.min(release, Integer.MAX_VALUE));
        }
        if (value > EXPLOSION_THRESHOLD
                && serverLevel.getRandom().nextFloat() < (value - EXPLOSION_THRESHOLD) / (float) EXPLOSION_DIVISOR) {
            serverLevel.explode(null,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                    EXPLOSION_POWER, Level.ExplosionInteraction.TNT);
        }
    }

    private static ItemStack waste(int count) {
        return new ItemStack(ModItems.NUCLEAR_WASTE.get(), count);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        FacingLabels.add(this, outputInventory);
    }
}
