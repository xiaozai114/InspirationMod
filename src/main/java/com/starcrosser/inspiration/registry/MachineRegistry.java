package com.starcrosser.inspiration.registry;

import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * 机器方块清单 —— 「同一份清单手写 5 遍」这个问题的根因修复。
 * <p>
 * Titanium 的 {@code registerBlockWithTile(...)} 把方块注册进自己的注册表，
 * **不会**进入 {@code ModBlocks.BLOCKS}，于是 datagen 里遍历不到它们。
 * 结果就是每台机器都要在语言（中 / 英）、方块状态、挖掘标签、战利品表里**各手写一遍**，
 * 加一台机器漏一处 → 物品栏黑紫块或缺失翻译。
 * <p>
 * 现在注册时顺带登记进这份清单，上面那几处全部改成遍历：
 * 加一台新机器只需要在注册处写一次，配套资源自动生成。
 * <p>
 * 条目顺序 = {@code ModMachines} 里的注册顺序，也就是标签 JSON 里 machine 的输出顺序；
 * 语言文件是按键名排序的、战利品表与方块状态各写一个文件，所以这两类不受顺序影响。
 */
public final class MachineRegistry {

    /**
     * 一台机器生成配套资源所需的全部元数据。
     *
     * @param id     方块 ID，与 {@code ModMachines} 注册时用的名字一致
     * @param machine Titanium 的注册结果（方块 + 物品 + 方块实体）
     * @param zhName 中文显示名
     * @param enName 英文显示名
     */
    public record MachineEntry(String id, BlockWithTile machine, String zhName, String enName) {

        /** 机器方块本体；比 {@code machine} 更常用（标签 / 战利品表 / 语言键都认它）。 */
        public Block block() {
            return machine.getBlock();
        }
    }

    private static final List<MachineEntry> ENTRIES = new ArrayList<>();

    private MachineRegistry() {
    }

    /**
     * 注册一台机器并登记进清单。
     * <p>
     * 传进来的 {@code blockFactory} 里可以放心引用 {@code ModMachines} 的静态字段：
     * Titanium 是延迟注册的，真正造方块时那个字段已经赋值了 —— 这也是它能把「自己」传给
     * 方块构造的原因。
     *
     * @return Titanium 的注册结果，交给 {@code ModMachines} 的静态字段持有
     */
    public static BlockWithTile register(DeferredRegistryHelper registry, String id,
                                         String zhName, String enName,
                                         Supplier<BasicTileBlock<?>> blockFactory) {
        BlockWithTile machine = registry.registerBlockWithTile(id, blockFactory, null);
        ENTRIES.add(new MachineEntry(id, machine, zhName, enName));
        return machine;
    }

    /** 全部机器，按注册顺序。 */
    public static List<MachineEntry> machines() {
        return Collections.unmodifiableList(ENTRIES);
    }

    /** 全部机器方块，按注册顺序 —— 给标签、战利品表这类「只认 Block」的场合用。 */
    public static List<Block> blocks() {
        return ENTRIES.stream().map(MachineEntry::block).toList();
    }
}
