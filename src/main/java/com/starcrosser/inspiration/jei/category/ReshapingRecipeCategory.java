package com.starcrosser.inspiration.jei.category;

import com.starcrosser.inspiration.jei.JeiLayout;
import com.starcrosser.inspiration.jei.MachineRecipeText;
import com.starcrosser.inspiration.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.machine.recipe.ReshapingRecipe;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 物质重塑机的 JEI 分类。
 * <p>
 * 结构为「主输入 [+ 辅助材料] → 输出」，单输入配方的第二个槽位留空；
 * 底部只给基准耗时与基准每刻耗电。
 */
public class ReshapingRecipeCategory implements IRecipeCategory<ReshapingRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public ReshapingRecipeCategory(IGuiHelper guiHelper) {
        this.background = JeiLayout.backgroundDrawable(guiHelper, "reshaping",
                JeiLayout.WIDTH, JeiLayout.HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModMachines.MATTER_RESHAPER.getBlock()));
    }

    @Override
    public RecipeType<ReshapingRecipe> getRecipeType() {
        return ModJeiRecipeTypes.RESHAPING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.inspiration.reshaping");
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
    public void setRecipe(IRecipeLayoutBuilder builder, ReshapingRecipe recipe, IFocusGroup focuses) {
        IRecipeSlotBuilder firstSlot = builder.addInputSlot(JeiLayout.FIRST_INPUT_X, JeiLayout.SLOT_Y)
                .setStandardSlotBackground();
        addCountedIngredient(firstSlot, recipe.input(), recipe.inputCount());

        IRecipeSlotBuilder secondSlot = builder.addInputSlot(JeiLayout.SECOND_INPUT_X, JeiLayout.SLOT_Y)
                .setStandardSlotBackground();
        Optional<Ingredient> secondInput = recipe.secondInput();
        secondInput.ifPresent(secondSlot::addIngredients);

        builder.addOutputSlot(JeiLayout.OUTPUT_X, JeiLayout.SLOT_Y)
                .setOutputSlotBackground()
                .addItemStack(recipe.result());
    }

    /** 一次消耗多个材料的配方，槽位按实际消耗数量展示，避免玩家误以为只要 1 个。 */
    private static void addCountedIngredient(IRecipeSlotBuilder slot, Ingredient ingredient, int count) {
        if (count <= 1) {
            slot.addIngredients(ingredient);
            return;
        }
        List<ItemStack> stacks = new ArrayList<>();
        for (ItemStack stack : ingredient.getItems()) {
            stacks.add(stack.copyWithCount(count));
        }
        slot.addItemStacks(stacks);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, ReshapingRecipe recipe, IFocusGroup focuses) {
        builder.addText(MachineRecipeText.baseLines(recipe.duration(), recipe.energyPerTick()),
                        JeiLayout.WIDTH - 12, 20)
                .setPosition(6, JeiLayout.TEXT_Y)
                .setTextAlignment(HorizontalAlignment.CENTER);
    }
}
