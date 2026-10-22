package com.starcrosser.inspiration.item;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MOD_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Inspiration.MODID);
    public static final Supplier<CreativeModeTab> INSPIRATION_TAB =
            CREATIVE_MOD_TABS.register("inspire_tab", ()-> CreativeModeTab.builder()
                    .icon(()->new ItemStack(ModItems.INSPIRATION.get()))
                    .title(Component.translatable("itemGroup.inspire_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.INSPIRATION);
                        output.accept(ModItems.LIGNITE);
                        output.accept(ModItems.COOKED_INSPIRATION);
                        output.accept(ModBlocks.LIGNITE_BLOCK);
                        output.accept(ModItems.INSPIREAD);
                        output.accept(ModItems.INSPIRRY);
                        output.accept(ModItems.COKE);
                        output.accept(ModItems.EXP_SWORD);
                        output.accept(ModItems.STORM_SWORD);
                        output.accept(ModItems.STICKY_AXE);
                        output.accept(ModItems.HEAD_HUNTER_AXE);
                        output.accept(ModItems.EXP_PICKAXE);
                        output.accept(ModItems.REDSTONE_INGOT);
                        output.accept(ModItems.LAPIS_INGOT);
                        output.accept(ModBlocks.LAPIS_INGOT_BLOCK);
                        output.accept(ModBlocks.REDSTONE_INGOT_BLOCK);
                        output.accept(ModItems.UNSTABLE_CORE);
                        output.accept(ModItems.UNSTABLE_SMITHING_TEMPLATE);
                        output.accept(ModItems.EXTREME_ALLOY);
                        output.accept(ModBlocks.EPHEMERAL_ORE);
                        output.accept(ModItems.EPHEMERAL_MATERIAL);
                        output.accept(ModItems.SOD);
                        output.accept(ModItems.EXP_HOE);
                        output.accept(ModItems.EXP_SHOVEL);
                        output.accept(ModItems.SCYTHE_OF_DEATH);
                        output.accept(ModItems.EMPTY_BEACON);
                        output.accept(ModItems.CESIUM_SALT);
                        output.accept(ModItems.RAW_CESIUM_SALT);
                        output.accept(ModBlocks.CESIUM_SALT_ORE);
                        output.accept(ModBlocks.INTENSIFIED_TNT);
                        output.accept(ModItems.EXPLODING_CANDY);
                        output.accept(ModItems.MIND_FLUID_BUCKET.get());
                        output.accept(ModItems.POWER_BEACON);
                        output.accept(ModItems.CALCIUM_HYDROXIDE_DUST);
                        output.accept(ModItems.CALCIUM_DUST);
                        output.accept(ModItems.REDUCTION_POWDER);
                        output.accept(ModItems.CESIUM_DUST);
                        output.accept(ModBlocks.CESIUM_SALT_BLOCK);
                        output.accept(ModBlocks.CESIUM_BLOCK);
                        output.accept(ModItems.CESIUM_INGOT);
                        output.accept(ModItems.JADE_STEEL_INGOT);
                        output.accept(ModBlocks.EMPOWERED_TNT_ITEM);
                        output.accept(ModItems.BASALT_ALLOY);
                        output.accept(ModItems.REINFORCED_CORE);
                        output.accept(ModItems.POWERFUL_CORE);
                        output.accept(ModItems.HIGH_ENERGY_CORE);
                        output.accept(ModItems.JADE_STEEL_UPGRADE_TEMPLATE);
                        output.accept(ModItems.JADE_STEEL_SWORD);
                        output.accept(ModItems.JADE_STEEL_AXE);
                        output.accept(ModItems.JADE_STEEL_PICKAXE);
                        output.accept(ModItems.JADE_STEEL_HOE);
                        output.accept(ModItems.JADE_STEEL_SHOVEL);
                        output.accept(ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE);
                        output.accept(ModItems.ENERGIZED_JADE_STEEL_SWORD);
                        output.accept(ModItems.ENERGIZED_JADE_STEEL_AXE);
                        output.accept(ModItems.ENERGIZED_JADE_STEEL_PICKAXE);
                        output.accept(ModItems.ENERGIZED_JADE_STEEL_HOE);
                        output.accept(ModItems.ENERGIZED_JADE_STEEL_SHOVEL);
                        output.accept(ModItems.BASALT_ALLOY_FIRESTRIKER);
                        output.accept(ModItems.PLASMA_INGOT);
                        output.accept(ModItems.CAPACITOR_PLUGIN);
                        output.accept(ModItems.INSPIRATION_PLUGIN);
                        output.accept(ModItems.SPEED_PLUGIN);
                        output.accept(ModItems.CAPACITOR_DOWNGRADE_PLUGIN);
                        // 机器在创造栏里是与同期物品穿插排列的（展示顺序是刻意排的：一台机器紧挨着它的
                        // 材料与产物），不是"所有机器挨在一起"，所以这里仍然逐个写、不遍历 MachineRegistry。
                        output.accept(ModMachines.MATTER_FUSION_MACHINE);
                        output.accept(ModMachines.ENERGIZED_MATTER_FUSION_MACHINE);
                        output.accept(ModMachines.MATTER_RESHAPER);
                        output.accept(ModMachines.MINERAL_TWISTER);
                        output.accept(ModItems.INSPIRATION_SHARD);
                        output.accept(ModItems.MIND_CRYSTAL);
                        output.accept(ModItems.MIND_MATRIX_INGOT);
                        output.accept(ModBlocks.DILUTED_INSPIRATION_CRYSTAL);
                        output.accept(ModBlocks.CONCENTRATED_INSPIRATION_CRYSTAL);
                        output.accept(ModBlocks.INSPIRATION_GEL);
                        output.accept(ModBlocks.MIND_CRYSTAL_ROCK);
                        output.accept(ModMachines.INSPIRATION_ASSEMBLER);
                        // 注入合成：底座 —— 束缚器 —— 注入枪 —— 绑定器 —— 两种新物品
                        output.accept(ModBlocks.BASALT_ALLOY_BLOCK);
                        output.accept(ModBlocks.SCULK_ALLOY_BLOCK);
                        output.accept(ModItems.SCULK_ALLOY);
                        output.accept(ModInjection.MATTER_BINDER);
                        output.accept(ModInjection.INJECTION_GUN);
                        output.accept(ModItems.BINDING_WAND);
                        output.accept(ModItems.BASALT_BASE);
                        output.accept(ModItems.ENERGIZED_JADE_STEEL);
                        // 辐射：钫盐系列与盖革计数器
                        output.accept(ModBlocks.FRANCIUM_SALT_ORE);
                        output.accept(ModItems.RAW_FRANCIUM_SALT);
                        output.accept(ModItems.FRANCIUM_SALT);
                        output.accept(ModBlocks.FRANCIUM_SALT_BLOCK);
                        output.accept(ModItems.FRANCIUM_DUST);
                        output.accept(ModBlocks.FRANCIUM_BLOCK);
                        output.accept(ModItems.FRANCIUM_INGOT);
                        output.accept(ModItems.GEIGER_COUNTER);
                        // 下一阶段主材与量具
                        output.accept(ModItems.END_ALLOY);
                        output.accept(ModItems.EPHEMERAL_ALLOY);
                        output.accept(ModItems.END_ALLOY_SHIELD);
                        output.accept(ModItems.WORLD_DATA_DETECTOR);
                        output.accept(ModItems.WORLD_BREAKER);
                        output.accept(ModItems.GUIDE_BOOK);
                        // 地核钻探者与其耗材
                        output.accept(ModMachines.CORE_DRILLER);
                        output.accept(ModItems.BASALT_ALLOY_DRILL_BIT);
                        // 高能阶段：三种注能锭与三台高能机器
                        output.accept(ModItems.ENERGIZED_LAPIS_INGOT);
                        output.accept(ModItems.ENERGIZED_PLASMA_INGOT);
                        output.accept(ModItems.ENERGIZED_REDSTONE_INGOT);
                        output.accept(ModMachines.HIGH_ENERGY_MINERAL_TWISTER);
                        output.accept(ModMachines.HIGH_ENERGY_INSPIRATION_ASSEMBLER);
                        output.accept(ModMachines.HIGH_ENERGY_MATTER_RESHAPER);
                        // 发电类：两种核心 —— 两台发电机 —— 核废料
                        output.accept(ModItems.GENERATION_CORE);
                        output.accept(ModItems.REACTION_CORE);
                        output.accept(ModMachines.ALKALINE_REACTOR);
                        output.accept(ModMachines.RTG_GENERATOR);
                        output.accept(ModItems.NUCLEAR_WASTE);
                        // 结构模拟机：外壳 —— 五种仓室 —— 控制器 —— 两份结构数据
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
                    })
                    .build());
    public static void register(IEventBus eventBus) {
        CREATIVE_MOD_TABS.register(eventBus);
    }
}
