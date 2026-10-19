package com.starcrosser.inspiration.injection.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.RotatableBlock;
import com.hrznstudio.titanium.block.tile.PoweredTile;
import com.hrznstudio.titanium.client.screen.addon.AssetScreenAddon;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.starcrosser.inspiration.injection.InjectionBeam;
import com.starcrosser.inspiration.injection.InjectionOutcome;
import com.starcrosser.inspiration.injection.InjectionStructure;
import com.starcrosser.inspiration.injection.InjectionWindow;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.injection.block.InjectionGunBlock;
import com.starcrosser.inspiration.injection.recipe.InjectionRecipe;
import com.starcrosser.inspiration.injection.recipe.InjectionRecipeInput;
import com.starcrosser.inspiration.machine.plugin.PluginEffects;
import com.starcrosser.inspiration.machine.plugin.PluginEnergyStorage;
import com.starcrosser.inspiration.machine.plugin.PluginHost;
import com.starcrosser.inspiration.machine.plugin.PluginInventory;
import com.starcrosser.inspiration.recipe.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * 注入枪的方块实体。
 * <p>
 * 职责有四：攒电（100k FE）、记住自己绑定的物质束缚器、在收到红石上升沿时发起一次注入，
 * 以及按插件槽里的插件改写自身能量上限。
 * <p>
 * 放电规则：只消耗被红石脉冲触发的那一支枪自身的存量，其余枪不受影响，
 * 因此玩家可以用「给哪一支枪发脉冲」来决定这次注入的总能量。
 * 材料在启动瞬间就扣除，之后无论成功、能量不足还是过载都不会退回。
 * <p>
 * 绑定之后枪口会自动转向束缚器（六个方向都支持），玩家不用自己摆正朝向。
 */
public class InjectionGunTile extends PoweredTile<InjectionGunTile> {

    /** 基础能量缓冲。 */
    public static final int ENERGY_CAPACITY = 100_000;

    /** 允许的最大输入功率，避免一格里就充满。 */
    public static final int MAX_ENERGY_INPUT = 20_000;

    /** 过载爆炸的强度，比 TNT 略小，避免把整片基地一起带走。 */
    private static final float OVERLOAD_RADIUS = 3.0F;

    /** 注入物质槽位在 GUI 中的位置。 */
    private static final int SLOT_X = 80;
    private static final int SLOT_Y = 35;

    /** 注入物质槽在侧栏中的序号，插件槽顺延。 */
    private static final int SLOT_POS = 0;
    private static final int PLUGIN_SLOT_POS = 1;

    @Save
    private SidedInventoryComponent<InjectionGunTile> injectSlot;

    @Save
    private SidedInventoryComponent<InjectionGunTile> pluginSlots;

    /** 插件倍率的结算器：注入枪只认能量容量类插件。 */
    private final PluginHost plugins;

    /** 绑定的物质束缚器位置；未绑定时为 null。 */
    @Nullable
    private BlockPos binderPos;

    /** 上一刻的红石状态，用来识别上升沿。 */
    private boolean powered;

    /** 正在进行的注入；空闲时为 null。 */
    @Nullable
    private InjectionRecipe activeRecipe;
    @Nullable
    private InjectionOutcome activeOutcome;
    private int remainingTicks;

