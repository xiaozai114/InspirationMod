package com.starcrosser.inspiration.client.extensions;

import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 侧向配置按钮上方的文字标签。
 * <p>
 * Titanium 的 {@code FacingHandlerScreenAddon} 只画一个 14×14 的小按钮，靠颜色区分属于哪个槽，
 * 单看界面很难分辨哪个是输入、哪个是输出。这里在按钮正上方补一行短标签。
 * <p>
 * 位置约束（界面固定 176×184）：按钮画在最底一行 y=84，玩家背包从 y=102 开始，
 * 按钮下方只有 4 像素，放不下字；所以标签统一画在按钮**上方**，共 10 像素
 * （字高 9 + 间隙 1）。横向对准按钮中心，一行里相邻按钮间隔 18 像素，
 * 因此标签要控制在两个汉字（16 像素）以内。
 * <p>
 * 颜色取组件自己的 {@code getColor()}，也就是和槽位高亮、按钮底色同一套配色，
 * 看一眼就能把按钮和它管的槽对上。原色偏亮，统一压暗成深色避免在浅灰面板上看不清。
 */
@OnlyIn(Dist.CLIENT)
public class FacingLabelAddon extends BasicScreenAddon {

    /** Titanium 的朝向按钮贴图尺寸，用来算按钮中心。 */
    public static final int BUTTON_SIZE = 14;

    /** 字体行高。 */
    private static final int LINE_HEIGHT = 9;

    /** 标签与按钮之间留 1 像素，别贴太紧。 */
    private static final int GAP = 1;

    /** 面板底色是浅灰，原色直接拿来画会太淡，按这个比例压暗。 */
    private static final float DARKEN = 0.62F;

    private final Component label;
    private final int color;

    /**
     * @param translationKey 标签的语言键
     * @param buttonX 按钮左上角 x（组件自己的 facing handler x）
     * @param buttonY 按钮左上角 y（通常是 84）
     * @param rgb 组件颜色，用来画字
     */
    public FacingLabelAddon(String translationKey, int buttonX, int buttonY, int rgb) {
        super(buttonX + BUTTON_SIZE / 2, buttonY - GAP - LINE_HEIGHT);
        this.label = Component.translatable(translationKey);
        this.color = 0xFF000000 | darken(rgb);
    }

    private static int darken(int rgb) {
        int r = (int) (((rgb >> 16) & 0xFF) * DARKEN);
        int g = (int) (((rgb >> 8) & 0xFF) * DARKEN);
        int b = (int) ((rgb & 0xFF) * DARKEN);
        return (r << 16) | (g << 8) | b;
    }

    @Override
    public int getXSize() {
        // 只是为了给 isMouseOver 一个大致范围，不用于布局
        return 18;
    }

    @Override
    public int getYSize() {
        return LINE_HEIGHT;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics graphics, Screen screen, IAssetProvider provider,
                                    int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        Font font = screen.getMinecraft().font;
        // getPosX() 已经是按钮中心，直接居中画
        graphics.drawCenteredString(font, this.label, guiX + getPosX(), guiY + getPosY(), this.color);
    }

    @Override
    public void drawForegroundLayer(GuiGraphics graphics, Screen screen, IAssetProvider provider,
                                    int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        // 文字只在背景层画一次：这一层不用可能被调用到的前景再画第二遍
    }
}
