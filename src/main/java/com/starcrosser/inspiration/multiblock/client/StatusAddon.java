package com.starcrosser.inspiration.multiblock.client;

import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.starcrosser.inspiration.multiblock.blockentity.StructureSimulatorTile;
import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 结构模拟机界面上那一行状态文字。
 * <p>
 * 主方块没有自己的槽位，界面上只有能量条与进度条，玩家看不出机器为什么不动；
 * 这里按 {@link StructureSimulatorTile#getStatus()} 画一行提示（结构未成型 / 缺少数据 / 缺电……）。
 * 状态值带 {@code @Save}，服务端改动后由 Titanium 的更新包推给客户端，addon 直接读即可。
 */
@OnlyIn(Dist.CLIENT)
public class StatusAddon extends BasicScreenAddon {

    private static final int COLOR = 0xFF555555;

    private final StructureSimulatorTile tile;

    public StatusAddon(StructureSimulatorTile tile) {
        // 界面 176×184，玩家背包从 y=102 开始，状态行压在它上面
        super(8, 90);
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
        Component text = Component.translatable(tile.getStatus().translationKey());
        // 搭错了就把出错的那一格（相对控制器的偏移）一并标出来，省得玩家整座结构瞎找
        BlockPos errorPos = tile.getSyncedErrorPos();
        if (errorPos != null) {
            BlockPos rel = errorPos.subtract(tile.getBlockPos());
            text = text.copy().append(Component.literal(
                    String.format(" (%+d,%+d,%+d)", rel.getX(), rel.getY(), rel.getZ())));
        }
        graphics.drawString(font, text, guiX + getPosX(), guiY + getPosY(), COLOR, false);
    }

    @Override
    public void drawForegroundLayer(GuiGraphics graphics, Screen screen, IAssetProvider provider,
                                    int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        // 只在背景层画一次
    }
}
