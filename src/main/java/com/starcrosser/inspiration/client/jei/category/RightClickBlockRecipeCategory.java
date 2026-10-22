package com.starcrosser.inspiration.client.jei.category;

import com.starcrosser.inspiration.client.jei.JeiLayout;
import com.starcrosser.inspiration.client.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.recipe.RightClickBlockRecipe;
import com.starcrosser.inspiration.util.BlockItemLookup;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 右键转化配方的 JEI 分类。
 * <p>
 * 左槽为目标方块，右槽为手持物品，右侧为转化结果。结果既可能是方块，也可能是掉落物。
 */
public class RightClickBlockRecipeCategory implements IRecipeCategory<RightClickBlockRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public RightClickBlockRecipeCategory(IGuiHelper guiHelper) {
        this.background = JeiLayout.backgroundDrawable(guiHelper, "right_click_block",
                JeiLayout.WIDTH, JeiLayout.HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.INSPIRATION.get()));
    }

    @Override
    public RecipeType<RightClickBlockRecipe> getRecipeType() {
        return ModJeiRecipeTypes.RIGHT_CLICK_BLOCK;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.inspiration.right_click_block");
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
    public void setRecipe(IRecipeLayoutBuilder builder, RightClickBlockRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder blockSlot = builder.addInputSlot(JeiLayout.FIRST_INPUT_X, JeiLayout.SLOT_Y)
                .setStandardSlotBackground();
        IRecipeSlotBuilder itemSlot = builder.addInputSlot(JeiLayout.SECOND_INPUT_X, JeiLayout.SLOT_Y)
                .setStandardSlotBackground();

        ItemStack blockIcon = BlockItemLookup.iconOf(recipe.inputBlock());
        if (!blockIcon.isEmpty()) {
            blockSlot.addItemStack(blockIcon);
        }
        if (!recipe.inputItem().isEmpty()) {
            itemSlot.addItemStack(recipe.inputItem());
        }

        ItemStack result = recipe.displayResult();
        if (!result.isEmpty()) {
            builder.addOutputSlot(JeiLayout.OUTPUT_X, JeiLayout.SLOT_Y)
                    .setOutputSlotBackground()
                    .addItemStack(result);
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RightClickBlockRecipe recipe, IFocusGroup focuses) {
        builder.addText(List.of(
                        Component.translatable("jei.inspiration.right_click_block.hint"),
                        Component.translatable("jei.inspiration.right_click_block.consume",
                                recipe.inputItem().getCount())),
                        JeiLayout.WIDTH - 12, 20)
                .setPosition(6, JeiLayout.TEXT_Y)
                .setTextAlignment(HorizontalAlignment.CENTER);
    }
}
