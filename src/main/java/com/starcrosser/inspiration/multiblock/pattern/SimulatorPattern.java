package com.starcrosser.inspiration.multiblock.pattern;

import com.lowdragmc.lowdraglib2.utils.data.BlockInfo;
import com.lowdragmc.mbd2.api.pattern.BlockPattern;
import com.lowdragmc.mbd2.api.pattern.FactoryBlockPattern;
import com.lowdragmc.mbd2.api.pattern.MultiblockState;
import com.lowdragmc.mbd2.api.pattern.Predicates;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 结构模拟机的 4×3×3 结构定义。
 * <p>
 * 只用 Multiblocked2（MBD2）的**结构层**：{@link FactoryBlockPattern} 描述形状、
 * {@link BlockPattern#checkPatternAtWithoutController} 做成型校验，控制器与仓室仍然是本模组自己的
 * 方块实体，界面、能量、灵感充能全部走 Titanium —— MBD2 在这里只当一个「结构匹配器」。
 * <p>
 * 形状只有 {@link #SHAPE} 这一份数据源：MBD2 的 aisle 与客户端虚影都从它生成，
 * 改形状只需要动这一个常量，两边不会走偏。
 *
 * <pre>
 * SHAPE[层][行]（行自下而上，层自后往前）：
 *   后  CCCC / CCCC / CCCC
 *   中  CCCC / C  C / CCCC   ← 中间两格是空腔
 *   前  CCCC / CMCC / CCCC   ← 正面中层左二那格是主方块
 * </pre>
 *
 * 也就是 4 宽 × 3 高 × 3 长 = 36 格，外壳/部件 34 格、内部空腔 2 格。
 * {@code C} 既可以是高能机器外壳，也可以是五种仓室中的任意一种（仓室替换外壳就是这么实现的），
 * 所以用 {@link Predicates#custom} 一个谓词吃掉所有情况，而不是拼一串 {@code blocks(...)}。
 */
public final class SimulatorPattern {

    /**
     * {@code SHAPE[层][行]}，行自下而上，层自后往前（层下标越大越靠近结构的正面）。
     * {@code C} 外壳 / 仓室，{@code M} 主方块，空格是空腔。
     */
    private static final String[][] SHAPE = {
            {"CCCC", "CCCC", "CCCC"}, // 后
            {"CCCC", "C  C", "CCCC"}, // 中：内部空腔
            {"CCCC", "CMCC", "CCCC"}, // 前：中层左二换成主方块
    };

    /** 宽 / 高 / 长（层）。 */
    private static final int WIDTH = 4;
    private static final int HEIGHT = 3;
    private static final int DEPTH = 3;

    /** 主方块在图案坐标里的位置（列, 行, 层），必须与 {@link #SHAPE} 里的 'M' 对齐。 */
    private static final BlockPos CONTROLLER = new BlockPos(1, 1, 2);

    private static BlockPattern pattern;

    private SimulatorPattern() {
    }

    /** 结构里的部件方块：五种仓室，都可以顶替外壳的位置。 */
    private static Set<Block> hatches() {
        return Set.of(
                ModMultiblocks.INPUT_HATCH.getBlock(),
                ModMultiblocks.OUTPUT_HATCH.getBlock(),
                ModMultiblocks.ENERGY_PORT.getBlock(),
                ModMultiblocks.INSPIRATION_HATCH.getBlock(),
                ModMultiblocks.DATA_HATCH.getBlock());
    }

    /** 懒加载：方块要等注册完成才能取，所以不能在静态初始化里建 pattern。 */
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
                .where('C', Predicates.custom(SimulatorPattern::isShell, SimulatorPattern::shellPreview))
                .where('M', Predicates.controller(
                        Predicates.blocks(ModMultiblocks.STRUCTURE_SIMULATOR.getBlock())))
                // 内部两格用 any：留空、塞满外壳、放别的杂物都算成型。
                // 用 air() 的话玩家得精确数着这两格别填，实际搭的时候几乎必踩 —— 而填上与否
                // 对结构判定没有任何意义，没必要为难人。
                .where(' ', Predicates.any())
                .build();
    }

    /** 外壳或任意一种仓室都算数。 */
    private static boolean isShell(MultiblockState state) {
        Block block = state.getBlockState().getBlock();
        return block == ModBlocks.HIGH_ENERGY_MACHINE_CASING.get() || hatches().contains(block);
    }

    /** 预览用的候选方块；MBD2 在编辑器与结构预览里会拿它当示例。 */
    private static BlockInfo[] shellPreview() {
        List<Block> blocks = new ArrayList<>();
        blocks.add(ModBlocks.HIGH_ENERGY_MACHINE_CASING.get());
        blocks.addAll(hatches());
        return blocks.stream().map(BlockInfo::fromBlock).toArray(BlockInfo[]::new);
    }

    /* ============================ 形状查询（JEI 结构预览用） ============================ */

    public static int width() {
        return WIDTH;
    }

    public static int height() {
        return HEIGHT;
    }

    public static int depth() {
        return DEPTH;
    }

    /**
     * 图案里某一格的符号：{@code C} 外壳/仓室、{@code M} 主方块、空格是内部空腔。
     *
     * @param layer  层下标，0 是最后面那一层
     * @param row    行下标，0 是最下面那一行
     * @param column 列下标
     */
    public static char symbolAt(int layer, int row, int column) {
        return SHAPE[layer][row].charAt(column);
    }

    /* ============================ 虚影预览 ============================ */

    /** 虚影里的一格：相对主方块的**世界坐标**偏移 + 该格该摆什么方块。 */
    public record PreviewCell(BlockPos offset, BlockState state) {
    }

    /**
     * 按给定朝向列出结构里每一格（空腔跳过）相对主方块的位置。
     * <p>
     * 图案坐标 → 世界坐标那一步用的是 MBD2
     * {@code BlockPattern#setActualRelativeOffset} 的同一套公式
     * （{@code structureDir = {LEFT, UP, FRONT}}）：列沿 {@code LEFT}、行沿 {@code UP}、
     * 层沿 {@code FRONT}，四个水平朝向只是把这三个轴重新贴到世界的 X/Y/Z 上。
     */
    public static List<PreviewCell> previewCells(Direction facing) {
        BlockState casing = ModBlocks.HIGH_ENERGY_MACHINE_CASING.get().defaultBlockState();
        BlockState controller = ModMultiblocks.STRUCTURE_SIMULATOR.getBlock().defaultBlockState();
        List<PreviewCell> cells = new ArrayList<>();
        for (int layer = 0; layer < DEPTH; layer++) {
            for (int row = 0; row < HEIGHT; row++) {
                for (int column = 0; column < WIDTH; column++) {
                    char symbol = SHAPE[layer][row].charAt(column);
                    if (symbol == ' ') {
                        continue;
                    }
                    BlockPos patternOffset = new BlockPos(
                            column - CONTROLLER.getX(),
                            row - CONTROLLER.getY(),
                            layer - CONTROLLER.getZ());
                    cells.add(new PreviewCell(rotate(patternOffset, facing),
                            symbol == 'M' ? controller : casing));
                }
            }
        }
        return cells;
    }

    /** 与 MBD2 的 {@code setActualRelativeOffset} 等价：把图案坐标按朝向贴到世界坐标上。 */
    private static BlockPos rotate(BlockPos offset, Direction facing) {
        Direction left = facing.getCounterClockWise();
        int[] out = new int[3];
        putAxis(out, left, offset.getX());
        out[1] = offset.getY();
        putAxis(out, facing, offset.getZ());
        return new BlockPos(out[0], out[1], out[2]);
    }

    private static void putAxis(int[] out, Direction dir, int value) {
        switch (dir) {
            case UP -> out[1] = value;
            case DOWN -> out[1] = -value;
            case WEST -> out[0] = -value;
            case EAST -> out[0] = value;
            case NORTH -> out[2] = -value;
            case SOUTH -> out[2] = value;
        }
    }
}
