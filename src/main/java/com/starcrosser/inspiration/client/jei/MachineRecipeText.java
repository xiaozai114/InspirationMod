package com.starcrosser.inspiration.client.jei;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

import java.util.List;

/**
 * 单方块机器 JEI 分类共用的说明文字。
 * <p>
 * 只给「基准耗时」与「基准每刻耗电」两行：插件倍率、总量推导、灵感充能消耗这些
 * 在机器界面里看得到，面板再堆一轮只会把正文挤到看不见。
 * 多方块（注入）的信息更关键，不走这里。
 */
public final class MachineRecipeText {

    private MachineRecipeText() {
    }

    /**
     * @param duration       配方基准耗时（tick）
     * @param energyPerTick  配方基准每刻耗电（FE）
     */
    public static List<FormattedText> baseLines(int duration, int energyPerTick) {
        return List.of(
                Component.translatable("jei.inspiration.machine.duration", duration),
                Component.translatable("jei.inspiration.machine.energy_per_tick", energyPerTick));
    }
}
