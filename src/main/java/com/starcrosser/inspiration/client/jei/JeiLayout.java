package com.starcrosser.inspiration.client.jei;

import com.starcrosser.inspiration.Inspiration;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.resources.ResourceLocation;

/**
 * JEI 配方界面共用的布局常量。
 * <p>
 * 两个输入槽 —— 箭头 —— 一个输出槽，底部留出说明文字的位置。
 * 数值与 {@code assets/inspiration/textures/gui/jei/*.png} 中绘制的背景保持一致。
 */
public final class JeiLayout {

    /** 背景贴图目录，具体分类只需在末尾接上 {@code .png} 的文件名。 */
    public static final String BACKGROUND_DIRECTORY = "textures/gui/jei/";

    public static final int WIDTH = 104;
    public static final int HEIGHT = 54;

    public static final int FIRST_INPUT_X = 8;
    public static final int SECOND_INPUT_X = 30;
    public static final int OUTPUT_X = 78;

    /** 槽位与箭头共同的竖直中心线。 */
    public static final int SLOT_Y = 6;

    /** 底部说明文字的起始纵坐标。 */
    public static final int TEXT_Y = 30;

    /**
     * 背景贴图路径。
     * <p>
     * **必须带上 {@code .png} 扩展名。** MC 的纹理管理器是按完整路径去找文件的
     * （{@code assets/<namespace>/textures/gui/jei/<name>.png}），不会自动补扩展名；
     * 少了它就加载不到，界面会渲染成缺失贴图的黑紫棋盘格。JEI 自身的分类也是这么写的
     * （见其 {@code DebugRecipeCategory}）。
     */
    public static ResourceLocation background(String name) {
        return ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, BACKGROUND_DIRECTORY + name + ".png");
    }

    /**
     * 按贴图的真实尺寸构建背景。
     * <p>
     * 不能直接用 {@code guiHelper.createDrawable(...)}：它默认把贴图当成 256x256 的图集，
     * UV 会按 256 归一化。我们的背景就是一张独立的小图，那样采样只能取到左上角一部分再拉伸，
     * 面板看上去就成了「箭头颜色大色块的一角」。必须显式声明纹理尺寸。
     */
    public static IDrawableStatic backgroundDrawable(IGuiHelper guiHelper, String name, int width, int height) {
        return guiHelper.drawableBuilder(background(name), 0, 0, width, height)
                .setTextureSize(width, height)
                .build();
    }

    /** 单输入配方（矿物扭曲机）：输入槽的横坐标，输出槽沿用 {@link #OUTPUT_X}。 */
    public static final int SINGLE_INPUT_X = 8;

    /* ---------------- 多输入配方（灵感装配台）：最多 3x2 输入区 ---------------- */

    /** 输入区的三列与两行坐标。前两列两行就是原来的 2x2，第三列留给高能机型的第 5、6 种材料。 */
    public static final int GRID_COLUMN_X = 8;
    public static final int GRID_COLUMN_X2 = 30;
    public static final int GRID_COLUMN_X3 = 52;
    public static final int GRID_ROW_Y = 6;
    public static final int GRID_ROW_Y2 = 24;

    /** 输出槽：箭头右侧，留够第三列与箭杆的位置。 */
    public static final int GRID_OUTPUT_X = 98;
    public static final int GRID_OUTPUT_Y = 24;

    /** 多输入分类的面板高度与说明文字起始行。 */
    public static final int TALL_HEIGHT = 74;
    public static final int TALL_TEXT_Y = 46;

    /** 流体原料罐：摆在输入区下方，一罐一格，最多两罐（高能灵感装配台）。 */
    public static final int FLUID_TANK_X = 8;
    public static final int FLUID_TANK_X2 = 30;
    public static final int FLUID_TANK_Y = 44;

    /**
     * 灵感装配台专用的面板尺寸：比 {@link #TALL_HEIGHT} 更高更宽 ——
     * 第三列材料槽把面板撑到 {@code GRID_WIDTH}，底下还要留出两格流体罐与两行说明文字。
     */
    public static final int GRID_WIDTH = 122;
    public static final int GRID_HEIGHT = 96;
    public static final int GRID_TEXT_Y = 68;

    /** 三材料配方（强能物质融合机）的第三槽：放在两个输入槽正下方居中。 */
    public static final int THIRD_INPUT_X = 19;
    public static final int THIRD_INPUT_Y = 24;

    /* ---------------- 原料精炼：单输入 -> 三个输出槽竖排 ---------------- */

    /** 面板尺寸与说明文字起始行；比 {@link #TALL_HEIGHT} 再高一点才放得下三格产物。 */
    public static final int REFINING_HEIGHT = 86;
    public static final int REFINING_TEXT_Y = 60;

    /** 三个输出槽共用一列：第 0 格（主产物）与输入槽同高，副产物顺次往下排。 */
    public static final int REFINING_OUTPUT_X = 78;
    public static final int REFINING_OUTPUT_Y = 6;
    public static final int REFINING_OUTPUT_PITCH = 18;

    /* ---------------- 注入配方：两行输入各带来源图标与角色标签，右侧箭头指产物 ---------------- */

    /**
     * 注入分类单独用一块又宽又高的面板（150×112）：两个输入各自要放下
     * 「来源图标 + 槽位 + 角色标签」，分隔线下方再放说明文字 ——
     * 固定三行：等级、最小能量、最大能量（种子随机的配方两个值都写成 ???）。
     * 高度按「每行都不折行」留足余量——一旦超出 maxHeight，
     * addText 会直接截断成省略号，玩家就什么都看不到了。
     */
    /* ---------------- 虚空注入 / 虚空凝聚 ---------------- */

    public static final int VOID_WIDTH = 104;
    public static final int VOID_INJECTION_HEIGHT = 92;
    public static final int VOID_CONDENSATION_HEIGHT = 78;
    /** 虚空注入：底材在左上，注入物在左下，产物在箭头另一侧。 */
    public static final int VOID_BASE_X = 20;
    public static final int VOID_BASE_Y = 18;
    public static final int VOID_INJECT_X = 20;
    public static final int VOID_INJECT_Y = 40;
    public static final int VOID_RESULT_X = 78;
    public static final int VOID_RESULT_Y = 18;
    public static final int VOID_INJECTION_TEXT_Y = 56;
    /** 虚空凝聚：面板只有一个产物槽，其余是说明。 */
    public static final int CONDENSE_RESULT_X = 62;
    public static final int CONDENSE_RESULT_Y = 16;
    public static final int CONDENSE_TEXT_Y = 52;

    public static final int INJECT_WIDTH = 150;
    public static final int INJECT_HEIGHT = 112;

    /** 来源图标（注入枪 / 物质束缚器）的横坐标与两行各自的纵坐标。 */
    public static final int INJECT_ICON_X = 6;
    public static final int INJECT_ICON_Y = 7;
    public static final int INJECT_BASE_ICON_Y = 27;

    /** 两个输入槽的横坐标（上为注入物质，下为基底物质）。 */
    public static final int INJECT_INPUT_X = 26;
    public static final int INJECT_INPUT_Y = 6;
    public static final int INJECT_BASE_Y = 26;

    /**
     * 槽位右侧的角色标签：「注入物质」/「基底物质」。
     * 高度给到两行——英文的 "Injected material" 在这个宽度下必然折行，只给一行会被截断。
     */
    public static final int INJECT_LABEL_X = 48;
    public static final int INJECT_LABEL_Y = 8;
    public static final int INJECT_BASE_LABEL_Y = 28;
    public static final int INJECT_LABEL_WIDTH = 44;
    public static final int INJECT_LABEL_HEIGHT = 22;

    public static final int INJECT_OUTPUT_X = 120;
    public static final int INJECT_OUTPUT_Y = 24;

    /** 说明文字区：分隔线下方，等级、最小/最大能量、后果与触发方式。 */
    public static final int INJECT_TEXT_Y = 56;

    /* ---------------- 结构模拟机的结构预览：逐层的 4×3 方块网格，没有箭头 ---------------- */

    /** 面板尺寸，与 {@code structure_preview.png} 一致。 */
    public static final int STRUCTURE_WIDTH = 104;
    public static final int STRUCTURE_HEIGHT = 86;

    /** 网格格子的边长与间距；4 列 × 3 行正好填满分隔线以上的区域。 */
    public static final int STRUCTURE_CELL = 16;
    public static final int STRUCTURE_PITCH = 18;
    public static final int STRUCTURE_GRID_X = 17;
    public static final int STRUCTURE_GRID_Y = 4;

    /** 分隔线下方：左边一个槽位显示控制器，右边一行「第几层」。 */
    public static final int STRUCTURE_SLOT_X = 6;
    public static final int STRUCTURE_SLOT_Y = 66;
    public static final int STRUCTURE_TEXT_X = 28;
    public static final int STRUCTURE_TEXT_Y = 70;

    private JeiLayout() {
    }
}
