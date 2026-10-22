package com.starcrosser.inspiration.item;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.material.InspirationMaterial;
import com.starcrosser.inspiration.material.ModMaterials;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 创造模式物品栏：两页。
 * <p>
 * 分页规则是「有没有直接使用功能」：<b>灵感</b>页放能拿起来就用的东西 —— 食物、工具、
 * 机器、功能方块、插件、模板、结构数据、指导书；<b>材料</b>页放只作为合成原料存在的东西 ——
 * 矿、锭、粉、盐、晶、核心部件与废料、煤。
 * <p>
 * 分页之后两页内部仍然逐个显式写、不遍历清单，因为展示顺序是刻意排的：
 * 一条生产线的上下游要挨在一起（矿石 → 粗盐 → 盐 → 粉 → 锭 → 块），
 * 而清单顺序是声明顺序，跟玩法脉络对不上。
 * 唯一例外是锭类材料那一组：它们没有上下游关系，遍历 {@link ModMaterials#ALL}
 * 既能保证同一材料的锭与块挨着，加材料时也不用再来补这里。
 */
public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MOD_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Inspiration.MODID);

    /* ==================== 灵感页：能直接用的东西 ==================== */

    public static final Supplier<CreativeModeTab> INSPIRATION_TAB =
            CREATIVE_MOD_TABS.register("inspire_tab", ()-> CreativeModeTab.builder()
                    .icon(()->new ItemStack(ModItems.INSPIRATION.get()))
                    .title(Component.translatable("itemGroup.inspire_tab"))
                    .displayItems((parameters, output) -> {
                        // 灵感本体与食物
                        output.accept(ModItems.INSPIRATION);
                        output.accept(ModItems.COOKED_INSPIRATION);
                        output.accept(ModItems.INSPIREAD);
                        output.accept(ModItems.INSPIRRY);
                        output.accept(ModItems.SOD);
                        output.accept(ModItems.EXPLODING_CANDY);
                        // 灵感系列五件工具
                        output.accept(ModItems.EXP_SWORD);
                        output.accept(ModItems.STORM_SWORD);
                        output.accept(ModItems.STICKY_AXE);
                        output.accept(ModItems.HEAD_HUNTER_AXE);
                        output.accept(ModItems.EXP_PICKAXE);
                        output.accept(ModItems.EXP_HOE);
                        output.accept(ModItems.EXP_SHOVEL);
                        output.accept(ModItems.SCYTHE_OF_DEATH);
                        output.accept(ModItems.WORLD_BREAKER);
                        // 翡翠钢系列五件工具
                        output.accept(ModItems.JADE_STEEL_SWORD);
                        output.accept(ModItems.JADE_STEEL_AXE);
                        output.accept(ModItems.JADE_STEEL_PICKAXE);
                        output.accept(ModItems.JADE_STEEL_HOE);
                        output.accept(ModItems.JADE_STEEL_SHOVEL);
                        // 注能翡翠钢系列五件工具（长按右键蓄力换增益）
                        output.accept(ModItems.ENERGIZED_JADE_STEEL_SWORD);
                        output.accept(ModItems.ENERGIZED_JADE_STEEL_AXE);
                        output.accept(ModItems.ENERGIZED_JADE_STEEL_PICKAXE);
                        output.accept(ModItems.ENERGIZED_JADE_STEEL_HOE);
                        output.accept(ModItems.ENERGIZED_JADE_STEEL_SHOVEL);
                        // 升级模板：锻造台上把工具升一档
                        output.accept(ModItems.UNSTABLE_SMITHING_TEMPLATE);
                        output.accept(ModItems.JADE_STEEL_UPGRADE_TEMPLATE);
                        output.accept(ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE);
                        // 末地合金盾与两种量具
                        output.accept(ModItems.END_ALLOY_SHIELD);
                        output.accept(ModItems.GEIGER_COUNTER);
                        output.accept(ModItems.WORLD_DATA_DETECTOR);
                        // 玄武岩合金工具：打火石与钻头（地核钻探者的耗材）
                        output.accept(ModItems.BASALT_ALLOY_FIRESTRIKER);
                        output.accept(ModItems.BASALT_ALLOY_DRILL_BIT);
                        // 机器插件：四种，装进机器侧栏改变性能
                        output.accept(ModItems.CAPACITOR_PLUGIN);
                        output.accept(ModItems.INSPIRATION_PLUGIN);
                        output.accept(ModItems.SPEED_PLUGIN);
                        output.accept(ModItems.CAPACITOR_DOWNGRADE_PLUGIN);
                        // 信标与 TNT
                        output.accept(ModItems.EMPTY_BEACON);
                        output.accept(ModItems.POWER_BEACON);
                        output.accept(ModBlocks.INTENSIFIED_TNT);
                        output.accept(ModBlocks.EMPOWERED_TNT_ITEM);
                        output.accept(ModItems.MIND_FLUID_BUCKET.get());
                        // 思维流体反应生成的四类方块
                        output.accept(ModBlocks.DILUTED_INSPIRATION_CRYSTAL);
                        output.accept(ModBlocks.CONCENTRATED_INSPIRATION_CRYSTAL);
                        output.accept(ModBlocks.INSPIRATION_GEL);
                        output.accept(ModBlocks.MIND_CRYSTAL_ROCK);
                        // 机器在创造栏里是与同期物品穿插排列的（展示顺序是刻意排的：一台机器紧挨着它的
                        // 材料与产物），不是"所有机器挨在一起"，所以这里仍然逐个写、不遍历 MachineRegistry。
                        output.accept(ModMachines.MATTER_FUSION_MACHINE);
                        output.accept(ModMachines.ENERGIZED_MATTER_FUSION_MACHINE);
                        output.accept(ModMachines.MATTER_RESHAPER);
                        output.accept(ModMachines.MINERAL_TWISTER);
                        output.accept(ModMachines.INSPIRATION_ASSEMBLER);
                        output.accept(ModMachines.CORE_DRILLER);
                        output.accept(ModMachines.HIGH_ENERGY_MINERAL_TWISTER);
                        output.accept(ModMachines.HIGH_ENERGY_INSPIRATION_ASSEMBLER);
                        output.accept(ModMachines.HIGH_ENERGY_MATTER_RESHAPER);
                        output.accept(ModMachines.ALKALINE_REACTOR);
                        output.accept(ModMachines.RTG_GENERATOR);
                        // 注入合成：束缚器 —— 注入枪 —— 绑定器
                        output.accept(ModInjection.MATTER_BINDER);
                        output.accept(ModInjection.INJECTION_GUN);
                        output.accept(ModItems.BINDING_WAND);
                        // 结构模拟机：外壳 —— 五种仓室 —— 控制器 —— 两份结构数据 —— 投影仪
                        output.accept(ModBlocks.HIGH_ENERGY_MACHINE_CASING);
                        output.accept(ModMultiblocks.INPUT_HATCH);
                        output.accept(ModMultiblocks.OUTPUT_HATCH);
                        output.accept(ModMultiblocks.ENERGY_PORT);
                        output.accept(ModMultiblocks.INSPIRATION_HATCH);
                        output.accept(ModMultiblocks.DATA_HATCH);
                        output.accept(ModMultiblocks.STRUCTURE_SIMULATOR);
                        output.accept(ModItems.BLANK_DATA);
                        output.accept(ModItems.SHIPWRECK_DATA);
                        output.accept(ModItems.PROJECTOR);
                        output.accept(ModItems.GUIDE_BOOK);
                    })
                    .build());

    /* ==================== 材料页：只作为合成原料存在的东西 ==================== */

    public static final Supplier<CreativeModeTab> MATERIAL_TAB =
            CREATIVE_MOD_TABS.register("material_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModMaterials.CESIUM.item().get()))
                    .title(Component.translatable("itemGroup.material_tab"))
                    .displayItems((parameters, output) -> {
                        // 煤系：褐煤是最早的燃料，焦煤是它的冶炼产物
                        output.accept(ModItems.LIGNITE);
                        output.accept(ModBlocks.LIGNITE_BLOCK);
                        output.accept(ModItems.COKE);
                        // 钙系粉：钙粉遇水变氢氧化钙粉，还原粉是冶炼助剂
                        output.accept(ModItems.CALCIUM_DUST);
                        output.accept(ModItems.CALCIUM_HYDROXIDE_DUST);
                        output.accept(ModItems.REDUCTION_POWDER);
                        // 铯盐系列：粗盐 —— 盐 —— 矿 —— 盐块 —— 粉
                        output.accept(ModItems.RAW_CESIUM_SALT);
                        output.accept(ModItems.CESIUM_SALT);
                        output.accept(ModBlocks.CESIUM_SALT_ORE);
                        output.accept(ModBlocks.CESIUM_SALT_BLOCK);
                        output.accept(ModItems.CESIUM_DUST);
                        // 钫盐系列（流程与铯逐一对应，但全流程带辐射）
                        output.accept(ModBlocks.FRANCIUM_SALT_ORE);
                        output.accept(ModItems.RAW_FRANCIUM_SALT);
                        output.accept(ModItems.FRANCIUM_SALT);
                        output.accept(ModBlocks.FRANCIUM_SALT_BLOCK);
                        output.accept(ModItems.FRANCIUM_DUST);
                        // 无恒系列与思维系晶体
                        output.accept(ModBlocks.EPHEMERAL_ORE);
                        output.accept(ModItems.EPHEMERAL_MATERIAL);
                        output.accept(ModItems.MIND_CRYSTAL);
                        output.accept(ModItems.INSPIRATION_SHARD);
                        // 锭类材料：遍历清单，每个材料的锭紧挨着自己的方块
                        for (InspirationMaterial material : ModMaterials.ALL) {
                            output.accept(material.item());
                            if (material.hasBlock()) {
                                output.accept(material.block());
                            }
                            if (material.hasNugget()) {
                                output.accept(material.nugget());
                            }
                            if (material.hasGrit()) {
                                output.accept(material.grit());
                            }
                        }
                        // 核心部件与废料：合成机器用的中间产物
                        output.accept(ModItems.UNSTABLE_CORE);
                        output.accept(ModItems.REINFORCED_CORE);
                        output.accept(ModItems.POWERFUL_CORE);
                        output.accept(ModItems.HIGH_ENERGY_CORE);
                        output.accept(ModItems.GENERATION_CORE);
                        output.accept(ModItems.REACTION_CORE);
                        output.accept(ModItems.NUCLEAR_WASTE);
                        // 注入底材
                        output.accept(ModItems.BASALT_BASE);
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MOD_TABS.register(eventBus);
    }
}