    public InjectionGunTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<InjectionGunTile>) ModInjection.INJECTION_GUN.getBlock(),
                ModInjection.INJECTION_GUN.type().get(), pos, state);

        SidedInventoryComponent<InjectionGunTile> slot =
                new SidedInventoryComponent<>("inject", SLOT_X, SLOT_Y, 1, SLOT_POS);
        slot.setRange(1, 1);
        slot.setColor(DyeColor.LIME);
        slot.setFacingHandlerPos(8, 84);
        this.injectSlot = slot;
        addInventory(this.injectSlot);

        this.pluginSlots = PluginInventory.create(PLUGIN_SLOT_POS);
        addInventory(this.pluginSlots);
        this.plugins = new PluginHost(this.pluginSlots, this::applyPlugins);
    }

    @Override
    protected EnergyStorageComponent<InjectionGunTile> createEnergyStorage() {
        // 只进不出：储电只供自身放电，不能被线缆抽走
        // 用 PluginEnergyStorage 是为了让容量能被插件改写（取下插件时还会自动截断多余存量）
        return new PluginEnergyStorage<>(ENERGY_CAPACITY, MAX_ENERGY_INPUT, 0, 8, 14);
    }

    /**
     * 插件槽的背景底板。Titanium 只在 {@code MachineTile} 里画，这里得自己补一个。
     * <p>
     * 必须标 {@link OnlyIn}：底板 addon 属于客户端类，不加的话服务端做类校验时就会去加载它，
     * 触发 RuntimeDistCleaner 报错（参照 Titanium 自己的 {@code MachineTile#initClient}）。
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        addGuiAddonFactory(() -> new AssetScreenAddon(AssetTypes.AUGMENT_BACKGROUND,
                PluginInventory.BACKGROUND_X, PluginInventory.BACKGROUND_Y, true));
    }

    /* ============================ 插件 ============================ */

    /** 把插件倍率落到能量缓冲上：注入枪只用到容量这一项。 */
    private void applyPlugins(PluginEffects effects) {
        if (getEnergyStorage() instanceof PluginEnergyStorage<?> storage) {
            storage.setCapacity(effects.energyCapacity(ENERGY_CAPACITY));
        }
        markForUpdate();
    }

    /** 当前生效的插件倍率，供界面与调试读取。 */
    public PluginEffects pluginEffects() {
        return plugins.effects();
    }

    @Override
    public InjectionGunTile getSelf() {
        return this;
    }

    /* ============================ 绑定 ============================ */

    public boolean isBound() {
        return binderPos != null;
    }

    @Nullable
    public BlockPos getBinderPos() {
        return binderPos;
    }

    /** 绑定物质束缚器，并把枪口转向它。位置合法性由调用方（绑定器）先行校验。 */
    public void bind(BlockPos pos) {
        this.binderPos = pos.immutable();
        applyBlockState(true, InjectionGunBlock.facingTowards(worldPosition, pos));
        markForUpdate();
    }

    /** 解除绑定，朝向保持不变。 */
    public void unbind() {
        this.binderPos = null;
        applyBlockState(false, null);
        markForUpdate();
    }

    /**
     * 同步方块状态里的「已绑定」标记与朝向，一次调用只写一次方块。
     * 亮起的枪口贴图是玩家在世界上唯一能直接看到的绑定反馈。
     */
    private void applyBlockState(boolean linked, @Nullable Direction facing) {
        if (level == null) {
            return;
        }
        BlockState state = level.getBlockState(worldPosition);
        boolean changed = false;
        if (state.hasProperty(InjectionGunBlock.LINKED) && state.getValue(InjectionGunBlock.LINKED) != linked) {
            state = state.setValue(InjectionGunBlock.LINKED, linked);
            changed = true;
        }
        if (facing != null && state.hasProperty(RotatableBlock.FACING_ALL)
                && state.getValue(RotatableBlock.FACING_ALL) != facing) {
            state = state.setValue(RotatableBlock.FACING_ALL, facing);
            changed = true;
        }
        if (changed) {
            level.setBlockAndUpdate(worldPosition, state);
        }
    }

    /** 右键打开 GUI。Titanium 只在 {@code MachineTile} 里开 GUI，这里得自己接上。 */
    @Override
    public ItemInteractionResult onActivated(Player player, InteractionHand hand, Direction facing,
                                             double hitX, double hitY, double hitZ) {
        if (super.onActivated(player, hand, facing, hitX, hitY, hitZ) == ItemInteractionResult.SUCCESS) {
            return ItemInteractionResult.SUCCESS;
        }
        openGui(player);
        return ItemInteractionResult.SUCCESS;
    }

    /** 取回绑定的束缚器；绑定失效（被拆 / 超出范围 / 未加载）时返回 null。 */
    @Nullable
    public MatterBinderTile boundBinder() {
        if (level == null || binderPos == null) {
            return null;
        }
        if (!InjectionStructure.isInRange(worldPosition, binderPos)) {
            return null;
        }
        return level.getBlockEntity(binderPos) instanceof MatterBinderTile binder ? binder : null;
    }

    /* ============================ 每刻 ============================ */

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, InjectionGunTile self) {
        super.serverTick(level, pos, state, self);
        if (level.isClientSide) {
            return;
        }

        refreshBinding(level);
        plugins.refresh();
        int signal = level.getBestNeighborSignal(pos);
        if (signal > 0 && !powered) {
            startInjection(level, pos, state);
        }
        powered = signal > 0;
        if (remainingTicks > 0) {
            advanceInjection(level, pos, state);
        }
    }

    /** 束缚器被拆掉时自动解除绑定，避免方块状态一直亮着骗人；还在的话把枪口重新对准它。 */
    private void refreshBinding(Level level) {
        if (binderPos == null || !level.isLoaded(binderPos)) {
            return;
        }
        if (level.getBlockEntity(binderPos) instanceof MatterBinderTile) {
            applyBlockState(true, InjectionGunBlock.facingTowards(worldPosition, binderPos));
        } else {
            unbind();
        }
    }

    /* ============================ 注入流程 ============================ */

    private void startInjection(Level level, BlockPos pos, BlockState state) {
        if (remainingTicks > 0) {
            return;
        }
        MatterBinderTile binder = boundBinder();
        if (binder == null) {
            return;
        }
        ItemStack base = binder.getBase();
        ItemStack inject = injectSlot.getStackInSlot(0);
        if (base.isEmpty() || inject.isEmpty()) {
            return;
        }
        RecipeHolder<InjectionRecipe> holder = findRecipe(level, binder, base, inject);
        if (holder == null) {
            return;
        }
        InjectionRecipe recipe = holder.value();
        // 种子随机配方要拿存档种子现算窗口；算不出来（客户端）就不启动
        InjectionWindow window = InjectionWindow.of(level, holder);
        if (window == null) {
            return;
        }

        // 放电：无论判定结果如何，枪里存的 FE 都已被消耗
        int energy = getEnergyStorage().getEnergyStored();
        getEnergyStorage().setEnergyStored(0);

        // 材料在启动瞬间扣除，能量不足时就是「配方不会发生且损失材料」
        binder.extractBase(1);
        injectSlot.extractItem(0, 1, false);

        this.activeRecipe = recipe;
        this.activeOutcome = InjectionOutcome.of(energy, window.min(), window.max());
        this.remainingTicks = Math.max(1, recipe.duration());
        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.6F, 1.6F);
        markForUpdate();
    }

    /** 取等级合格且材料匹配的配方；同材料多等级并存时取等级最高的一条。 */
    @Nullable
    private RecipeHolder<InjectionRecipe> findRecipe(Level level, MatterBinderTile binder,
                                                     ItemStack base, ItemStack inject) {
        int structureTier = binder.structureTier();
        if (structureTier <= 0) {
            return null;
        }
        InjectionRecipeInput input = new InjectionRecipeInput(base, inject);
        RecipeHolder<InjectionRecipe> found = null;
        for (RecipeHolder<InjectionRecipe> holder : level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.INJECTION_TYPE.get())) {
            InjectionRecipe recipe = holder.value();
            if (recipe.tier() > structureTier || !recipe.matches(input, level)) {
                continue;
            }
            if (found == null || recipe.tier() > found.value().tier()) {
                found = holder;
            }
        }
        return found;
    }

    private void advanceInjection(Level level, BlockPos pos, BlockState state) {
        MatterBinderTile binder = boundBinder();
        Direction facing = state.hasProperty(RotatableBlock.FACING_ALL)
                ? state.getValue(RotatableBlock.FACING_ALL)
                : Direction.NORTH;
        if (binder != null) {
            InjectionBeam.beam(level, muzzle(pos, facing), Vec3.atCenterOf(binder.getBlockPos()));
        }
        if (--remainingTicks > 0) {
            return;
        }
        finishInjection(level, pos, binder);
    }

    private void finishInjection(Level level, BlockPos pos, @Nullable MatterBinderTile binder) {
        InjectionRecipe recipe = activeRecipe;
        InjectionOutcome outcome = activeOutcome;
        this.activeRecipe = null;
        this.activeOutcome = null;
        this.remainingTicks = 0;
        if (recipe == null || outcome == null) {
            return;
        }

        BlockPos target = binder != null ? binder.getBlockPos() : pos;
        Vec3 center = Vec3.atCenterOf(target).add(0.0, 0.35, 0.0);
        switch (outcome) {
            case SUCCESS -> succeed(level, binder, target, recipe, center);
            case INSUFFICIENT -> {
                InjectionBeam.fizzle(level, center);
                level.playSound(null, target, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.7F, 0.7F);
            }
            case OVERLOAD -> overload(level, center);
        }
        markForUpdate();
    }

    private void succeed(Level level, @Nullable MatterBinderTile binder, BlockPos target,
                         InjectionRecipe recipe, Vec3 center) {
        ItemStack result = recipe.result().copy();
        InjectionBeam.flash(level, center);
        level.playSound(null, target, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.7F, 1.8F);
        if (binder != null && binder.depositResult(result)) {
            return;
        }
        // 束缚器不在了或者槽位被占，产物直接掉在原地，不至于凭空消失
        Block.popResource(level, target.above(), result);
    }

    private void overload(Level level, Vec3 center) {
        if (level instanceof ServerLevel) {
            level.explode(null, center.x, center.y, center.z, OVERLOAD_RADIUS, Level.ExplosionInteraction.BLOCK);
        }
    }

    /** 枪口在方块表面外侧一点的位置，激光从这里出发。 */
    private static Vec3 muzzle(BlockPos pos, Direction facing) {
        return new Vec3(
                pos.getX() + 0.5 + facing.getStepX() * 0.55,
                pos.getY() + 0.5 + facing.getStepY() * 0.55,
                pos.getZ() + 0.5 + facing.getStepZ() * 0.55);
    }

    /* ============================ 持久化 ============================ */

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (binderPos != null) {
            tag.putLong("BinderPos", binderPos.asLong());
        }
        tag.putBoolean("Powered", powered);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.binderPos = tag.contains("BinderPos") ? BlockPos.of(tag.getLong("BinderPos")) : null;
        this.powered = tag.getBoolean("Powered");
    }
}
