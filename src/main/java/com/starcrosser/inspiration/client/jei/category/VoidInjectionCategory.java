package com.starcrosser.inspiration.client.jei.category;

import com.starcrosser.inspiration.client.jei.JeiLayout;
import com.starcrosser.inspiration.client.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.voidenergy.recipe.VoidInjectionRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 虚空注入的 JEI 分类：只吃团簇能量，界面写的是「能量的 10%」这个成本的上下限。
 * <p>
 * 注入物可空 —— 弱化核心、虚空等离子锭这类配方只有底材，槽位就留空。
 */
public class VoidInjectionCategory implements IRecipeCategory<VoidInjectionRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public VoidInjectionCategory(IGuiHelper guiHelper) {
        this.background = JeiLayout.backgroundDrawable(guiHelper, "void_injection",
                JeiLayout.VOID_WIDTH, JeiLayout.VOID_INJECTION_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModInjection.VOID_INJECTION_GUN.getBlock()));
    }

    @Override
    public RecipeType<VoidInjectionRecipe> getRecipeType() {
        return ModJeiRecipeTypes.VOID_INJECTION;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.inspiration.void_injection");
    }

    @Override
    public int getWidth() {
        return JeiLayout.VOID_WIDTH;
    }

    @Override
    public int getHeight() {
        return JeiLayout.VOID_INJECTION_HEIGHT;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, VoidInjectionRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(JeiLayout.VOID_BASE_X, JeiLayout.VOID_BASE_Y)
                .setStandardSlotBackground()
                .addIngredients(recipe.base());
        if (recipe.inject() != null) {
            builder.addInputSlot(JeiLayout.VOID_INJECT_X, JeiLayout.VOID_INJECT_Y)
                    .setStandardSlotBackground()
                    .addIngredients(recipe.inject());
        }
        builder.addOutputSlot(JeiLayout.VOID_RESULT_X, JeiLayout.VOID_RESULT_Y)
                .setOutputSlotBackground()
                .addItemStack(recipe.result());
    }

    @Override
    public void createRecipeExtras(mezz.jei.api.gui.widgets.IRecipeExtrasBuilder builder,
                                   VoidInjectionRecipe recipe, IFocusGroup focuses) {
        builder.addText(Component.translatable("jei.inspiration.void_injection.window")
                        .withStyle(ChatFormatting.DARK_PURPLE),
                JeiLayout.VOID_BASE_X - 12, JeiLayout.VOID_INJECTION_TEXT_Y);
        builder.addText(Component.translatable("jei.inspiration.void_injection.range",
                                recipe.minVe(), recipe.maxVe())
                        .withStyle(ChatFormatting.GRAY),
                JeiLayout.VOID_BASE_X - 12, JeiLayout.VOID_INJECTION_TEXT_Y + 11);
    }
}
