package com.starcrosser.inspiration.guide;

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 指导书的界面类型。
 * <p>
 * 书不装任何槽位，走菜单而不是自定义网络包，图的是「打开 / 关闭 / ESC / 窗口缩放」
 * 这一套原版容器已经处理好了 —— 代价只是两侧各注册一次菜单与界面。
 */
public class ModGuideMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Inspiration.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<GuideBookMenu>> GUIDE_BOOK =
            MENUS.register("guide_book",
                    () -> new MenuType<>(GuideBookMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
