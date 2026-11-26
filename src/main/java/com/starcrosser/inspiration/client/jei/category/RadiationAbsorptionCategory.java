package com.starcrosser.inspiration.client.jei.category;

import com.starcrosser.inspiration.client.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.machine.blockentity.RadiationAbsorberTile;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class RadiationAbsorptionCategory implements IRecipeCategory<ItemStack> {
    private final IDrawable icon;

    public RadiationAbsorptionCategory(IGuiHelper gui) {
        icon = gui.createDrawableItemStack(new ItemStack(ModMachines.RADIATION_ABSORBER.getBlock()));
    }

    @Override
    public RecipeType<ItemStack> getRecipeType() {
        return ModJeiRecipeTypes.RADIATION_ABSORPTION;
    }

    @Override
    public Component getTitle() {
        return ModMachines.RADIATION_ABSORBER.getBlock().getName();
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
        return 83;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder layout, ItemStack waste, IFocusGroup focuses) {
        layout.addOutputSlot(69, 4).setOutputSlotBackground().addItemStack(waste);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder extras, ItemStack waste, IFocusGroup focuses) {
        extras.addText(Component.translatable("jei.inspiration.machine.energy_per_tick",
                RadiationAbsorberTile.FE_PER_TICK), 150, 12).setPosition(2, 26);
        extras.addText(Component.translatable("jei.inspiration.absorber.rate",
                RadiationAbsorberTile.TICKS_PER_POINT / 20), 150, 20).setPosition(2, 40);
        extras.addText(Component.translatable("jei.inspiration.absorber.waste",
                RadiationAbsorberTile.POINTS_PER_WASTE), 150, 20).setPosition(2, 61);
    }
}
