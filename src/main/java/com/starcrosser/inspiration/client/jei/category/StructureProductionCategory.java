package com.starcrosser.inspiration.client.jei.category;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.starcrosser.inspiration.client.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import com.starcrosser.inspiration.multiblock.blockentity.StructureSimulatorTile;
import com.starcrosser.inspiration.multiblock.data.StructureDataItem;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.ScrollbarVisibility;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;

public final class StructureProductionCategory extends SimulationCategory<StructureDataItem> {
    public StructureProductionCategory(IGuiHelper gui) {
        super(gui, ModJeiRecipeTypes.STRUCTURE_PRODUCTION, ModMultiblocks.STRUCTURE_SIMULATOR.getBlock());
    }

    @Override
    public ResourceLocation getRegistryName(StructureDataItem card) {
        return BuiltInRegistries.ITEM.getKey(card);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, StructureDataItem card, IFocusGroup focuses) {
        var spec = card.spec();
        dataSlot(builder, card, spec.duration(), spec.energyPerTick(), StructureSimulatorTile.CHARGE_PER_TICK);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder extras, StructureDataItem card, IFocusGroup focuses) {
        var spec = card.spec();
        costs(extras, card, spec.duration(), spec.energyPerTick());
        extras.addText(Component.translatable("jei.inspiration.sim.charge", StructureSimulatorTile.CHARGE_PER_TICK),
                150, 10).setPosition(2, 37).setColor(0xff404040);
        extras.addText(Component.translatable("jei.inspiration.sim.loot"), 150, 10)
                .setPosition(2, 53).setColor(0xff382c51);
        Map<ResourceLocation, Integer> counts = new LinkedHashMap<>();
        for (var table : spec.lootTables()) {
            counts.merge(table, 1, Integer::sum);
        }
        var lines = new ArrayList<FormattedText>();
        counts.forEach((id, count) -> {
            String key = "jei.inspiration.loot." + id.toLanguageKey().replace('/', '.');
            var name = Component.translatableWithFallback(key, id.toString());
            lines.add(Component.translatable("jei.inspiration.sim.table_chance", name, count, spec.lootTables().size()));
        });
        extras.addScrollBoxWidget(150, 44, 2, 66)
                .setScrollbarVisibility(ScrollbarVisibility.AUTO_HIDE).setContents(lines);
    }
}
