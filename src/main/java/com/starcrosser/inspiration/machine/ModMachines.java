package com.starcrosser.inspiration.machine;

import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.machine.block.AlkalineReactorBlock;
import com.starcrosser.inspiration.machine.block.CoreDrillerBlock;
import com.starcrosser.inspiration.machine.block.CultureTankBlock;
import com.starcrosser.inspiration.machine.block.FluidExtractorBlock;
import com.starcrosser.inspiration.machine.block.InspirationAssemblerBlock;
import com.starcrosser.inspiration.machine.block.MatterFusionMachineBlock;
import com.starcrosser.inspiration.machine.block.MatterReshaperMachineBlock;
import com.starcrosser.inspiration.machine.block.MineralTwisterBlock;
import com.starcrosser.inspiration.machine.block.RawRefinerBlock;
import com.starcrosser.inspiration.machine.block.RtgGeneratorBlock;
import com.starcrosser.inspiration.machine.spec.MachineSpec;
import com.starcrosser.inspiration.registry.MachineRegistry;
import net.minecraft.world.item.DyeColor;

/**
 * 机器注册中心。
 * <p>
 * 借助 Titanium 的 {@link DeferredRegistryHelper} 一次性完成方块、方块物品、方块实体类型
 * 与能量 / 物品 / 流体能力的注册。
 * <p>
 * **同族机器共用一套方块与方块实体实现**，差别全部写进注册时这份 {@link MachineSpec}：
 * 等级（决定容量 / 速率的倍率 {@code 2^(tier-1)}）与槽数（决定一次能吃几种材料）互相独立。
 * 所以将来加一台等级 3 的机器，只要在下面再加一条注册，不用写新的方块实体类。
 */
public class ModMachines {

    /* ============================ 等级配置 ============================ */

    /** 物质融合机：等级 1，2 个物品输入槽。 */
    private static final MachineSpec FUSION_T1 = MachineSpec.of(1, 2, DyeColor.LIGHT_BLUE);
    /** 强能物质融合机：等级 2，3 个物品输入槽。 */
    private static final MachineSpec FUSION_T2 = MachineSpec.of(2, 3, DyeColor.LIGHT_BLUE);

    /** 物质重塑机：等级 1，2 个物品输入槽。 */
    private static final MachineSpec RESHAPER_T1 = MachineSpec.of(1, 2, DyeColor.GREEN);
    /** 高能物质重塑机：等级 2，仍是 2 个物品输入槽（两档共用同一套配方）。 */
    private static final MachineSpec RESHAPER_T2 = MachineSpec.of(2, 2, DyeColor.PURPLE);

    /** 矿物扭曲机：等级 1，1 个物品输入槽。 */
    private static final MachineSpec TWISTER_T1 = MachineSpec.of(1, 1, DyeColor.LIME);
    /** 高能矿物扭曲机：等级 2，1 个物品输入槽。 */
    private static final MachineSpec TWISTER_T2 = MachineSpec.of(2, 1, DyeColor.LIME);

    /** 地核钻探者：等级 1，1 个钻头槽。目前没有上位机型。 */
    private static final MachineSpec DRILLER_T1 = MachineSpec.of(1, 1, DyeColor.PURPLE);

    /** 流体提取机：等级 1，1 个输入槽。 */
    private static final MachineSpec EXTRACTOR_T1 = MachineSpec.of(1, 1, DyeColor.CYAN);

    /** 原料精炼机：等级 1，1 个输入槽（三个输出槽是固定的，不随配置变）。 */
    private static final MachineSpec REFINER_T1 = MachineSpec.of(1, 1, DyeColor.ORANGE);

    /** 培养缸：等级 1，1 个脑槽 + 1 个营养液罐。 */
    private static final MachineSpec CULTURE_TANK_T1 = MachineSpec.of(1, 1, DyeColor.LIME);

    /** 灵感装配台：等级 1，4 个物品输入槽 + 1 个流体输入槽。 */
    private static final MachineSpec ASSEMBLER_T1 = MachineSpec.of(1, 4, 1, DyeColor.YELLOW);
    /** 高能灵感装配台：等级 2，6 个物品输入槽 + 2 个流体输入槽。 */
    private static final MachineSpec ASSEMBLER_T2 = MachineSpec.of(2, 6, 2, DyeColor.YELLOW);

    /* ============================ 注册条目 ============================ */

    public static BlockWithTile MATTER_FUSION_MACHINE;
    /** 强能物质融合机：物质融合器的升级版，三个输入槽、数值翻倍。 */
    public static BlockWithTile ENERGIZED_MATTER_FUSION_MACHINE;
    public static BlockWithTile MATTER_RESHAPER;
    public static BlockWithTile MINERAL_TWISTER;
    public static BlockWithTile INSPIRATION_ASSEMBLER;
    /** 地核钻探者：向下掏竖井到基岩并把产物顶到上面的容器里。 */
    public static BlockWithTile CORE_DRILLER;
    /** 高能矿物扭曲机：矿物扭曲机的上位机型，等效时运 12，其余数值翻倍。 */
    public static BlockWithTile HIGH_ENERGY_MINERAL_TWISTER;
    /** 高能灵感装配台：6 物品槽 + 2 流体槽，本阶段最重要的合成机器。 */
    public static BlockWithTile HIGH_ENERGY_INSPIRATION_ASSEMBLER;
    /** 高能物质重塑机：与物质重塑机共用配方，数值翻倍且无额外效果。 */
    public static BlockWithTile HIGH_ENERGY_MATTER_RESHAPER;
    /** 碱性发电机：烧碱性物质发电的发电机，无副产物、无辐射。 */
    public static BlockWithTile ALKALINE_REACTOR;
    /** RTG反应堆：烧放射性物质发电，代价是往区块里放辐射。 */
    public static BlockWithTile RTG_GENERATOR;
    /** 流体提取机：把材料熔成液态金属。 */
    public static BlockWithTile FLUID_EXTRACTOR;
    /** 原料精炼机：粗矿 / 盐 / 核废料 → 主产物 + 副产物。 */
    public static BlockWithTile RAW_REFINER;
    /** 培养缸：村民脑产灵感、末影人脑产思维晶体，消耗营养液与电力。 */
    public static BlockWithTile CULTURE_TANK;

