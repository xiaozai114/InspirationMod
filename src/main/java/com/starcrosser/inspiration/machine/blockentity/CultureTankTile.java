package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.PoweredTile;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.component.fluid.SidedFluidTankComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.starcrosser.inspiration.client.util.FacingLabels;
import com.starcrosser.inspiration.fluid.ModFluids;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.annotation.Nonnull;

public class CultureTankTile extends PoweredTile<CultureTankTile> {

    public static final int ENERGY_CAPACITY = 400_000;
    public static final int MAX_ENERGY_INPUT = 20_000;
    public static final int NUTRIENT_CAPACITY = 16_000;

    @Save
    private SidedInventoryComponent<CultureTankTile> brainSlot;
    @Save
    private SidedInventoryComponent<CultureTankTile> outputSlot;
    @Save
    private SidedFluidTankComponent<CultureTankTile> nutrientTank;
    @Save
    private ProgressBarComponent<CultureTankTile> progressBar;

    public CultureTankTile(MachineSpec spec, BasicTileBlock<CultureTankTile> block,
                           BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(block, type, pos, state);

        SidedInventoryComponent<CultureTankTile> brain = new SidedInventoryComponent<>("brain", 44, 30, 1, 0);
        brain.setRange(1, 1);
        brain.setColor(DyeColor.LIME);
        brain.setInputFilter((stack, index) -> BrainKind.of(stack) != null);
        brain.setFacingHandlerPos(8, 84);
        this.brainSlot = brain;
        addInventory(this.brainSlot);

        SidedInventoryComponent<CultureTankTile> output = new SidedInventoryComponent<>("output", 116, 39, 1, 1);
        output.setRange(1, 1);
        output.setColor(DyeColor.LIME);
        output.setFacingHandlerPos(26, 84);
        this.outputSlot = output;
        addInventory(this.outputSlot);

        SidedFluidTankComponent<CultureTankTile> tank =
                new SidedFluidTankComponent<>("nutrient", NUTRIENT_CAPACITY, 26, 14, 2);
        tank.setTankType(FluidTankComponent.Type.NORMAL);
        tank.setTankAction(FluidTankComponent.Action.FILL);
        tank.setValidator(stack -> stack.getFluid() == ModFluids.Fluids.NUTRIENT_FLUID.get());
        tank.setColor(DyeColor.LIME);
        tank.setFacingHandlerPos(44, 84);
        this.nutrientTank = tank;
        addTank(this.nutrientTank);

        ProgressBarComponent<CultureTankTile> bar =
                new ProgressBarComponent<>(78, 38, BrainKind.VILLAGER.ticks);
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        bar.setColor(spec.barColor());
        bar.setCanIncrease(this::canWork);
        bar.setCanReset(tile -> tile.brainKind() == null || tile.progressBar.getProgress() >= tile.progressBar.getMaxProgress());
        bar.setOnTickWork(this::work);
        bar.setOnFinishWork(this::produce);
        this.progressBar = bar;
        addProgressBar(this.progressBar);
    }

    @Nonnull
    @Override
    protected EnergyStorageComponent<CultureTankTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(ENERGY_CAPACITY, MAX_ENERGY_INPUT, 0, 8, 14);
    }

    @Nonnull
    @Override
    public CultureTankTile getSelf() {
        return this;
    }

    @Override
    public ItemInteractionResult onActivated(Player player, InteractionHand hand, Direction facing,
                                             double hitX, double hitY, double hitZ) {
        if (level != null && !level.isClientSide) {
            openGui(player);
        }
        return ItemInteractionResult.SUCCESS;
    }

    public int getNutrientAmount() {
        return nutrientTank.getFluidAmount();
    }

    public IFluidHandler nutrientHandler() {
        return nutrientTank;
    }

    private BrainKind brainKind() {
        return BrainKind.of(brainSlot.getStackInSlot(0));
    }

    private boolean canWork(CultureTankTile tile) {
        if (level == null || level.isClientSide) {
            return false;
        }
        BrainKind kind = brainKind();
        if (kind == null) {
            return false;
        }
        if (progressBar.getMaxProgress() != kind.ticks) {
            progressBar.setMaxProgress(kind.ticks);
        }
        if (getEnergyStorage().getEnergyStored() < kind.energy) {
            return false;
        }
        if (!canOutput(kind)) {
            return false;
        }
        if (kind.deactivates) {
            return true;
        }
        return nutrientTank.getFluidAmount() >= kind.nutrient;
    }

    private boolean canOutput(BrainKind kind) {
        ItemStack current = outputSlot.getStackInSlot(0);
        ItemStack result = new ItemStack(kind.result.get());
        if (current.isEmpty()) {
            return true;
        }
        return ItemStack.isSameItemSameComponents(current, result)
                && current.getCount() + result.getCount() <= outputSlot.getSlotLimit(0);
    }

    private void work() {
        BrainKind kind = brainKind();
        if (kind == null) {
            return;
        }
        if (nutrientTank.getFluidAmount() < kind.nutrient) {
            if (kind.deactivates) {
                deactivate();
            }
            return;
        }
        nutrientTank.drainForced(kind.nutrient, IFluidHandler.FluidAction.EXECUTE);
        EnergyStorageComponent<CultureTankTile> storage = getEnergyStorage();
        storage.setEnergyStored(storage.getEnergyStored() - kind.energy);
    }

    private void produce() {
        BrainKind kind = brainKind();
        if (kind == null) {
            return;
        }
        outputSlot.insertItem(0, new ItemStack(kind.result.get()), false);
        markForUpdate();
    }

    private void deactivate() {
        brainSlot.setStackInSlot(0, new ItemStack(ModItems.INACTIVE_BRAIN.get()));
        progressBar.setProgress(0);
        markForUpdate();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        addLabels(brainSlot, outputSlot, nutrientTank);
    }

    @OnlyIn(Dist.CLIENT)
    private void addLabels(IFacingComponent... components) {
        FacingLabels.add(this, components);
    }

    private enum BrainKind {
        VILLAGER(ModItems.VILLAGER_BRAIN, 1, 100, 200, ModItems.INSPIRATION, true),
        ENDERMAN(ModItems.ENDERMAN_BRAIN, 10, 1_000, 400, ModItems.MIND_CRYSTAL, false);

        private final DeferredItem<Item> brain;
        private final int nutrient;
        private final int energy;
        private final int ticks;
        private final DeferredItem<Item> result;
        private final boolean deactivates;

        BrainKind(DeferredItem<Item> brain, int nutrient, int energy, int ticks,
                  DeferredItem<Item> result, boolean deactivates) {
            this.brain = brain;
            this.nutrient = nutrient;
            this.energy = energy;
            this.ticks = ticks;
            this.result = result;
            this.deactivates = deactivates;
        }

        private static BrainKind of(ItemStack stack) {
            for (BrainKind kind : values()) {
                if (stack.is(kind.brain.get())) {
                    return kind;
                }
            }
            return null;
        }
    }
}
