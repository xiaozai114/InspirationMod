package com.starcrosser.inspiration.machine.plugin;

import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 一台设备上已安装插件的倍率结算器。
 * <p>
 * 持有一个插件槽库存，每刻调用 {@link #refresh()}：槽位内容没变就什么都不做，
 * 变了才重新折算 {@link PluginEffects} 并回调给宿主去落到各个部件上。
 * 这样 {@code InspiredMachineTile} 与注入枪可以共用同一套「指纹 + 结算」逻辑。
 */
public final class PluginHost {

    private final SidedInventoryComponent<?> inventory;
    private final Consumer<PluginEffects> applier;

    /** 插件槽内容的指纹，只有变化时才重算倍率。 */
    private String stamp = "";
    private PluginEffects effects = PluginEffects.NONE;

    /**
     * @param inventory 插件槽库存
     * @param applier   倍率变化后的应用回调，宿主在里面改写容量 / 进度条等部件
     */
    public PluginHost(SidedInventoryComponent<?> inventory, Consumer<PluginEffects> applier) {
        this.inventory = inventory;
        this.applier = applier;
    }

    /** 槽位内容变化时重算倍率并回调宿主。 */
    public void refresh() {
        List<ItemStack> installed = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            installed.add(stack);
            builder.append(stack.isEmpty() ? '-' : BuiltInRegistries.ITEM.getKey(stack.getItem()))
                    .append(':').append(stack.getCount()).append(';');
        }
        String current = builder.toString();
        if (current.equals(stamp)) {
            return;
        }
        stamp = current;
        effects = PluginEffects.from(installed);
        applier.accept(effects);
    }

    /** 当前插件倍率，供界面与调试读取。 */
    public PluginEffects effects() {
        return effects;
    }
}
