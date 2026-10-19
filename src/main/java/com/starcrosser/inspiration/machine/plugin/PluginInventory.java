package com.starcrosser.inspiration.machine.plugin;

import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.component.inventory.SidedInventoryComponent;
import com.hrznstudio.titanium.component.sideness.IFacingComponent;
import com.hrznstudio.titanium.util.FacingUtil;
import com.starcrosser.inspiration.item.custom.MachinePluginItem;
import net.minecraft.world.item.DyeColor;

/**
 * 机器插件槽的创建与配置。
 * <p>
 * Titanium 只在 {@code MachineTile} 里自带四个 augment 槽，其它方块实体（注入枪）若要支持插件，
 * 得用一个一模一样的库存组件才能画出同样的界面。因此创建与配置都收在这里：
 * <ul>
 *     <li>{@link #create(int)} —— 给非机器的方块实体新建一个插件槽组件；</li>
 *     <li>{@link #configure(SidedInventoryComponent)} —— 把 Titanium 自带的 augment 槽改造成插件槽。</li>
 * </ul>
 */
public final class PluginInventory {

    /** 插件槽数量，与 Titanium 机器的 augment 槽一致。 */
    public static final int SLOTS = 4;

    /** 界面右侧那一列的位置，沿用 Titanium 机器的 augment 槽坐标。 */
    public static final int X = 180;
    public static final int Y = 11;

    /** 插件槽的背景底板在界面中的位置，与 {@code MachineTile#getAugmentBackground} 相同。 */
    public static final int BACKGROUND_X = 175;
    public static final int BACKGROUND_Y = 4;

    /** 槽位库存名：测试与调试按名字取用。 */
    public static final String NAME = "augments";

    private PluginInventory() {
    }

    /** 新建一个插件槽组件。第 {@code facingPos} 参数只是界面里的侧栏序号，与其它槽位错开即可。 */
    public static <T extends IComponentHarness> SidedInventoryComponent<T> create(int facingPos) {
        SidedInventoryComponent<T> inventory = new SidedInventoryComponent<>(NAME, X, Y, SLOTS, facingPos);
        configure(inventory);
        return inventory;
    }

    /**
     * 把槽位组件改造成插件槽：只收本模组的插件、每槽一个、不向自动化开放任何侧面。
     * <p>
     * 侧面全部设成 {@code NONE} 是照抄 {@code MachineTile} 的做法 —— 插件只能由玩家在界面里放，
     * 管道与漏斗都够不着，免得机器被别的东西偷偷改装。
     */
    public static void configure(SidedInventoryComponent<?> inventory) {
        inventory.setInputFilter((stack, slot) -> stack.getItem() instanceof MachinePluginItem);
        inventory.setSlotLimit(1);
        inventory.setRange(1, SLOTS);
        inventory.setColor(DyeColor.PURPLE);
        inventory.disableFacingAddon();
        for (FacingUtil.Sideness sideness : FacingUtil.Sideness.values()) {
            inventory.getFacingModes().put(sideness, IFacingComponent.FaceMode.NONE);
        }
    }
}
