package com.starcrosser.inspiration.machine.blockentity;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.PoweredTile;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.starcrosser.inspiration.client.util.FacingLabels;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.radiation.RadiationSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class RadiationAbsorberTile extends PoweredTile<RadiationAbsorberTile> {
    public static final int FE_PER_TICK = 400;
    public static final int TICKS_PER_POINT = 1_200;
    public static final int POINTS_PER_WASTE = 64;

    @Save private SidedInventoryComponent<RadiationAbsorberTile> output;
    @Save private ProgressBarComponent<RadiationAbsorberTile> minute;
    @Save private ProgressBarComponent<RadiationAbsorberTile> absorbed;
    private final ChunkPos chunk;

    public RadiationAbsorberTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<RadiationAbsorberTile>) ModMachines.RADIATION_ABSORBER.getBlock(),
                ModMachines.RADIATION_ABSORBER.type().get(), pos, state);
        chunk = new ChunkPos(pos);
        output = new SidedInventoryComponent<>("output", 116, 39, 1, 0);
        output.setRange(1, 1);
        output.setInputFilter((stack, slot) -> false);
        output.setColor(DyeColor.LIME);
        output.setFacingHandlerPos(8, 84);
        addInventory(output);
        minute = counter(25, TICKS_PER_POINT, DyeColor.YELLOW);
        absorbed = counter(48, POINTS_PER_WASTE, DyeColor.LIME);
    }

    private ProgressBarComponent<RadiationAbsorberTile> counter(int y, int maximum, DyeColor color) {
        ProgressBarComponent<RadiationAbsorberTile> bar = new ProgressBarComponent<>(78, y, maximum);
        bar.setBarDirection(ProgressBarComponent.BarDirection.ARROW_RIGHT);
        bar.setColor(color);
        bar.setCanIncrease(tile -> false);
        bar.setCanReset(tile -> false);
        addProgressBar(bar);
        return bar;
    }

    @Override
    protected EnergyStorageComponent<RadiationAbsorberTile> createEnergyStorage() {
        return new EnergyStorageComponent<>(200_000, 10_000, 0, 8, 14);
    }

    @Override
    public RadiationAbsorberTile getSelf() {
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

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, RadiationAbsorberTile self) {
        super.serverTick(level, pos, state, self);
        if (!(level instanceof ServerLevel server) || getEnergyStorage().getEnergyStored() < FE_PER_TICK) {
            return;
        }
        RadiationSavedData radiation = RadiationSavedData.get(server);
        if (radiation.get(chunk) <= 0) {
            return;
        }
        boolean producesWaste = absorbed.getProgress() + 1 == POINTS_PER_WASTE;
        if (producesWaste && !output.insertItem(0, new ItemStack(ModItems.NUCLEAR_WASTE.get()), true).isEmpty()) {
            return;
        }
        getEnergyStorage().setEnergyStored(getEnergyStorage().getEnergyStored() - FE_PER_TICK);
        if (minute.getProgress() + 1 == TICKS_PER_POINT) {
            radiation.add(chunk, -1);
            minute.setProgress(0);
            if (producesWaste) {
                output.insertItem(0, new ItemStack(ModItems.NUCLEAR_WASTE.get()), false);
                absorbed.setProgress(0);
            } else {
                absorbed.setProgress(absorbed.getProgress() + 1);
            }
        } else {
            minute.setProgress(minute.getProgress() + 1);
        }
        setChanged();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        FacingLabels.add(this, output);
    }
}
