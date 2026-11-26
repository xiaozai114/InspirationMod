package com.starcrosser.inspiration.client.jei.category;

import com.lowdragmc.lowdraglib2.integration.xei.jei.ModularUIRecipeCategory;
import com.starcrosser.inspiration.client.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.client.jei.StructurePreview;
import com.starcrosser.inspiration.client.jei.preview.SimulatorPreviewPanel;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class StructurePreviewCategory extends ModularUIRecipeCategory<StructurePreview> {
    private final IDrawable icon;

    public StructurePreviewCategory(IGuiHelper gui) {
        super(SimulatorPreviewPanel::create);
        icon = gui.createDrawableItemLike(ModMultiblocks.STRUCTURE_SIMULATOR.getBlock());
    }

    @Override
    public RecipeType<StructurePreview> getRecipeType() {
        return ModJeiRecipeTypes.STRUCTURE_PREVIEW;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.inspiration.structure_preview");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return SimulatorPreviewPanel.WIDTH;
    }

    @Override
    public int getHeight() {
        return SimulatorPreviewPanel.HEIGHT;
    }

    @Override
    public ResourceLocation getRegistryName(StructurePreview recipe) {
        return BuiltInRegistries.BLOCK.getKey(recipe.kind().controller());
    }
}
