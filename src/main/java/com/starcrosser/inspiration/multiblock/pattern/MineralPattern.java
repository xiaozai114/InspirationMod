package com.starcrosser.inspiration.multiblock.pattern;

import com.lowdragmc.lowdraglib2.utils.data.BlockInfo;
import com.lowdragmc.mbd2.api.pattern.BlockPattern;
import com.lowdragmc.mbd2.api.pattern.FactoryBlockPattern;
import com.lowdragmc.mbd2.api.pattern.MultiblockState;
import com.lowdragmc.mbd2.api.pattern.Predicates;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 矿物模拟机的 3×5×3 结构定义（宽 3 × 高 5 × 长 3）。
 * <p>
 * 与结构模拟机同一套路：只用 MBD2 的结构层，成型校验交给
 * {@code checkPatternAtWithoutController}，控制器与仓室仍是本模组的方块实体。
 *
 * <pre>
 * SHAPE[层][行]（行自下而上，层自后往前）：
 *   后  CCC / CCC / CCC / CCC / CCC
 *   中  CCC / C C / C C / C C / CCC   ← 中间一列是空腔
 *   前  CCC / CCC / CMC / CCC / CCC   ← 正面中层中间那格是主方块
 * </pre>
 */
public final class MineralPattern {

    private static final String[][] SHAPE = {
            {"CCC", "CCC", "CCC", "CCC", "CCC"}, // 后
            {"CCC", "C C", "C C", "C C", "CCC"}, // 中：内部一列空腔
            {"CCC", "CCC", "CMC", "CCC", "CCC"}, // 前：中层中间是主方块
    };

    private static final int WIDTH = 3;
    private static final int HEIGHT = 5;
    private static final int DEPTH = 3;

    /** 主方块在图案里的位置（列, 行, 层）。 */
    private static final BlockPos CONTROLLER = new BlockPos(1, 2, 2);

    private static BlockPattern pattern;

    private MineralPattern() {
    }

    private static Set<Block> hatches() {
        return Set.of(
                ModMultiblocks.INPUT_HATCH.getBlock(),
                ModMultiblocks.OUTPUT_HATCH.getBlock(),
                ModMultiblocks.ENERGY_PORT.getBlock(),
                ModMultiblocks.INSPIRATION_HATCH.getBlock(),
                ModMultiblocks.DATA_HATCH.getBlock());
    }

    public static BlockPattern get() {
        if (pattern == null) {
            pattern = build();
        }
        return pattern;
    }

    private static BlockPattern build() {
        FactoryBlockPattern factory = FactoryBlockPattern.start();
        for (int layer = 0; layer < DEPTH; layer++) {
            factory.aisle(SHAPE[layer]);
        }
        return factory
                .where('C', Predicates.custom(MineralPattern::isShell, MineralPattern::shellPreview))
                // 必须用 controller 谓词，MBD2 才会算出 centerOffset（见结构模拟机同类注释）
                .where('M', Predicates.controller(
                        Predicates.blocks(ModMultiblocks.MINERAL_SIMULATOR.getBlock())))
                .where(' ', Predicates.any())
                .build();
    }

    private static boolean isShell(MultiblockState state) {
        Block block = state.getBlockState().getBlock();
        return block == ModBlocks.HIGH_ENERGY_MACHINE_CASING.get() || hatches().contains(block);
    }

    private static BlockInfo[] shellPreview() {
        List<Block> blocks = new ArrayList<>();
        blocks.add(ModBlocks.HIGH_ENERGY_MACHINE_CASING.get());
        blocks.addAll(hatches());
        return blocks.stream().map(BlockInfo::fromBlock).toArray(BlockInfo[]::new);
    }

    public static BlockPos controllerOffset() {
        return CONTROLLER;
    }

    public static int width() {
        return WIDTH;
    }

    public static int height() {
        return HEIGHT;
    }

    public static int depth() {
        return DEPTH;
    }
}
