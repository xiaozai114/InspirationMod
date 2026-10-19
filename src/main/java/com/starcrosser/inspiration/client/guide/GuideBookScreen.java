package com.starcrosser.inspiration.client.guide;

import com.starcrosser.inspiration.guide.GuideBookMenu;
import com.starcrosser.inspiration.guide.GuideChapter;
import com.starcrosser.inspiration.guide.GuideContent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 指导书的界面：把原版那本成书的书页拿来当纸，左页是正文，底部两枚箭头翻章。
 * <p>
 * 界面尺寸、书页贴图与文字起始位置都照抄原版 {@code BookViewScreen} 的常量
 * （书 192×192、正文从 (36, 30) 起、宽 114），这样两本书放在一起长得一样。
 * 章节内容来自 {@link GuideContent}，这里只负责排字与翻页。
 */
public class GuideBookScreen extends Screen implements MenuAccess<GuideBookMenu> {

    private static final ResourceLocation BOOK_LOCATION =
            ResourceLocation.withDefaultNamespace("textures/gui/book.png");

    private static final int IMAGE_WIDTH = 192;
    private static final int IMAGE_HEIGHT = 192;
    /** 正文相对书左上角的偏移与可用宽度（数值同原版成书界面）。 */
    private static final int TEXT_X = 36;
    private static final int TEXT_Y = 34;
    private static final int TEXT_WIDTH = 114;
    private static final int PAGE_INDICATOR_Y = 148;

    private static final int COLOR_TITLE = 0x5B2C87;
    private static final int COLOR_TEXT = 0x2B2118;
    private static final int COLOR_FADED = 0x8A7C6A;
    private static final int COLOR_HOVER = 0x5B2C87;
    private static final int COLOR_ARROW = 0x4A3B2A;

    private static final int ARROW_WIDTH = 16;
    private static final int ARROW_HEIGHT = 14;
    /** 翻页箭头相对书左上角的位置（右侧箭头镜像是 IMAGE_WIDTH - ARROW_X - 宽）。 */
    private static final int ARROW_X = 24;
    private static final int ARROW_Y = 142;

    private final GuideBookMenu menu;
    private int chapter;

    public GuideBookScreen(GuideBookMenu menu, Inventory playerInventory, Component title) {
        super(title);
        this.menu = menu;
    }

    @Override
    public GuideBookMenu getMenu() {
        return menu;
    }

    private int bookLeft() {
        return (this.width - IMAGE_WIDTH) / 2;
    }

    private int bookTop() {
        return (this.height - IMAGE_HEIGHT) / 2;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        int left = this.bookLeft();
        int top = this.bookTop();
        graphics.blit(BOOK_LOCATION, left, top, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        GuideChapter current = GuideContent.chapter(this.chapter);
        Font font = this.font;

        // 标题压在书页中缝上，两页共用一行
        graphics.drawCenteredString(font, Component.translatable(current.titleKey()),
                left + IMAGE_WIDTH / 2, top + 16, COLOR_TITLE);

        FormattedText body = Component.translatable(current.bodyKey()).withStyle(ChatFormatting.BLACK);
        graphics.drawWordWrap(font, body, left + TEXT_X, top + TEXT_Y, TEXT_WIDTH, COLOR_TEXT);

        // 页码
        Component page = Component.literal((this.chapter + 1) + " / " + GuideContent.size());
        graphics.drawCenteredString(font, page, left + IMAGE_WIDTH / 2,
                top + PAGE_INDICATOR_Y, COLOR_FADED);

        // 翻页箭头：只在真的还有上一章 / 下一章时才画出来
        if (this.chapter > 0) {
            drawArrow(graphics, font, "<", left + ARROW_X, top + ARROW_Y, mouseX, mouseY);
        }
        if (this.chapter < GuideContent.size() - 1) {
            drawArrow(graphics, font, ">", left + IMAGE_WIDTH - ARROW_X - ARROW_WIDTH,
                    top + ARROW_Y, mouseX, mouseY);
        }
    }

    private void drawArrow(GuiGraphics graphics, Font font, String arrow, int x, int y,
                           double mouseX, double mouseY) {
        boolean hovered = hovered(x, y, mouseX, mouseY);
        int color = hovered ? COLOR_HOVER : COLOR_ARROW;
        if (hovered) {
            graphics.fill(x - 2, y - 2, x + ARROW_WIDTH + 2, y + ARROW_HEIGHT + 2, 0x22FFFFFF);
        }
        graphics.drawCenteredString(font, arrow, x + ARROW_WIDTH / 2, y + 3, color);
    }

    /** 鼠标是否落在某块矩形里（{@code Screen} 自己没有矩形命中判定，{@code AbstractContainerScreen} 才有）。 */
    private boolean hovered(int x, int y, double mouseX, double mouseY) {
        return mouseX >= x && mouseX < x + ARROW_WIDTH
                && mouseY >= y && mouseY < y + ARROW_HEIGHT;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int left = this.bookLeft();
        int top = this.bookTop();
        if (this.chapter > 0 && hovered(left + ARROW_X, top + ARROW_Y, mouseX, mouseY)) {
            this.chapter--;
            return true;
        }
        if (this.chapter < GuideContent.size() - 1
                && hovered(left + IMAGE_WIDTH - ARROW_X - ARROW_WIDTH, top + ARROW_Y,
                mouseX, mouseY)) {
            this.chapter++;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
