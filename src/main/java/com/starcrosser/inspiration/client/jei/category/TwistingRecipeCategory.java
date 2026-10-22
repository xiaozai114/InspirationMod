package com.starcrosser.inspiration.client.jei.category;

import com.starcrosser.inspiration.client.jei.JeiLayout;
import com.starcrosser.inspiration.client.jei.MachineRecipeText;
import com.starcrosser.inspiration.client.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.machine.recipe.TwistingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * 矿物扭曲机的 JEI 分类。
 * <p>
 * 结构为「矿石 -> 粗矿」，产量随等效时运随机，输出槽展示基准产量，
 * 产量区间与期望值挂在输出槽的 tooltip 上，面板正文只给基准耗时与基准每刻耗电。
 */
public class TwistingRecipeCategory implements IRecipeCategory<TwistingRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public TwistingRecipeCategory(IGuiHelper guiHelper) {
        this.background = JeiLayout.backgroundDrawable(guiHelper, "twisting",
                JeiLayout.WIDTH, JeiLayout.HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModMachines.MINERAL_TWISTER.getBlock()));
    }

    @Override
    public RecipeType<TwistingRecipe> getRecipeType() {
        return ModJeiRecipeTypes.TWISTING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.inspiration.twisting");
    }

    @Override
    public int getWidth() {
        return JeiLayout.WIDTH;
    }

    @Override
    public int getHeight() {
        return JeiLayout.HEIGHT;
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
    public void setRecipe(IRecipeLayoutBuilder builder, TwistingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(JeiLayout.SINGLE_INPUT_X, JeiLayout.SLOT_Y)
                .setStandardSlotBackground()
                .addIngredients(recipe.input());

        builder.addOutputSlot(JeiLayout.OUTPUT_X, JeiLayout.SLOT_Y)
                .setOutputSlotBackground()
                .addItemStack(recipe.result())
                // 产量随等效时运浮动，具体区间挂在产物 tooltip 上，面板正文只留耗时与耗电
                .addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable(
                        "jei.inspiration.twisting.fortune", recipe.fortuneLevel(),
                        recipe.minResultCount(), recipe.maxResultCount(),
                        formatAverage(recipe.averageResultCount()))));
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, TwistingRecipe recipe, IFocusGroup focuses) {
        builder.addText(MachineRecipeText.baseLines(recipe.duration(), recipe.energyPerTick()),
                        JeiLayout.WIDTH - 12, JeiLayout.HEIGHT - JeiLayout.TEXT_Y - 4)
                .setPosition(6, JeiLayout.TEXT_Y)
                .setTextAlignment(HorizontalAlignment.CENTER);
    }

    /** 期望产量保留一位小数，固定用点号作小数点以免受语言环境影响。 */
    private static String formatAverage(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }
}
