package com.starcrosser.inspiration.client.jei;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import com.starcrosser.inspiration.multiblock.MultiblockKind;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** A working arrangement of interchangeable hatches on the matched MBD2 shell. */
public record StructurePreview(MultiblockKind kind, Map<BlockPos, BlockState> blocks, List<ItemStack> materials) {
    public static StructurePreview of(MultiblockKind kind) {
        var blocks = new LinkedHashMap<BlockPos, BlockState>();
        kind.cells(Direction.NORTH).forEach(cell -> blocks.put(cell.offset(), cell.state()));
        var front = blocks.keySet().stream().filter(p -> p.getZ() == 0 && !p.equals(BlockPos.ZERO))
                .sorted(Comparator.<BlockPos>comparingInt(BlockPos::getY).thenComparingInt(BlockPos::getX)).toList();
        var hatches = new ArrayList<Block>();
        hatches.add(ModMultiblocks.DATA_HATCH.getBlock());
        hatches.add(ModMultiblocks.ENERGY_PORT.getBlock());
        if (kind == MultiblockKind.STRUCTURE) {
            hatches.add(ModMultiblocks.INSPIRATION_HATCH.getBlock());
        }
        hatches.add(ModMultiblocks.OUTPUT_HATCH.getBlock());
        for (int i = 0; i < hatches.size(); i++) {
            blocks.put(front.get(i), hatches.get(i).defaultBlockState());
        }
        var materials = new ArrayList<ItemStack>();
        materials.add(kind.controllerStack());
        materials.add(kind.casingStack().copyWithCount(blocks.size() - 1 - hatches.size()));
        hatches.forEach(hatch -> materials.add(new ItemStack(hatch)));
        return new StructurePreview(kind, Map.copyOf(blocks), List.copyOf(materials));
    }
}