    /**
     * 一次性注册所有机器。
     * <p>
     * 走 {@link MachineRegistry#register} 而不是 Titanium 的裸调用：顺带把机器登记进
     * {@link MachineRegistry} 这份可遍历清单，语言 / 方块状态 / 标签 / 战利品表就都能循环生成，
     * 不用每台机器再手写一遍。
     * <p>
     * **注册顺序 = 清单顺序 = 标签 JSON 里的输出顺序**：基础机型 → 上位机型 → 发电类。
     * 改这个顺序会让 tag json 的内容重排（功能不变但 diff 变脏），所以别为了好看重排。
     */
    public static void register() {
        DeferredRegistryHelper registry = new DeferredRegistryHelper(Inspiration.MODID);

        MATTER_FUSION_MACHINE = MachineRegistry.register(registry, "matter_fusion_machine",
                "物质融合器", "Matter Fusion Machine",
                () -> new MatterFusionMachineBlock("matter_fusion_machine", FUSION_T1,
                        () -> MATTER_FUSION_MACHINE, 3.5F, 6.0F));
        ENERGIZED_MATTER_FUSION_MACHINE = MachineRegistry.register(registry, "energized_matter_fusion_machine",
                "强能物质融合机", "Energized Matter Fusion Machine",
                () -> new MatterFusionMachineBlock("energized_matter_fusion_machine", FUSION_T2,
                        () -> ENERGIZED_MATTER_FUSION_MACHINE, 3.5F, 8.0F));
        MATTER_RESHAPER = MachineRegistry.register(registry, "matter_reshaper",
                "物质重塑机", "Matter Reshaper",
                () -> new MatterReshaperMachineBlock("matter_reshaper", RESHAPER_T1,
                        () -> MATTER_RESHAPER, 4.5F, 9.0F));
        MINERAL_TWISTER = MachineRegistry.register(registry, "mineral_twister",
                "矿物扭曲机", "Mineral Twister",
                () -> new MineralTwisterBlock("mineral_twister", TWISTER_T1,
                        () -> MINERAL_TWISTER, 5.0F, 10.0F));
        INSPIRATION_ASSEMBLER = MachineRegistry.register(registry, "inspiration_assembler",
                "灵感装配台", "Inspiration Assembler",
                () -> new InspirationAssemblerBlock("inspiration_assembler", ASSEMBLER_T1,
                        () -> INSPIRATION_ASSEMBLER, 5.0F, 10.0F));
        CORE_DRILLER = MachineRegistry.register(registry, "core_driller",
                "地核钻探者", "Core Driller",
                () -> new CoreDrillerBlock("core_driller", DRILLER_T1,
                        () -> CORE_DRILLER, 6.0F, 12.0F));

        HIGH_ENERGY_MINERAL_TWISTER = MachineRegistry.register(registry, "high_energy_mineral_twister",
                "高能矿物扭曲机", "High-Energy Mineral Twister",
                () -> new MineralTwisterBlock("high_energy_mineral_twister", TWISTER_T2,
                        () -> HIGH_ENERGY_MINERAL_TWISTER, 5.0F, 12.0F));
        HIGH_ENERGY_INSPIRATION_ASSEMBLER = MachineRegistry.register(registry, "high_energy_inspiration_assembler",
                "高能灵感装配台", "High-Energy Inspiration Assembler",
                () -> new InspirationAssemblerBlock("high_energy_inspiration_assembler", ASSEMBLER_T2,
                        () -> HIGH_ENERGY_INSPIRATION_ASSEMBLER, 5.0F, 12.0F));
        HIGH_ENERGY_MATTER_RESHAPER = MachineRegistry.register(registry, "high_energy_matter_reshaper",
                "高能物质重塑机", "High-Energy Matter Reshaper",
                () -> new MatterReshaperMachineBlock("high_energy_matter_reshaper", RESHAPER_T2,
                        () -> HIGH_ENERGY_MATTER_RESHAPER, 5.0F, 12.0F));

        FLUID_EXTRACTOR = MachineRegistry.register(registry, "fluid_extractor",
                "流体提取机", "Fluid Extractor",
                () -> new FluidExtractorBlock("fluid_extractor", EXTRACTOR_T1,
                        () -> FLUID_EXTRACTOR, 5.0F, 10.0F));
        RAW_REFINER = MachineRegistry.register(registry, "raw_refiner",
                "原料精炼机", "Raw Refiner",
                () -> new RawRefinerBlock("raw_refiner", REFINER_T1,
                        () -> RAW_REFINER, 5.0F, 10.0F));
        CULTURE_TANK = MachineRegistry.register(registry, "culture_tank",
                "培养缸", "Culture Tank",
                () -> new CultureTankBlock("culture_tank", CULTURE_TANK_T1,
                        () -> CULTURE_TANK, 3.0F, 6.0F));

        // 发电类：不跑配方，只烧燃料往外输电
        ALKALINE_REACTOR = MachineRegistry.register(registry, "alkaline_reactor",
                "碱性发电机", "Alkaline Generator", AlkalineReactorBlock::new);
        RTG_GENERATOR = MachineRegistry.register(registry, "rtg_generator",
                "RTG反应堆", "RTG Reactor", RtgGeneratorBlock::new);
    }
}
