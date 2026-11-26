package com.starcrosser.inspiration.client.jei.category;

import java.text.NumberFormat;

import com.starcrosser.inspiration.client.jei.MineralProductionPage;
import com.starcrosser.inspiration.client.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class MineralProductionCategory extends SimulationCategory<MineralProductionPage> {
    public MineralProductionCategory(IGuiHelper gui) {
        super(gui, ModJeiRecipeTypes.MINERAL_PRODUCTION, ModMultiblocks.MINERAL_SIMULATOR.getBlock());
    }

    @Override
    public ResourceLocation getRegistryName(MineralProductionPage page) {
        return BuiltInRegistries.ITEM.getKey(page.card()).withSuffix("/" + page.index());
    }

    private static int x(int index) { return 11 + index % 4 * 38; }
    private static int y(int index) { return 51 + index / 4 * 32; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MineralProductionPage page, IFocusGroup focuses) {
        var table = page.card().table();
        dataSlot(builder, page.card(), table.duration(), table.energyPerTick(), 0);
        for (int i = 0; i < page.outputs().size(); i++) {
            var ore = page.outputs().get(i);
            builder.addOutputSlot(x(i), y(i)).setStandardSlotBackground().addItemStack(ore.stack().get())
                    .addRichTooltipCallback((slot, tip) -> tip.add(Component.translatable(
                            "jei.inspiration.sim.weight", ore.weight(), table.totalWeight())));
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder extras, MineralProductionPage page, IFocusGroup focuses) {
        var table = page.card().table();
        costs(extras, page.card(), table.duration(), table.energyPerTick());
        extras.addText(Component.translatable("jei.inspiration.sim.one_ore"), 150, 10)
                .setPosition(2, 37).setColor(0xff382c51);
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(2);
        for (int i = 0; i < page.outputs().size(); i++) {
            var chance = (double) page.outputs().get(i).weight() / table.totalWeight();
            extras.addText(Component.literal(percent.format(chance)), 37, 10).setPosition(x(i) - 10, y(i) + 18)
                    .setTextAlignment(HorizontalAlignment.CENTER).setColor(0xff404040);
        }
    }
}
