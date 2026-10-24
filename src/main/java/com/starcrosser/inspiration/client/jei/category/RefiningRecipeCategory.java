package com.starcrosser.inspiration.client.jei.category;

import com.starcrosser.inspiration.client.jei.JeiLayout;
import com.starcrosser.inspiration.client.jei.JeiSlots;
import com.starcrosser.inspiration.client.jei.MachineRecipeText;
import com.starcrosser.inspiration.client.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.machine.recipe.RefiningRecipe;
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
 * 原料精炼机的 JEI 分类。
 * <p>
 * 三个输出槽竖排：最上面那格与输入槽同高、箭头正对着它，是主产物；
 * 下面两格是副产物，配方没写就空着。
 * <p>
 * 面板正文只给基准耗时与基准每刻耗电 —— 副产物会被回收升级翻倍，
 * 那是机器侧的事，写进面板会让玩家误以为配方本身就会产出那么多。
 */
public class RefiningRecipeCategory implements IRecipeCategory<RefiningRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public RefiningRecipeCategory(IGuiHelper guiHelper) {
        this.background = JeiLayout.backgroundDrawable(guiHelper, "refining",
                JeiLayout.WIDTH, JeiLayout.REFINING_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModMachines.RAW_REFINER.getBlock()));
    }

    @Override
    public RecipeType<RefiningRecipe> getRecipeType() {
        return ModJeiRecipeTypes.REFINING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.inspiration.refining");
    }

    @Override
    public int getWidth() {
        return JeiLayout.WIDTH;
    }

    @Override
    public int getHeight() {
        return JeiLayout.REFINING_HEIGHT;
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
    public void setRecipe(IRecipeLayoutBuilder builder, RefiningRecipe recipe, IFocusGroup focuses) {
        JeiSlots.input(builder, JeiLayout.SINGLE_INPUT_X, JeiLayout.SLOT_Y,
                recipe.input(), recipe.inputCount());

        // 第 0 格是主产物，与输入槽同高；副产物顺次排到下面两格
        builder.addOutputSlot(JeiLayout.REFINING_OUTPUT_X, JeiLayout.REFINING_OUTPUT_Y)
                .setOutputSlotBackground()
                .addItemStack(recipe.result());
        for (int index = 0; index < RefiningRecipe.MAX_BYPRODUCTS; index++) {
            ItemStack byproduct = recipe.byproduct(index);
            if (byproduct.isEmpty()) {
                continue;
            }
            int y = JeiLayout.REFINING_OUTPUT_Y + JeiLayout.REFINING_OUTPUT_PITCH * (index + 1);
            builder.addOutputSlot(JeiLayout.REFINING_OUTPUT_X, y)
                    .setOutputSlotBackground()
                    .addItemStack(byproduct)
                    .addRichTooltipCallback((view, tooltip) ->
                            tooltip.add(Component.translatable("jei.inspiration.refining.byproduct")));
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RefiningRecipe recipe,
                                   IFocusGroup focuses) {
        List<FormattedText> lines = new ArrayList<>(
                MachineRecipeText.baseLines(recipe.duration(), recipe.energyPerTick()));
        if (!recipe.byproducts().isEmpty()) {
            lines.add(Component.translatable("jei.inspiration.refining.recycling"));
        }
        builder.addText(lines, JeiLayout.WIDTH - 12, JeiLayout.REFINING_HEIGHT - JeiLayout.REFINING_TEXT_Y - 4)
                .setPosition(6, JeiLayout.REFINING_TEXT_Y)
                .setTextAlignment(HorizontalAlignment.CENTER);
    }
}
