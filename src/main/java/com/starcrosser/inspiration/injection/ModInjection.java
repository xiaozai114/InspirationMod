package com.starcrosser.inspiration.injection;

import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.injection.block.InjectionGunBlock;
import com.starcrosser.inspiration.injection.block.MatterBinderBlock;

/**
 * 注入结构的两个方块（物质束缚器与注入枪）的注册中心。
 * <p>
 * 与 {@code ModMachines} 一样借助 Titanium 的 {@link DeferredRegistryHelper}，
 * 一次性完成方块、方块物品、方块实体类型与能量 / 物品能力的注册。
 */
public final class ModInjection {

    public static BlockWithTile MATTER_BINDER;
    public static BlockWithTile INJECTION_GUN;

    public static void register() {
        DeferredRegistryHelper registry = new DeferredRegistryHelper(Inspiration.MODID);
        MATTER_BINDER = registry.registerBlockWithTile("matter_binder", MatterBinderBlock::new, null);
        INJECTION_GUN = registry.registerBlockWithTile("injection_gun", InjectionGunBlock::new, null);
    }

    private ModInjection() {
    }
}
