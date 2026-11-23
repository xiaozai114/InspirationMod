package com.starcrosser.inspiration.client.jei.category;

import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.client.jei.JeiLayout;
import com.starcrosser.inspiration.client.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.client.jei.StructurePreview;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 结构模拟机的结构预览。
 * <p>
 * 4 宽 × 3 高 × 3 深逐层看最清楚，所以**每一层是一条配方**，翻页即翻层。
 * 每页画出这一层的 4×3 网格：{@code C} 位置放高能机器外壳的图标、{@code M} 放控制器，
 * 内部空腔那两格画一个淡色底块表示「这里不用放」（留空、填上都能成型）。
 * <p>
 * 网格用手绘（{@link #draw}）而不是槽位：结构要的是「格子挨着格子」的观感，
 * JEI 的标准槽位之间有间隙，摆出来更像零件清单而不是结构。
 */
public class StructurePreviewCategory implements IRecipeCategory<StructurePreview> {

    private static final int CELL = JeiLayout.STRUCTURE_CELL;
    private static final int PITCH = JeiLayout.STRUCTURE_PITCH;

    /** 空腔格的底色：很淡的一层暗，用来区分「不用放」与「漏画」。 */
    private static final int EMPTY_CELL = 0x22000000;

    private static final int TEXT_COLOR = 0x404040;

    private final IDrawable background;
    private final IDrawable icon;
    private final ItemStack casingStack;

    public StructurePreviewCategory(IGuiHelper guiHelper) {
        this.background = JeiLayout.backgroundDrawable(guiHelper, "structure_preview",
                JeiLayout.STRUCTURE_WIDTH, JeiLayout.STRUCTURE_HEIGHT);
        this.casingStack = new ItemStack(ModBlocks.HIGH_ENERGY_MACHINE_CASING.get());
        this.icon = guiHelper.createDrawableItemStack(
                com.starcrosser.inspiration.multiblock.MultiblockKind.STRUCTURE.controllerStack());
    }

    @Override
    public RecipeType<StructurePreview> getRecipeType() {
        return ModJeiRecipeTypes.STRUCTURE_PREVIEW;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.inspiration.structure_preview");
    }

    @Override
    public int getWidth() {
        return JeiLayout.STRUCTURE_WIDTH;
    }

    @Override
    public int getHeight() {
        return JeiLayout.STRUCTURE_HEIGHT;
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
    public void setRecipe(IRecipeLayoutBuilder builder, StructurePreview recipe, IFocusGroup focuses) {
        builder.addInputSlot(JeiLayout.STRUCTURE_SLOT_X, JeiLayout.STRUCTURE_SLOT_Y)
                .setStandardSlotBackground()
                .addItemStack(recipe.kind().controllerStack());
    }

    @Override
    public void draw(StructurePreview recipe, IRecipeSlotsView slotsView, GuiGraphics gui,
                     double mouseX, double mouseY) {
        int layer = recipe.layer();
        int height = recipe.kind().height();
        int width = recipe.kind().width();

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int x = JeiLayout.STRUCTURE_GRID_X + column * PITCH;
                // 图案的行自下而上，GUI 的 y 向下，所以要翻过来
                int y = JeiLayout.STRUCTURE_GRID_Y + (height - 1 - row) * PITCH;
                char symbol = recipe.kind().symbolAt(layer, row, column);
                if (symbol == ' ') {
                    gui.fill(x, y, x + CELL, y + CELL, EMPTY_CELL);
                } else {
                    gui.renderItem(symbol == 'M' ? recipe.kind().controllerStack() : casingStack, x, y);
                }
            }
        }

        Component label = Component.translatable("jei.inspiration.structure_preview.layer",
                layer + 1, recipe.kind().depth(),
                Component.translatable("jei.inspiration.structure_preview.layer." + layer));
        gui.drawString(Minecraft.getInstance().font, label,
                JeiLayout.STRUCTURE_TEXT_X, JeiLayout.STRUCTURE_TEXT_Y, TEXT_COLOR, false);
    }
}
