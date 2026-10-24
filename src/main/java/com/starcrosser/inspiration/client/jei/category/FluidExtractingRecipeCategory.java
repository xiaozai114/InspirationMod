package com.starcrosser.inspiration.client.jei.category;

import com.starcrosser.inspiration.client.jei.JeiLayout;
import com.starcrosser.inspiration.client.jei.JeiSlots;
import com.starcrosser.inspiration.client.jei.MachineRecipeText;
import com.starcrosser.inspiration.client.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.machine.recipe.FluidExtractingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 流体提取机的 JEI 分类。
 * <p>
 * 与别的机器分类唯一的区别是**产物是流体**：输出槽用流体渲染器（16x16 一格罐），
 * 不占用物品槽背景。产物量单独写成一行正文 —— 16x16 的格子放不下数字，
 * 玩家不悬停就看不到 144 mB 这个比例。
 * <p>
 * 输入侧仍然可能一次消耗多个（配方里的 {@code count}），走 {@link JeiSlots} 统一处理。
 */
public class FluidExtractingRecipeCategory implements IRecipeCategory<FluidExtractingRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public FluidExtractingRecipeCategory(IGuiHelper guiHelper) {
        this.background = JeiLayout.backgroundDrawable(guiHelper, "fluid_extracting",
                JeiLayout.WIDTH, JeiLayout.TALL_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModMachines.FLUID_EXTRACTOR.getBlock()));
    }

    @Override
    public RecipeType<FluidExtractingRecipe> getRecipeType() {
        return ModJeiRecipeTypes.FLUID_EXTRACTING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.inspiration.fluid_extracting");
    }

    @Override
    public int getWidth() {
        return JeiLayout.WIDTH;
    }

    @Override
    public int getHeight() {
        return JeiLayout.TALL_HEIGHT;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    /** 背景走 getBackground()：extras 里的 drawable 会在槽位与文字之后绘制，会盖住内容。 */
    @SuppressWarnings("removal")
    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FluidExtractingRecipe recipe, IFocusGroup focuses) {
        JeiSlots.input(builder, JeiLayout.SINGLE_INPUT_X, JeiLayout.SLOT_Y,
                recipe.input(), recipe.inputCount());
        builder.addOutputSlot(JeiLayout.OUTPUT_X, JeiLayout.SLOT_Y)
                .setFluidRenderer(recipe.result().getAmount(), false, 16, 16)
                .addFluidStack(recipe.result().getFluid(), recipe.result().getAmount());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, FluidExtractingRecipe recipe,
                                   IFocusGroup focuses) {
        List<FormattedText> lines = new ArrayList<>(
                MachineRecipeText.baseLines(recipe.duration(), recipe.energyPerTick()));
        lines.add(Component.translatable("jei.inspiration.fluid_extracting.output",
                recipe.result().getAmount()));

        builder.addText(lines, JeiLayout.WIDTH - 12, 24)
                .setPosition(6, JeiLayout.TALL_TEXT_Y)
                .setTextAlignment(HorizontalAlignment.CENTER);
    }
}
