package com.starcrosser.inspiration.client.jei.category;

import com.starcrosser.inspiration.client.jei.JeiLayout;
import com.starcrosser.inspiration.client.jei.JeiSlots;
import com.starcrosser.inspiration.client.jei.MachineRecipeText;
import com.starcrosser.inspiration.client.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.machine.recipe.FusionRecipe;
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
        JeiSlots.input(builder, JeiLayout.FIRST_INPUT_X, JeiLayout.SLOT_Y,
                recipe.firstInput(), recipe.firstCount());
        JeiSlots.input(builder, JeiLayout.SECOND_INPUT_X, JeiLayout.SLOT_Y,
                recipe.secondInput(), recipe.secondCount());
        builder.addOutputSlot(JeiLayout.OUTPUT_X, JeiLayout.SLOT_Y)
                .setOutputSlotBackground()
                .addItemStack(recipe.result());
        // 三材料配方（思维矩阵锭 / 末地合金）：第三个输入槽摆在两槽下方，只有强能机型有这个槽位
        if (recipe.hasThirdInput()) {
            JeiSlots.input(builder, JeiLayout.THIRD_INPUT_X, JeiLayout.THIRD_INPUT_Y,
                    recipe.thirdInput(), recipe.thirdCount());
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
