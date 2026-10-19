package com.starcrosser.inspiration.item.tool;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;

/** 经验掉落：灵感系列工具与翡翠钢升级版共用同一套产出方式。 */
public final class ExperienceDrops {

    private ExperienceDrops() {
    }

    /**
     * 在指定坐标生成一簇经验球。
     *
     * @param amount 经验点数，小于等于 0 时不生成
     */
    public static void spawnExperience(Level level, double x, double y, double z, int amount) {
        if (level.isClientSide || amount <= 0) {
            return;
        }
        level.addFreshEntity(new ExperienceOrb(level, x, y, z, amount));
    }
}
