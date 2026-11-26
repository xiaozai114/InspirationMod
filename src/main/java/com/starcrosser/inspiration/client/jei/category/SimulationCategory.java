package com.starcrosser.inspiration.client.jei.category;

import java.text.NumberFormat;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

abstract class SimulationCategory<T> implements IRecipeCategory<T> {
    private final RecipeType<T> type;
    private final Block controller;
    private final IDrawable icon;

    protected SimulationCategory(IGuiHelper gui, RecipeType<T> type, Block controller) {
        this.type = type;
        this.controller = controller;
        this.icon = gui.createDrawableItemLike(controller);
    }

    @Override
    public RecipeType<T> getRecipeType() {
        return type;
    }

    @Override
    public Component getTitle() {
        return controller.getName();
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
        return 114;
    }

    protected void dataSlot(IRecipeLayoutBuilder builder, Item card, int ticks, int power, int charge) {
        builder.addSlot(RecipeIngredientRole.CATALYST, 3, 6).setStandardSlotBackground()
                .addItemStack(new ItemStack(card)).addRichTooltipCallback((slot, tip) -> {
                    tip.add(Component.translatable("jei.inspiration.sim.retained"));
                    tip.add(Component.translatable("jei.inspiration.sim.total_fe", number((long) ticks * power)));
                    if (charge > 0) {
                        tip.add(Component.translatable("jei.inspiration.sim.total_charge", number((long) ticks * charge)));
                    }
                });
    }

    protected void costs(IRecipeExtrasBuilder extras, Item card, int ticks, int power) {
        extras.addText(card.getDescription(), 128, 10).setPosition(24, 1).setColor(0xff382c51);
        extras.addText(Component.translatable("jei.inspiration.sim.retained"), 128, 10).setPosition(24, 12)
                .setColor(0xff404040);
        extras.addText(Component.translatable("jei.inspiration.sim.time_power", number(ticks), number(power)),
                150, 10).setPosition(2, 26).setColor(0xff404040);
    }

    protected static String number(long value) {
        return NumberFormat.getIntegerInstance().format(value);
    }
}
