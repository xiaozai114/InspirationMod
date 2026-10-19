package com.starcrosser.inspiration.machine;

import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.machine.block.AlkalineReactorBlock;
import com.starcrosser.inspiration.machine.block.CoreDrillerBlock;
import com.starcrosser.inspiration.machine.block.EnergizedMatterFusionMachineBlock;
import com.starcrosser.inspiration.machine.block.HighEnergyInspirationAssemblerBlock;
import com.starcrosser.inspiration.machine.block.HighEnergyMatterReshaperMachineBlock;
import com.starcrosser.inspiration.machine.block.HighEnergyMineralTwisterBlock;
import com.starcrosser.inspiration.machine.block.InspirationAssemblerBlock;
import com.starcrosser.inspiration.machine.block.MatterFusionMachineBlock;
import com.starcrosser.inspiration.machine.block.MatterReshaperMachineBlock;
import com.starcrosser.inspiration.machine.block.MineralTwisterBlock;
import com.starcrosser.inspiration.machine.block.RtgGeneratorBlock;

/**
 * 机器注册中心。
 * <p>
 * 借助 Titanium 的 {@link DeferredRegistryHelper} 一次性完成方块、方块物品、方块实体类型
 * 与能量 / 物品 / 流体能力的注册。
 */
public class ModMachines {

    public static BlockWithTile MATTER_FUSION_MACHINE;
    /** 强能物质融合机：物质融合器的升级版，三个输入槽、两倍速度。 */
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

    public static void register() {
        DeferredRegistryHelper registry = new DeferredRegistryHelper(Inspiration.MODID);
        MATTER_FUSION_MACHINE = registry.registerBlockWithTile(
                "matter_fusion_machine", MatterFusionMachineBlock::new, null);
        ENERGIZED_MATTER_FUSION_MACHINE = registry.registerBlockWithTile(
                "energized_matter_fusion_machine", EnergizedMatterFusionMachineBlock::new, null);
        MATTER_RESHAPER = registry.registerBlockWithTile(
                "matter_reshaper", MatterReshaperMachineBlock::new, null);
        MINERAL_TWISTER = registry.registerBlockWithTile(
                "mineral_twister", MineralTwisterBlock::new, null);
        INSPIRATION_ASSEMBLER = registry.registerBlockWithTile(
                "inspiration_assembler", InspirationAssemblerBlock::new, null);
        CORE_DRILLER = registry.registerBlockWithTile(
                "core_driller", CoreDrillerBlock::new, null);
        HIGH_ENERGY_MINERAL_TWISTER = registry.registerBlockWithTile(
                "high_energy_mineral_twister", HighEnergyMineralTwisterBlock::new, null);
        HIGH_ENERGY_INSPIRATION_ASSEMBLER = registry.registerBlockWithTile(
                "high_energy_inspiration_assembler", HighEnergyInspirationAssemblerBlock::new, null);
        HIGH_ENERGY_MATTER_RESHAPER = registry.registerBlockWithTile(
                "high_energy_matter_reshaper", HighEnergyMatterReshaperMachineBlock::new, null);
        // 发电类：不跑配方，只烧燃料往外输电
        ALKALINE_REACTOR = registry.registerBlockWithTile(
                "alkaline_reactor", AlkalineReactorBlock::new, null);
        RTG_GENERATOR = registry.registerBlockWithTile(
                "rtg_generator", RtgGeneratorBlock::new, null);
    }
}
