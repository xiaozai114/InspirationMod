package com.starcrosser.inspiration.client.gui;

import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.network.ExtractInspirationPayload;
import com.starcrosser.inspiration.player.InspirationValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class InspirationGauge extends AbstractWidget {

    private static final int BAR_HEIGHT = 50;
    private static final int BUTTON_HEIGHT = 20;
    private static final int GAP = 2;

    public InspirationGauge(int x, int y) {
        super(x, y, 20, BAR_HEIGHT + GAP + BUTTON_HEIGHT,
                Component.translatable("gui.inspiration.inspiration_value"));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        int value = InspirationValue.synced();

        graphics.fill(x, y, x + width, y + BAR_HEIGHT, 0xFF181820);
        graphics.fill(x + 1, y + 1, x + width - 1, y + BAR_HEIGHT - 1, 0xFF302642);
        int filled = Math.round((BAR_HEIGHT - 2) * Mth.clamp(value / (float) InspirationValue.MAX, 0F, 1F));
        if (filled > 0) {
            graphics.fill(x + 1, y + BAR_HEIGHT - 1 - filled, x + width - 1, y + BAR_HEIGHT - 1, 0xFFB36BE0);
        }

        int buttonY = y + BAR_HEIGHT + GAP;
        boolean hovered = isOverButton(mouseX, mouseY);
        graphics.fill(x, buttonY, x + width, buttonY + BUTTON_HEIGHT, hovered ? 0xFF5B4A73 : 0xFF302642);
        graphics.renderItem(new ItemStack(ModItems.INSPIRATION.get()), x + 2, buttonY + 2);
        if (hovered) {
            graphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(
                    Component.translatable("gui.inspiration.inspiration_value.tip", value, InspirationValue.MAX),
                    Component.translatable("gui.inspiration.inspiration_value.extract")), mouseX, mouseY);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (isOverButton(mouseX, mouseY)) {
            PacketDistributor.sendToServer(new ExtractInspirationPayload());
        }
    }

    private boolean isOverButton(double mouseX, double mouseY) {
        int buttonY = getY() + BAR_HEIGHT + GAP;
        return mouseX >= getX() && mouseX < getX() + width
                && mouseY >= buttonY && mouseY < buttonY + BUTTON_HEIGHT;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
}
