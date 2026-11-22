package com.starcrosser.inspiration.client.jei.category;

import com.starcrosser.inspiration.client.jei.JeiLayout;
import com.starcrosser.inspiration.client.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.voidenergy.recipe.VoidCondensationInfo;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 虚空能量凝聚的 JEI 分类。
 * <p>
 * 凝聚器没有数据包配方（固定行为），所以这里展示的是一条静态说明：每刻吃多少 ve / 多少 FE、
 * 攒够多少 ve 出 1 个露珠。没有箭头，因为它不是「输入物品 → 输出物品」。
 */
public class VoidCondensationCategory implements IRecipeCategory<VoidCondensationInfo> {

    private final IDrawable background;
    private final IDrawable icon;

    public VoidCondensationCategory(IGuiHelper guiHelper) {
        this.background = JeiLayout.backgroundDrawable(guiHelper, "void_condensation",
                JeiLayout.VOID_WIDTH, JeiLayout.VOID_CONDENSATION_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModMachines.VOID_CONDENSER.getBlock()));
    }

    @Override
    public RecipeType<VoidCondensationInfo> getRecipeType() {
        return ModJeiRecipeTypes.VOID_CONDENSATION;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.inspiration.void_condensation");
    }

    @Override
    public int getWidth() {
        return JeiLayout.VOID_WIDTH;
    }

    @Override
    public int getHeight() {
        return JeiLayout.VOID_CONDENSATION_HEIGHT;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, VoidCondensationInfo info, IFocusGroup focuses) {
        builder.addOutputSlot(JeiLayout.CONDENSE_RESULT_X, JeiLayout.CONDENSE_RESULT_Y)
                .setOutputSlotBackground()
                .addItemStack(info.result());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, VoidCondensationInfo info, IFocusGroup focuses) {
        builder.addText(Component.translatable("jei.inspiration.void_condensation.intake",
                        info.vePerTick(), info.energyPerTick())
                        .withStyle(ChatFormatting.DARK_PURPLE),
                JeiLayout.VOID_BASE_X - 12, JeiLayout.CONDENSE_TEXT_Y);
        builder.addText(Component.translatable("jei.inspiration.void_condensation.ratio",
                        info.vePerDew(), info.ticksPerResult())
                        .withStyle(ChatFormatting.GRAY),
                JeiLayout.VOID_BASE_X - 12, JeiLayout.CONDENSE_TEXT_Y + 11);
    }
}
