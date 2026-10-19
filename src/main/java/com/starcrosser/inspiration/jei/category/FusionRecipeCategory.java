package com.starcrosser.inspiration.jei.category;

import com.starcrosser.inspiration.jei.JeiLayout;
import com.starcrosser.inspiration.jei.MachineRecipeText;
import com.starcrosser.inspiration.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.machine.recipe.FusionRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * 物质融合器的 JEI 分类。
 * <p>
 * 展示输入槽（三材料的配方多一个槽位，摆在两槽正下方）、一个输出槽，
 * 以及底部的基准耗时与基准每刻耗电。
 * 需要多个的槽位直接画出数字（见 {@link #addInput}）。
 * 有第三输入时面板要加高一行，因此统一用 {@link JeiLayout#TALL_HEIGHT}，
 * 对应的背景贴图也是高版。
 */
public class FusionRecipeCategory implements IRecipeCategory<FusionRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public FusionRecipeCategory(IGuiHelper guiHelper) {
        // 背景尺寸必须与 getWidth()/getHeight() 一致，否则会被拉伸或截掉一块
        this.background = JeiLayout.backgroundDrawable(guiHelper, "fusion",
                JeiLayout.WIDTH, JeiLayout.TALL_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModMachines.MATTER_FUSION_MACHINE.getBlock()));
    }

    @Override
    public RecipeType<FusionRecipe> getRecipeType() {
        return ModJeiRecipeTypes.FUSION;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.inspiration.fusion");
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
    public void setRecipe(IRecipeLayoutBuilder builder, FusionRecipe recipe, IFocusGroup focuses) {
        addInput(builder, JeiLayout.FIRST_INPUT_X, JeiLayout.SLOT_Y, recipe.firstInput(), recipe.firstCount());
        addInput(builder, JeiLayout.SECOND_INPUT_X, JeiLayout.SLOT_Y, recipe.secondInput(), recipe.secondCount());
        builder.addOutputSlot(JeiLayout.OUTPUT_X, JeiLayout.SLOT_Y)
                .setOutputSlotBackground()
                .addItemStack(recipe.result());
        // 三材料配方（思维矩阵锭 / 末地合金）：第三个输入槽摆在两槽下方，只有强能机型有这个槽位
        if (recipe.hasThirdInput()) {
            addInput(builder, JeiLayout.THIRD_INPUT_X, JeiLayout.THIRD_INPUT_Y,
                    recipe.thirdInput(), recipe.thirdCount());
        }
    }

    /**
     * 输入槽：一次要消耗多个时改用带数量的 {@link ItemStack} 展示 ——
     * JEI 会在图标角上画出数字，玩家一眼能看到「这里要攒 4 个」；
     * 只消耗 1 个时照旧交 {@link Ingredient}，保留标签类原料的多物品轮播。
     */
    private static void addInput(IRecipeLayoutBuilder builder, int x, int y, Ingredient ingredient, int count) {
        IRecipeSlotBuilder slot = builder.addInputSlot(x, y).setStandardSlotBackground();
        if (count > 1) {
            slot.addItemStacks(Arrays.stream(ingredient.getItems())
                    .map(stack -> stack.copyWithCount(count))
                    .toList());
        } else {
            slot.addIngredients(ingredient);
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, FusionRecipe recipe, IFocusGroup focuses) {
        builder.addText(MachineRecipeText.baseLines(recipe.duration(), recipe.energyPerTick()),
                        JeiLayout.WIDTH - 12, 20)
                .setPosition(6, JeiLayout.TALL_TEXT_Y)
                .setTextAlignment(HorizontalAlignment.CENTER);
    }
}
