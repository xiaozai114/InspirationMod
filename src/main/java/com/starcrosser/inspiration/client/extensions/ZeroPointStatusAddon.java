package com.starcrosser.inspiration.client.extensions;

import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.starcrosser.inspiration.machine.blockentity.ZeroPointGeneratorTile;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 零点能发电机界面上的数值行：缓冲电量（GFE）+ 增幅器数量。
 * <p>
 * 界面那根「电量条」其实是百分比，看不出真实数值；这里直接写「12.3 / 128.0 GFE ・ 增幅器 3/64」。
 * {@code stored} 与槽位都带 {@code @Save}，Titanium 会把它们同步到客户端。
 */
@OnlyIn(Dist.CLIENT)
public class ZeroPointStatusAddon extends BasicScreenAddon {

    private static final int COLOR = 0xFF3A3A3A;

    private final ZeroPointGeneratorTile tile;

    public ZeroPointStatusAddon(ZeroPointGeneratorTile tile) {
        super(8, 78);
        this.tile = tile;
    }

    @Override
    public int getXSize() {
        return 160;
    }

    @Override
    public int getYSize() {
        return 9;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics graphics, Screen screen, IAssetProvider provider,
                                    int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        Font font = screen.getMinecraft().font;
        Component text = Component.translatable("gui.inspiration.zero_point_generator.status",
                formatGfe(tile.stored()), formatGfe(ZeroPointGeneratorTile.CAPACITY),
                tile.amplifiers(), ZeroPointGeneratorTile.MAX_AMPLIFIERS);
        graphics.drawString(font, text, guiX + getPosX(), guiY + getPosY(), COLOR, false);
    }

    /** 1.2G FE 这种量级用 GFE 表示，保留一位小数。 */
    private static String formatGfe(long fe) {
        return String.format("%.1f", fe / 1_000_000_000.0);
    }

    @Override
    public void drawForegroundLayer(GuiGraphics graphics, Screen screen, IAssetProvider provider,
                                    int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
    }
}
