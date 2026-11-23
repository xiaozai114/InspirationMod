package com.starcrosser.inspiration.multiblock;

import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.multiblock.pattern.MineralPattern;
import com.starcrosser.inspiration.multiblock.pattern.SimulatorPattern;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 多方块的「种类」：把结构尺寸、符号表、虚影与控制器方块收在一处，
 * JEI 结构预览与结构投影仪都只认它，新加一座多方块只要在这里加一个常量。
 */
public enum MultiblockKind {

    /** 结构模拟机：4×3×3，按战利品表产出。 */
    STRUCTURE("structure_simulator", 4, 3, 3,
            () -> ModMultiblocks.STRUCTURE_SIMULATOR.getBlock()) {
        @Override
        public char symbolAt(int layer, int row, int column) {
            return SimulatorPattern.symbolAt(layer, row, column);
        }

        @Override
        public List<Cell> cells(Direction facing) {
            return SimulatorPattern.previewCells(facing).stream()
                    .map(cell -> new Cell(cell.offset(), cell.state()))
                    .toList();
        }
    },

    /** 矿物模拟机：3×5×3，按维度数据的权重产矿。 */
    MINERAL("mineral_simulator", 3, 5, 3,
            () -> ModMultiblocks.MINERAL_SIMULATOR.getBlock()) {
        @Override
        public char symbolAt(int layer, int row, int column) {
            return MineralPattern.symbolAt(layer, row, column);
        }

        @Override
        public List<Cell> cells(Direction facing) {
            return MineralPattern.previewCells(facing).stream()
                    .map(cell -> new Cell(cell.offset(), cell.state()))
                    .toList();
        }
    };

    /** 虚影里的一格（统一形状，避免两个 pattern 各自定义 record 互相打架）。 */
    public record Cell(BlockPos offset, BlockState state) {
    }

    private final String id;
    private final int width;
    private final int height;
    private final int depth;
    /** 用供应商而不是方块本体：枚举常量的初始化可能早于 ModMultiblocks#register。 */
    private final java.util.function.Supplier<Block> controller;

    MultiblockKind(String id, int width, int height, int depth,
                   java.util.function.Supplier<Block> controller) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.controller = controller;
    }

    public abstract char symbolAt(int layer, int row, int column);

    public abstract List<Cell> cells(Direction facing);

    public String id() {
        return id;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int depth() {
        return depth;
    }

    public Block controller() {
        return controller.get();
    }

    public ItemStack controllerStack() {
        return new ItemStack(controller.get());
    }

    /** 外壳方块（所有多方块共用高能机器外壳）。 */
    public ItemStack casingStack() {
        return new ItemStack(ModBlocks.HIGH_ENERGY_MACHINE_CASING.get());
    }

    /** 按控制器方块找种类；不是控制器就返回 null（投影仪靠它判断该不该投影）。 */
    @Nullable
    public static MultiblockKind byController(Block block) {
        for (MultiblockKind kind : values()) {
            if (kind.controller() == block) {
                return kind;
            }
        }
        return null;
    }
}
