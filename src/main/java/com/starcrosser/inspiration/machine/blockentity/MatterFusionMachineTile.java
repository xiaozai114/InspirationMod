package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.machine.plugin.PluginEnergyStorage;
import com.starcrosser.inspiration.machine.recipe.FusionRecipe;
import com.starcrosser.inspiration.machine.recipe.FusionRecipeInput;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import com.starcrosser.inspiration.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


/**
 * 物质融合机一族的方块实体：物质融合机（等级 1）与强能物质融合机（等级 2）共用这一份实现。
 * <p>
 * 结构由 Titanium 的组件系统拼装：若干个输入槽、一个输出槽、一个能量缓冲与一个进度条，
 * 灵感充能槽与充能罐由 {@link InspiredMachineTile} 提供。
 * GUI 由 Titanium 的 {@code BasicAddonScreen} 依据这些组件自动生成。
 * <p>
 * **两档机器的差别全部由注册时的 {@link MachineSpec} 表达**：
 * 等级决定能量容量、输入功率、每刻耗电、充能消耗与运行速度（倍率 {@code 2^(tier-1)}），
 * 槽数决定一次能吃几种材料（2 槽 → 3 槽）。两者互相独立，所以本类里看不到任何
 * 「高能」分支 —— 将来加等级 3 只要再注册一份配置。
 */
public class MatterFusionMachineTile extends BatchProcessingTile<MatterFusionMachineTile> {

    /** tier 1 的能量缓冲容量（FE）。 */
    public static final int BASE_ENERGY_CAPACITY = 60_000;

    /** tier 1 的每刻最大输入功率（FE）。 */
    public static final int BASE_MAX_ENERGY_INPUT = 1_000;

    /** tier 1 每刻消耗的灵感充能（mB）。 */
    public static final int BASE_CHARGE_PER_TICK = 1;

    @Save
    private SidedInventoryComponent<MatterFusionMachineTile> inputInventory;
    @Save
    private SidedInventoryComponent<MatterFusionMachineTile> outputInventory;
    @Save
    private ProgressBarComponent<MatterFusionMachineTile> progressBar;

    private FusionRecipe cachedRecipe;

    public MatterFusionMachineTile(MachineSpec spec, BasicTileBlock<MatterFusionMachineTile> block,
                                   BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(spec, block, type, pos, state);

        int slots = spec.itemInputSlots();
        SidedInventoryComponent<MatterFusionMachineTile> inputs =
                new SidedInventoryComponent<>("input", 44, 30, slots, 0);
        inputs.setRange(1, slots);
        inputs.setColor(DyeColor.LIGHT_BLUE);
        inputs.setFacingHandlerPos(8, 84);
        this.inputInventory = inputs;
        addInventory(this.inputInventory);

        SidedInventoryComponent<MatterFusionMachineTile> outputs =
                new SidedInventoryComponent<>("output", 116, 39, 1, 1);
        outputs.setRange(1, 1);
        outputs.setColor(DyeColor.ORANGE);
        outputs.setInputFilter((stack, slot) -> false);
        outputs.setFacingHandlerPos(26, 84);
        this.outputInventory = outputs;
        addInventory(this.outputInventory);

        ProgressBarComponent<MatterFusionMachineTile> bar =
                new ProgressBarComponent<>(78, 38, FusionRecipe.DEFAULT_DURATION);
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        bar.setColor(spec.barColor());
        bar.setCanIncrease(this::canProcess);
        bar.setCanReset(tile -> tile.isFinished() || !tile.hasRecipe());
        bar.setOnTickWork(this::consumeBatchEnergy);
        bar.setOnFinishWork(this::finishCraft);
        this.progressBar = bar;
        addProgressBar(this.progressBar);
    }

    @Nonnull
    @Override
    protected PluginEnergyStorage<MatterFusionMachineTile> createEnergyStorage() {
        // 只进不出：maxExtract 为 0，能量只能被机器自身消耗。
        // 这里的容量是 tier 1 基准值，等级倍率由基类在构造末尾补正（见 InspiredMachineTile#applyTier）。
        return new PluginEnergyStorage<>(BASE_ENERGY_CAPACITY, BASE_MAX_ENERGY_INPUT, 0, 8, 14);
    }

    @Override
    public int chargePerTick() {
        return BASE_CHARGE_PER_TICK;
    }

    @Override
    protected int baseEnergyCapacity() {
        return BASE_ENERGY_CAPACITY;
    }

    @Override
    protected int maxEnergyInput() {
        return BASE_MAX_ENERGY_INPUT;
    }

    @Override
    public boolean isActive() {
        return progressBar != null && progressBar.getProgress() > 0;
    }

    @Nonnull
    @Override
    public MatterFusionMachineTile getSelf() {
        return this;
    }

    @Override
    public ProgressBarComponent<MatterFusionMachineTile> getProgressBar() {
        return progressBar;
    }

    /* ============================ 配方逻辑 ============================ */

    private boolean canProcess(MatterFusionMachineTile tile) {
        if (level == null || level.isClientSide) {
            return false;
        }
        FusionRecipe recipe = findRecipe();
        if (recipe == null) {
            return false;
        }
        return prepareBatch(recipe.duration(), recipe.energyPerTick(),
                n -> recipe.resolveSlots(inputStacks(), n) != null
                        && fitsOutput(outputInventory, 0, recipe.result(), n));
    }

    private void finishCraft() {
        if (level == null) {
            return;
        }
        FusionRecipe recipe = findRecipe();
        if (recipe == null) {
            return;
        }
        ItemStack result = batchOutput(recipe.getResultItem(level.registryAccess()));
        if (!outputInventory.insertItem(0, result.copy(), true).isEmpty()) {
            return;
        }
        int[] assignment = recipe.resolveSlots(inputStacks(), batchSize());
        if (assignment == null) {
            return;
        }
        outputInventory.insertItem(0, result, false);
        for (int material = 0; material < assignment.length; material++) {
            inputInventory.extractItem(assignment[material], recipe.countFor(material) * batchSize(), false);
        }
        markForUpdate();
    }

    private boolean hasRecipe() {
        return findRecipe() != null;
    }

    private boolean isFinished() {
        return progressBar.getProgress() >= progressBar.getMaxProgress();
    }

    /** 所有输入槽的当前内容，顺序与槽位序号一致。 */
    private List<ItemStack> inputStacks() {
        List<ItemStack> stacks = new ArrayList<>(inputInventory.getSlots());
        for (int slot = 0; slot < inputInventory.getSlots(); slot++) {
            stacks.add(inputInventory.getStackInSlot(slot));
        }
        return stacks;
    }

    private FusionRecipe findRecipe() {
        if (level == null || level.isClientSide) {
            return null;
        }
        List<ItemStack> stacks = inputStacks();
        FusionRecipeInput input = new FusionRecipeInput(stacks);
        if (stacks.stream().allMatch(ItemStack::isEmpty)) {
            cachedRecipe = null;
            return null;
        }
        if (cachedRecipe != null && cachedRecipe.matches(input, level)) {
            return cachedRecipe;
        }
        cachedRecipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.FUSION_TYPE.get(), input, level)
                .map(this::trackRecipe)
                .orElse(null);
        return cachedRecipe;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        addFacingLabels(inputInventory, outputInventory);
    }
}
