package com.starcrosser.inspiration.machine.client;

import com.hrznstudio.titanium.block.tile.ActiveTile;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 侧向配置按钮文字标签的挂载工具。
 * <p>
 * 标签的做法与三个位置约束写在 {@link FacingLabelAddon}，这里只封装「给一组组件各挂一个」
 * 这件事，让 {@code InspiredMachineTile}（用电机器）与 {@code PowerGeneratorTile}（发电机）
 * 两条继承链共用同一份实现。
 * <p>
 * 调用方必须标 {@link OnlyIn}：addon 属于客户端类，不标的话服务端做类校验时会去加载它。
 */
@OnlyIn(Dist.CLIENT)
public final class FacingLabels {

    /** 标签的语言键前缀，后面直接拼组件名。 */
    private static final String KEY_PREFIX = "gui.inspiration.facing.";

    private FacingLabels() {
    }

    public static void add(ActiveTile<?> tile, IFacingComponent... components) {
        for (IFacingComponent component : components) {
            String key = KEY_PREFIX + component.getName();
            int x = component.getFacingHandlerX();
            int y = component.getFacingHandlerY();
            int color = component.getColor();
            tile.addGuiAddonFactory(() -> new FacingLabelAddon(key, x, y, color));
        }
    }
}
