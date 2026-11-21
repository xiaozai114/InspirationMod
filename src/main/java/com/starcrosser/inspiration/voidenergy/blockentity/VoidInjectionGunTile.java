package com.starcrosser.inspiration.voidenergy.blockentity;

import com.starcrosser.inspiration.injection.InjectionOutcome;
import com.starcrosser.inspiration.injection.blockentity.InjectionGunTile;
import com.starcrosser.inspiration.injection.blockentity.MatterBinderTile;
import com.starcrosser.inspiration.recipe.ModRecipes;
import com.starcrosser.inspiration.voidenergy.VoidEnergyNetwork;
import com.starcrosser.inspiration.voidenergy.entity.VoidClusterEntity;
import com.starcrosser.inspiration.voidenergy.recipe.VoidInjectionRecipe;
import com.starcrosser.inspiration.voidenergy.recipe.VoidInjectionRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 虚空注入枪：与普通注入枪共用绑定、红石、激光、爆炸的整套流程，只把「配方来源」与「结算」换掉。
 * <p>
 * 唯一的能量来源是团簇：{@code cost = max(1, ve/10)}，拿 cost 与配方的窗口比 ——
 * 高于上限过载爆炸、低于下限不生效（材料照扣，与普通注入一致）、否则扣掉 cost。
 * 它没有 FE 缓冲，父类那条放电分支不会被走到。
 */
public class VoidInjectionGunTile extends InjectionGunTile {

    @Nullable
    private VoidInjectionRecipe activeVoidRecipe;

    public VoidInjectionGunTile(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    protected void startInjection(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide || remainingTicks > 0) {
            return;
        }
        MatterBinderTile binder = boundBinder();
        if (binder == null) {
            return;
        }
        ItemStack base = binder.getBase();
        if (base.isEmpty()) {
            return;
        }
        int structureTier = binder.structureTier();
        if (structureTier <= 0) {
            return;
        }
        RecipeHolder<VoidInjectionRecipe> holder = findRecipe(level, structureTier, base, injectSlot.getStackInSlot(0));
        if (holder == null) {
            return;
        }
        VoidClusterEntity cluster = VoidEnergyNetwork.findCluster(level, worldPosition);
        if (cluster == null) {
            return;
        }

        VoidInjectionRecipe recipe = holder.value();
        int cost = Math.max(1, cluster.energy() / 10);
        InjectionOutcome outcome = cost > recipe.maxVe() ? InjectionOutcome.OVERLOAD
                : cost < recipe.minVe() ? InjectionOutcome.INSUFFICIENT
                : InjectionOutcome.SUCCESS;
        if (outcome == InjectionOutcome.SUCCESS) {
            cluster.addEnergy(-cost);
        }

        binder.extractBase(1);
        if (!injectSlot.getStackInSlot(0).isEmpty()) {
            injectSlot.extractItem(0, 1, false);
        }

        this.activeVoidRecipe = recipe;
        this.activeRecipeId = holder.id();
        this.activeOutcome = outcome;
        this.remainingTicks = Math.max(1, recipe.duration());
        level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.6F, 1.6F);
        markForUpdate();
    }

    @Nullable
    private RecipeHolder<VoidInjectionRecipe> findRecipe(Level level, int structureTier,
                                                        ItemStack base, ItemStack inject) {
        VoidInjectionRecipeInput input = new VoidInjectionRecipeInput(base, inject);
        RecipeHolder<VoidInjectionRecipe> found = null;
        for (RecipeHolder<VoidInjectionRecipe> holder : level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.VOID_INJECTION_TYPE.get())) {
            VoidInjectionRecipe recipe = holder.value();
            if (recipe.tier() > structureTier || !recipe.matches(input, level)) {
                continue;
            }
            if (found == null || recipe.tier() > found.value().tier()) {
                found = holder;
            }
        }
        return found;
    }

    @Override
    protected boolean hasResolvedRecipe() {
        return activeVoidRecipe != null;
    }

    @Override
    protected boolean ensureRecipeResolved(Level level) {
        this.activeVoidRecipe = null;
        if (activeRecipeId == null) {
            return false;
        }
        for (RecipeHolder<VoidInjectionRecipe> holder : level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.VOID_INJECTION_TYPE.get())) {
            if (holder.id().equals(activeRecipeId)) {
                this.activeVoidRecipe = holder.value();
                return true;
            }
        }
        return false;
    }

    @Override
    protected ItemStack pendingResult() {
        return activeVoidRecipe == null ? ItemStack.EMPTY : activeVoidRecipe.result();
    }

    @Override
    protected void finishInjection(Level level, BlockPos pos, @Nullable MatterBinderTile binder) {
        super.finishInjection(level, pos, binder);
        this.activeVoidRecipe = null;
    }

    /** 供将来做 JEI / 提示用：当前这次注入对应的配方 id。 */
    @Nullable
    public ResourceLocation activeVoidRecipeId() {
        return activeRecipeId;
    }
}
