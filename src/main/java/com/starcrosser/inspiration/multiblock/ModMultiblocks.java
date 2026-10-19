package com.starcrosser.inspiration.multiblock;

import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.multiblock.block.HatchBlock;
import com.starcrosser.inspiration.multiblock.block.StructureSimulatorBlock;
import com.starcrosser.inspiration.multiblock.blockentity.part.DataHatchTile;
import com.starcrosser.inspiration.multiblock.blockentity.part.EnergyPortTile;
import com.starcrosser.inspiration.multiblock.blockentity.part.InputHatchTile;
import com.starcrosser.inspiration.multiblock.blockentity.part.InspirationHatchTile;
import com.starcrosser.inspiration.multiblock.blockentity.part.OutputHatchTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 多方块结构的注册中心。
 * <p>
 * 与 {@code ModMachines} 一样走 Titanium 的 {@link DeferredRegistryHelper}，一次注册
 * 方块、方块物品、方块实体与能力；因此**这些方块不在 {@code ModBlocks.BLOCKS} 里**，
 * 战利品表与方块标签要单独补（见 datagen 里那几处 {@code ALKALINE_REACTOR} 的写法）。
 */
public class ModMultiblocks {

    /** 输入仓：模拟配方的物品输入。 */
    public static BlockWithTile INPUT_HATCH;
    /** 输出仓：模拟产出的战利品。 */
    public static BlockWithTile OUTPUT_HATCH;
    /** 能量端口：结构唯一进电的地方。 */
    public static BlockWithTile ENERGY_PORT;
    /** 灵感提供仓：装灵感充能，模拟每刻固定抽 8 mB。 */
    public static BlockWithTile INSPIRATION_HATCH;
    /** 数据仓：放结构数据，不消耗。 */
    public static BlockWithTile DATA_HATCH;
    /** 结构模拟机控制器（主方块）。 */
    public static BlockWithTile STRUCTURE_SIMULATOR;

    /** 所有仓室与控制器共用一套方块属性：比铁块硬一点，需要镐。 */
    private static BlockBehaviour.Properties properties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                .strength(5.0F, 10.0F)
                .requiresCorrectToolForDrops();
    }

    public static void register() {
        DeferredRegistryHelper registry = new DeferredRegistryHelper(Inspiration.MODID);
        INPUT_HATCH = registry.registerBlockWithTile("simulator_input_hatch",
                () -> new HatchBlock<>(properties(), InputHatchTile.class, InputHatchTile::new), null);
        OUTPUT_HATCH = registry.registerBlockWithTile("simulator_output_hatch",
                () -> new HatchBlock<>(properties(), OutputHatchTile.class, OutputHatchTile::new), null);
        ENERGY_PORT = registry.registerBlockWithTile("simulator_energy_port",
                () -> new HatchBlock<>(properties(), EnergyPortTile.class, EnergyPortTile::new), null);
        INSPIRATION_HATCH = registry.registerBlockWithTile("simulator_inspiration_hatch",
                () -> new HatchBlock<>(properties(), InspirationHatchTile.class, InspirationHatchTile::new), null);
        DATA_HATCH = registry.registerBlockWithTile("simulator_data_hatch",
                () -> new HatchBlock<>(properties(), DataHatchTile.class, DataHatchTile::new), null);
        STRUCTURE_SIMULATOR = registry.registerBlockWithTile(
                "structure_simulator", StructureSimulatorBlock::new, null);
    }
}
