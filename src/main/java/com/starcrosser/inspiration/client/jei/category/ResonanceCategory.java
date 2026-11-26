package com.starcrosser.inspiration.client.jei.category;

import com.starcrosser.inspiration.client.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.machine.recipe.ResonanceRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ResonanceCategory implements IRecipeCategory<ResonanceRecipe> {
    private final IDrawable icon;

    public ResonanceCategory(IGuiHelper gui) {
        icon = gui.createDrawableItemStack(new ItemStack(ModMachines.RESONANCE_CHAMBER.getBlock()));
    }

    @Override
    public RecipeType<ResonanceRecipe> getRecipeType() {
        return ModJeiRecipeTypes.RESONANCE;
    }

    @Override
    public Component getTitle() {
        return ModMachines.RESONANCE_CHAMBER.getBlock().getName();
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 154;
    }

    @Override
    public int getHeight() {
        return 80;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, ResonanceRecipe recipe, IFocusGroup focuses) {
        layout.addInputSlot(34, 4).setStandardSlotBackground().addIngredients(recipe.input());
        layout.addOutputSlot(104, 4).setOutputSlotBackground().addItemStack(recipe.result());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder extras, ResonanceRecipe recipe, IFocusGroup focuses) {
        extras.addAnimatedRecipeArrow(recipe.duration()).setPosition(65, 4);
        extras.addText(Component.translatable("jei.inspiration.machine.duration", recipe.duration()), 150, 10)
                .setPosition(2, 27);
        extras.addText(Component.translatable("jei.inspiration.resonance.cost", recipe.energyPerTick(),
                ResonanceRecipe.VE_PER_TICK), 150, 10).setPosition(2, 39);
        extras.addText(Component.translatable("jei.inspiration.resonance.failure"), 150, 26)
                .setPosition(2, 52).setColor(0xff772640);
    }
}
