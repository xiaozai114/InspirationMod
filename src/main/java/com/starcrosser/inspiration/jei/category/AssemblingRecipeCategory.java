package com.starcrosser.inspiration.jei.category;

import com.starcrosser.inspiration.jei.JeiLayout;
import com.starcrosser.inspiration.jei.JeiSlots;
import com.starcrosser.inspiration.jei.MachineRecipeText;
import com.starcrosser.inspiration.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.machine.recipe.AssemblingRecipe;
import com.starcrosser.inspiration.machine.recipe.FluidRequirement;
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
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 灵感装配台的 JEI 分类（普通机型与高能机型共用同一套配方）。
 * <p>
 * 材料最多六种：前四种摆成 2x2，第五、六种顺延到第三列；顺序与槽位无关。
 * 流体原料最多两种，摆在输入区下方的两个罐格里（高能机型才有两个流体槽，
 * 因此两条流体的配方只有高能装配台能做）。底部只给基准耗时与基准每刻耗电。
 */
public class AssemblingRecipeCategory implements IRecipeCategory<AssemblingRecipe> {

    /** 输入区六个槽位的偏移：先填满 2x2，再落到第三列。 */
    private static final int[][] SLOT_POSITIONS = {
            {JeiLayout.GRID_COLUMN_X, JeiLayout.GRID_ROW_Y},
            {JeiLayout.GRID_COLUMN_X2, JeiLayout.GRID_ROW_Y},
            {JeiLayout.GRID_COLUMN_X, JeiLayout.GRID_ROW_Y2},
            {JeiLayout.GRID_COLUMN_X2, JeiLayout.GRID_ROW_Y2},
            {JeiLayout.GRID_COLUMN_X3, JeiLayout.GRID_ROW_Y},
            {JeiLayout.GRID_COLUMN_X3, JeiLayout.GRID_ROW_Y2},
    };

    /** 两个流体原料罐的横坐标。 */
    private static final int[] FLUID_TANK_X = {JeiLayout.FLUID_TANK_X, JeiLayout.FLUID_TANK_X2};

    private final IDrawable background;
    private final IDrawable icon;

    public AssemblingRecipeCategory(IGuiHelper guiHelper) {
        this.background = JeiLayout.backgroundDrawable(guiHelper, "assembling",
                JeiLayout.GRID_WIDTH, JeiLayout.GRID_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModMachines.INSPIRATION_ASSEMBLER.getBlock()));
    }

    @Override
    public RecipeType<AssemblingRecipe> getRecipeType() {
        return ModJeiRecipeTypes.ASSEMBLING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.inspiration.assembling");
    }

    @Override
    public int getWidth() {
        return JeiLayout.GRID_WIDTH;
    }

    @Override
    public int getHeight() {
        return JeiLayout.GRID_HEIGHT;
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
    public void setRecipe(IRecipeLayoutBuilder builder, AssemblingRecipe recipe, IFocusGroup focuses) {
        int count = Math.min(recipe.ingredients().size(), SLOT_POSITIONS.length);
        for (int index = 0; index < count; index++) {
            // 走 JeiSlots：一次要消耗多个材料时（例如沉船数据要 64 个木板）在图标角画出数量
            JeiSlots.input(builder, SLOT_POSITIONS[index][0], SLOT_POSITIONS[index][1],
                    recipe.ingredients().get(index), recipe.countOf(index));
        }
        builder.addOutputSlot(JeiLayout.GRID_OUTPUT_X, JeiLayout.GRID_OUTPUT_Y)
                .setOutputSlotBackground()
                .addItemStack(recipe.result());

        List<FluidRequirement> fluids = recipe.fluids();
        for (int index = 0; index < fluids.size() && index < FLUID_TANK_X.length; index++) {
            FluidStack[] stacks = fluids.get(index).ingredient().getStacks();
            if (stacks.length == 0) {
                continue;
            }
            FluidStack shown = stacks[0].copyWithAmount(fluids.get(index).amount());
            builder.addInputSlot(FLUID_TANK_X[index], JeiLayout.FLUID_TANK_Y)
                    .setFluidRenderer(1, false, 16, 16)
                    .addFluidStack(shown.getFluid(), shown.getAmount());
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, AssemblingRecipe recipe, IFocusGroup focuses) {
        List<FormattedText> lines = MachineRecipeText.baseLines(
                recipe.duration(), recipe.energyPerTick());

        builder.addText(lines,
                        JeiLayout.GRID_WIDTH - 12, JeiLayout.GRID_HEIGHT - JeiLayout.GRID_TEXT_Y - 4)
                .setPosition(6, JeiLayout.GRID_TEXT_Y)
                .setTextAlignment(HorizontalAlignment.CENTER);
    }
}
