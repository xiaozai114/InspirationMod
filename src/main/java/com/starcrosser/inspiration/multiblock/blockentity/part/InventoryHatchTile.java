package com.starcrosser.inspiration.multiblock.blockentity.part;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

/**
 * 带一个物品栏的仓室的公共基类。
 * <p>
 * 输入仓 / 输出仓 / 数据仓 / 灵感提供仓原先各写了一遍同样的四件套：
 * {@code @Save} 字段、建 {@link InventoryComponent} 并设排布与过滤、
 * {@code addInventory}、再加 {@code getSelf} 与 {@code getInventory} 两个取值方法。
 * 它们的差别只有「名字、位置、格数、排布、能放什么」，这里把这些收成构造参数。
 * <p>
 * ⚠️ 能量端口不在本链上：它要的是 Titanium 的能量缓冲，只能继承 {@code PoweredTile}。
 * <p>
 * ⚠️ {@code @Save} 字段放在基类是安全的 —— Titanium 的 NBTManager 会沿
 * {@code getSuperclass()} 一路往上扫父类字段，不会漏掉继承来的字段。
 *
 * @param <T> 具体仓室类型（自限定泛型，Titanium 要求）
 */
public abstract class InventoryHatchTile<T extends InventoryHatchTile<T>> extends HatchTile<T> {

    @Save
    private final InventoryComponent<T> inventory;

    /**
     * @param name     物品栏名字，同时决定它在存档里的键
     * @param x, y     物品栏在 GUI 中的左上角
     * @param slots    格数
     * @param columns  排布列数（与 rows 一起决定 GUI 里画成几行几列）
     * @param rows     排布行数
     * @param filter   入槽过滤；{@code null} 表示什么都能放
     */
    protected InventoryHatchTile(BasicTileBlock<T> block, BlockEntityType<?> type,
                                 BlockPos pos, BlockState state,
                                 String name, int x, int y, int slots,
                                 int columns, int rows,
                                 @Nullable BiPredicate<net.minecraft.world.item.ItemStack, Integer> filter) {
        super(block, type, pos, state);
        InventoryComponent<T> inv = new InventoryComponent<>(name, x, y, slots);
        inv.setRange(columns, rows);
        if (filter != null) {
            inv.setInputFilter(filter);
        }
        this.inventory = inv;
        addInventory(this.inventory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getSelf() {
        return (T) this;
    }

    public InventoryComponent<T> getInventory() {
        return inventory;
    }
}
