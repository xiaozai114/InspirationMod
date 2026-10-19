package com.starcrosser.inspiration.jei.category;

import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.jei.JeiLayout;
import com.starcrosser.inspiration.jei.ModJeiRecipeTypes;
import com.starcrosser.inspiration.injection.recipe.InjectionRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 注入配方的 JEI 分类。
 * <p>
 * 面板比其它分类宽，因为两个输入各自要摆出三样东西：左边一个「该材料放哪台设备」的小图标，
 * 中间是槽位，右边是角色标签（注入物质 / 基底物质）。
 * 下半部固定三行说明：等级、最小能量、最大能量 —— 能量窗口是这个机制的核心信息，
 * 玩家必须把注入枪充到窗口之内才不会白费材料或者把自己炸了。
 * 种子随机的配方没有可读的窗口，两个能量值都写成 {@code ???}，不再额外加说明行
 * （想知道具体数值得用世界数据探测器）。
 */
public class InjectionRecipeCategory implements IRecipeCategory<InjectionRecipe> {

    /** 种子随机配方的能量占位显示。 */
    private static final String UNKNOWN = "???";

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable gunIcon;
    private final IDrawable binderIcon;

    public InjectionRecipeCategory(IGuiHelper guiHelper) {
        this.background = JeiLayout.backgroundDrawable(guiHelper, "injection",
                JeiLayout.INJECT_WIDTH, JeiLayout.INJECT_HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModInjection.INJECTION_GUN.getBlock()));
        // 两个输入槽左侧的来源标记：注入物质放注入枪，基底物质放物质束缚器
        this.gunIcon = icon;
        this.binderIcon = guiHelper.createDrawableItemStack(new ItemStack(ModInjection.MATTER_BINDER.getBlock()));
    }

    @Override
    public RecipeType<InjectionRecipe> getRecipeType() {
        return ModJeiRecipeTypes.INJECTION;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.inspiration.injection");
    }

    @Override
    public int getWidth() {
        return JeiLayout.INJECT_WIDTH;
    }

    @Override
    public int getHeight() {
        return JeiLayout.INJECT_HEIGHT;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    /**
     * 背景必须走这里，不能用 {@code createRecipeExtras} 里的 {@code addDrawable}。
     * <p>
     * JEI 在槽位、说明文字、图标**都画完之后**才绘制 extras 中的 drawable，
     * 不透明背景会把这些内容整块盖掉；而 {@code getBackground()} 在槽位之前绘制，
     * 才是背景该待的位置。
     */
    @SuppressWarnings("removal")
    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InjectionRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(JeiLayout.INJECT_INPUT_X, JeiLayout.INJECT_INPUT_Y)
                .setStandardSlotBackground()
                .addIngredients(recipe.inject());
        builder.addInputSlot(JeiLayout.INJECT_INPUT_X, JeiLayout.INJECT_BASE_Y)
                .setStandardSlotBackground()
                .addIngredients(recipe.base());
        builder.addOutputSlot(JeiLayout.INJECT_OUTPUT_X, JeiLayout.INJECT_OUTPUT_Y)
                .setOutputSlotBackground()
                .addItemStack(recipe.result());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, InjectionRecipe recipe, IFocusGroup focuses) {
        builder.addDrawable(gunIcon, JeiLayout.INJECT_ICON_X, JeiLayout.INJECT_ICON_Y);
        builder.addDrawable(binderIcon, JeiLayout.INJECT_ICON_X, JeiLayout.INJECT_BASE_ICON_Y);

        label(builder, "jei.inspiration.injection.role_inject",
                JeiLayout.INJECT_LABEL_Y, ChatFormatting.LIGHT_PURPLE);
        label(builder, "jei.inspiration.injection.role_base",
                JeiLayout.INJECT_BASE_LABEL_Y, ChatFormatting.BLUE);

        // 每一行都控制在面板宽度内不换行：一旦换行，行数翻倍就会被 maxHeight 截断成省略号。
        // 种子随机的配方不额外解释，两个能量值都写成 ??? 就够传达「这里读不出来」。
        List<FormattedText> lines = List.of(
                Component.translatable("jei.inspiration.injection.tier", recipe.tier()),
                Component.translatable("jei.inspiration.injection.min_energy", energy(recipe, recipe.minEnergy()))
                        .withStyle(ChatFormatting.GOLD),
                Component.translatable("jei.inspiration.injection.max_energy", energy(recipe, recipe.maxEnergy()))
                        .withStyle(ChatFormatting.RED));

        builder.addText(lines,
                        JeiLayout.INJECT_WIDTH - 12, JeiLayout.INJECT_HEIGHT - JeiLayout.INJECT_TEXT_Y - 4)
                .setPosition(6, JeiLayout.INJECT_TEXT_Y)
                .setTextAlignment(HorizontalAlignment.CENTER);
    }

    private static void label(IRecipeExtrasBuilder builder, String translationKey, int y, ChatFormatting color) {
        builder.addText(List.of(Component.translatable(translationKey).withStyle(color)),
                        JeiLayout.INJECT_LABEL_WIDTH, JeiLayout.INJECT_LABEL_HEIGHT)
                .setPosition(JeiLayout.INJECT_LABEL_X, y);
    }

    /** 能量按千分位写，25,000 比 25000 好读得多。 */
    private static String formatEnergy(int energy) {
        return String.format("%,d", energy);
    }

    /** 种子随机配方的窗口在 JEI 里一律显示成 {@code ???}。 */
    private static String energy(InjectionRecipe recipe, int value) {
        return recipe.seeded() ? UNKNOWN : formatEnergy(value);
    }
}
