// 文件：ModEnUsLangProvider.java
package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import com.starcrosser.inspiration.registry.MachineRegistry;
import com.starcrosser.inspiration.material.ModMaterials;
import com.starcrosser.inspiration.material.InspirationMaterial;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModEnUsLangProvider extends LanguageProvider {

    public ModEnUsLangProvider(PackOutput output) {
        super(output, Inspiration.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // 物品
        add(ModItems.INSPIRATION.get(), "inspiration");
        add(ModItems.COOKED_INSPIRATION.get(), "cooked inspiration");
        add(ModItems.LIGNITE.get(), "lignite");
        add(ModItems.INSPIREAD.get(), "inspiread");
        add(ModItems.INSPIRRY.get(), "inspirry");
        add(ModItems.COKE.get(), "coke");
        add(ModItems.EXP_SWORD.get(), "Inspiration Sword");
        add(ModItems.STORM_SWORD.get(), "Storm Sword");
        add(ModItems.STICKY_AXE.get(), "Inspiration Axe");
        add(ModItems.HEAD_HUNTER_AXE.get(), "Head Hunter");
        add(ModItems.EXP_PICKAXE.get(), "Inspiration Pickaxe");
        add(ModItems.UNSTABLE_CORE.get(), "§7Unstable Core");
        add(ModItems.UNSTABLE_SMITHING_TEMPLATE.get(), "§7Unstable Smithing Template");
        add(ModItems.WORLD_BREAKER.get(), "§9WORLD§4BREAKER§5XX");
        add(ModItems.EPHEMERAL_MATERIAL.get(), "§5Ephemeral Material");
        add(ModItems.SOD.get(), "Sod");
        add(ModItems.EXP_HOE.get(), "Inspiration Hoe");
        add(ModItems.EXP_SHOVEL.get(), "Inspiration Shovel");
        add(ModItems.SCYTHE_OF_DEATH.get(), "Scythe of Death");
        add(ModItems.EMPTY_BEACON.get(), "Empty Beacon");
        add(ModItems.CESIUM_SALT.get(), "Cesium Salt");
        add(ModBlocks.CESIUM_SALT_ORE.get(), "Cesium Salt Ore");
        add(ModItems.RAW_CESIUM_SALT.get(), "Raw Cesium Salt");
        add(ModItems.EXPLODING_CANDY.get(),"Exploding Candy");
        add(ModItems.POWER_BEACON.get(), "Power Beacon");
        add(ModItems.MIND_FLUID_BUCKET.get(), "Mind Fluid Bucket");
        add(ModItems.MOLTEN_PLASMA_BUCKET.get(), "Molten Plasma Bucket");
        add(ModItems.NUTRIENT_FLUID_BUCKET.get(), "Nutrient Fluid Bucket");
        add(ModItems.VILLAGER_BRAIN.get(), "Villager Brain");
        add(ModItems.ENDERMAN_BRAIN.get(), "Enderman Brain");
        add(ModItems.INACTIVE_BRAIN.get(), "Inactive Brain");
        add(ModItems.PARASITE.get(), "Parasite");
        add(ModItems.MECHANICAL_PARASITE.get(), "Mechanical Parasite");
        add("fluid_type.inspiration.nutrient_fluid", "Nutrient Fluid");
        add("effect.inspiration.parasite", "Parasitized");
        add("effect.inspiration.mechanical_parasite", "Mechanically Parasitized");
        add("block.inspiration.cracked_bedrock", "Cracked Bedrock");
        add("block.inspiration.void_energy_extractor", "Void Energy Extractor");
        add("block.inspiration.void_energy_collector", "Void Energy Collector");
        add("block.inspiration.void_injection_gun", "Void Injection Gun");
        add("block.inspiration.void_energy_relay", "Void Energy Relay");
        add("item.inspiration.sculk_alloy_drill_bit", "Sculk Alloy Drill Bit");
        add("item.inspiration.unstable_metal", "Unstable Metal");
        add("item.inspiration.weakened_core", "Weakened Core");
        add("item.inspiration.void_plasma_ingot", "Void Plasma Ingot");
        add("item.inspiration.void_dew", "Void Dew");
        add("item.inspiration.null_point_module", "Null Point Module");
        add("item.inspiration.zero_point_amplifier", "Zero-Point Amplifier");
        add("item.inspiration.compressed_redstone_ingot", "Compressed Redstone Ingot");
        add("item.inspiration.void_goggles", "Void Goggles");
        add("item.inspiration.mineshaft_data", "Mineshaft Data");
        add("item.inspiration.monster_room_data", "Monster Room Data");
        add("item.inspiration.desert_pyramid_data", "Desert Pyramid Data");
        add("item.inspiration.desert_well_data", "Desert Well Data");
        add("item.inspiration.jungle_temple_data", "Jungle Temple Data");
        add("item.inspiration.igloo_data", "Igloo Data");
        add("item.inspiration.ocean_ruin_data", "Ocean Ruins Data");
        add("item.inspiration.trail_ruins_data", "Trail Ruins Data");
        add("item.inspiration.buried_treasure_data", "Buried Treasure Data");
        add("item.inspiration.village_data", "Village Data");
        add("item.inspiration.pillager_outpost_data", "Pillager Outpost Data");
        add("item.inspiration.stronghold_data", "Stronghold Data");
        add("item.inspiration.woodland_mansion_data", "Woodland Mansion Data");
        add("item.inspiration.ancient_city_data", "Ancient City Data");
        add("item.inspiration.ruined_portal_data", "Ruined Portal Data");
        add("item.inspiration.nether_fortress_data", "Nether Fortress Data");
        add("item.inspiration.bastion_remnant_data", "Bastion Remnant Data");
        add("item.inspiration.end_city_data", "End City Data");
        add("item.inspiration.trial_chambers_data", "Trial Chambers Data");
        add("item.inspiration.trial_vault_data", "Vault Data");
        add("item.inspiration.ominous_vault_data", "Ominous Vault Data");
        add("item.inspiration.nether_data", "Nether Data");
        add("item.inspiration.end_data", "End Data");
        add("block.inspiration.tungsten_ore", "Tungsten Ore");
        add("fluid_type.inspiration.void_fluid", "Void Fluid");
        add("block.inspiration.void_fluid", "Void Fluid");
        add("item.inspiration.void_fluid_bucket", "Void Fluid Bucket");
        add("jei.inspiration.loot.minecraft.chests.abandoned_mineshaft", "Minecart chest");
        add("jei.inspiration.loot.minecraft.chests.simple_dungeon", "Dungeon chest");
        add("jei.inspiration.loot.minecraft.chests.desert_pyramid", "Pyramid chest");
        add("jei.inspiration.loot.minecraft.archaeology.desert_pyramid", "Suspicious sand");
        add("jei.inspiration.loot.minecraft.archaeology.desert_well", "Well archaeology");
        add("jei.inspiration.loot.minecraft.chests.jungle_temple", "Temple chest");
        add("jei.inspiration.loot.minecraft.chests.jungle_temple_dispenser", "Arrow trap");
        add("jei.inspiration.loot.minecraft.chests.igloo_chest", "Basement chest");
        add("jei.inspiration.loot.minecraft.chests.underwater_ruin_big", "Large ruin chest");
        add("jei.inspiration.loot.minecraft.chests.underwater_ruin_small", "Small ruin chest");
        add("jei.inspiration.loot.minecraft.archaeology.ocean_ruin_warm", "Warm ruin sand");
        add("jei.inspiration.loot.minecraft.archaeology.ocean_ruin_cold", "Cold ruin gravel");
        add("jei.inspiration.loot.minecraft.archaeology.trail_ruins_common", "Common archaeology");
        add("jei.inspiration.loot.minecraft.archaeology.trail_ruins_rare", "Rare archaeology");
        add("jei.inspiration.loot.minecraft.chests.buried_treasure", "Treasure chest");
        add("jei.inspiration.loot.minecraft.chests.pillager_outpost", "Outpost chest");
        add("jei.inspiration.loot.minecraft.chests.stronghold_corridor", "Corridor chest");
        add("jei.inspiration.loot.minecraft.chests.stronghold_crossing", "Crossing chest");
        add("jei.inspiration.loot.minecraft.chests.stronghold_library", "Library chest");
        add("jei.inspiration.loot.minecraft.chests.woodland_mansion", "Mansion chest");
        add("jei.inspiration.loot.minecraft.chests.ancient_city", "City chest");
        add("jei.inspiration.loot.minecraft.chests.ancient_city_ice_box", "Ice box");
        add("jei.inspiration.loot.minecraft.chests.ruined_portal", "Portal chest");
        add("jei.inspiration.loot.minecraft.chests.nether_bridge", "Fortress chest");
        add("jei.inspiration.loot.minecraft.chests.bastion_bridge", "Bridge chest");
        add("jei.inspiration.loot.minecraft.chests.bastion_hoglin_stable", "Stable chest");
        add("jei.inspiration.loot.minecraft.chests.bastion_other", "Generic chest");
        add("jei.inspiration.loot.minecraft.chests.bastion_treasure", "Treasure chest");
        add("jei.inspiration.loot.minecraft.chests.end_city_treasure", "City treasure");
        add("jei.inspiration.loot.minecraft.chests.trial_chambers.entrance", "Entrance chest");
        add("jei.inspiration.loot.minecraft.chests.trial_chambers.corridor", "Corridor chest");
        add("jei.inspiration.loot.minecraft.chests.trial_chambers.intersection", "Junction chest");
        add("jei.inspiration.loot.minecraft.chests.trial_chambers.intersection_barrel", "Junction barrel");
        add("jei.inspiration.loot.minecraft.chests.trial_chambers.supply", "Supply chest");
        add("jei.inspiration.loot.minecraft.pots.trial_chambers.corridor", "Corridor pot");
        add("jei.inspiration.loot.minecraft.dispensers.trial_chambers.chamber", "Chamber dispenser");
        add("jei.inspiration.loot.minecraft.dispensers.trial_chambers.corridor", "Corridor dispenser");
        add("jei.inspiration.loot.minecraft.dispensers.trial_chambers.water", "Water dispenser");
        add("jei.inspiration.loot.minecraft.chests.trial_chambers.reward", "Vault reward");
        add("jei.inspiration.loot.minecraft.chests.trial_chambers.reward_ominous", "Ominous reward");
        add("jei.inspiration.loot.minecraft.chests.village.village_armorer", "Armorer");
        add("jei.inspiration.loot.minecraft.chests.village.village_butcher", "Butcher");
        add("jei.inspiration.loot.minecraft.chests.village.village_cartographer", "Cartographer");
        add("jei.inspiration.loot.minecraft.chests.village.village_desert_house", "Desert house");
        add("jei.inspiration.loot.minecraft.chests.village.village_fisher", "Fisherman");
        add("jei.inspiration.loot.minecraft.chests.village.village_fletcher", "Fletcher");
        add("jei.inspiration.loot.minecraft.chests.village.village_mason", "Mason");
        add("jei.inspiration.loot.minecraft.chests.village.village_plains_house", "Plains house");
        add("jei.inspiration.loot.minecraft.chests.village.village_savanna_house", "Savanna house");
        add("jei.inspiration.loot.minecraft.chests.village.village_shepherd", "Shepherd");
        add("jei.inspiration.loot.minecraft.chests.village.village_snowy_house", "Snowy house");
        add("jei.inspiration.loot.minecraft.chests.village.village_taiga_house", "Taiga house");
        add("jei.inspiration.loot.minecraft.chests.village.village_tannery", "Tannery");
        add("jei.inspiration.loot.minecraft.chests.village.village_temple", "Temple");
        add("jei.inspiration.loot.minecraft.chests.village.village_toolsmith", "Toolsmith");
        add("jei.inspiration.loot.minecraft.chests.village.village_weaponsmith", "Weaponsmith");
        add("item.inspiration.overworld_data", "Overworld Data");
        add("item.inspiration.void_alloy", "Void Alloy");
        add(ModItems.RESONANT_MIND_LATTICE.get(), "Resonant Mind Lattice");
        add(ModItems.RESONANT_INSPIRATION.get(), "Resonant Inspiration");
        add(ModItems.RESONANT_END_ALLOY.get(), "Resonant End Alloy");
        add(ModItems.QUANTUM_INGOT.get(), "Quantum Ingot");
        add("tooltip.inspiration.sculk_drill_bit.cracks", "Cracks the bedrock block it finally reaches.");
        add("void_energy.stage.weak", "Weak");
        add("void_energy.stage.climbing", "Climbing");
        add("void_energy.stage.plateau", "Plateau");
        add("void_energy.stage.oscillating", "Oscillating");
        add("void_energy.stage.peak", "Peak");
        add("void_energy.stage.dangerous", "Dangerous");
        add("void_energy.stage.celestial", "Celestial");
        add("message.inspiration.void_cluster", "Void energy: %s ve (%s)");
        add(ModItems.CALCIUM_HYDROXIDE_DUST.get(), "Calcium Hydroxide");
        add(ModItems.CALCIUM_DUST.get(), "Calcium Dust");
        add(ModItems.CESIUM_DUST.get(), "Cesium Dust");
        // 材料：锭 / 方块 / 粒 / 尘，遍历清单（语言文件按 key 排序输出，这里的顺序不影响生成结果）
        for (InspirationMaterial material : ModMaterials.ALL) {
            add(material.item().get(), material.enItem());
            if (material.hasBlock()) {
                add(material.block().get(), material.enBlock());
            }
            if (material.hasNugget()) {
                add(material.nugget().get(), material.enNugget());
            }
            if (material.hasGrit()) {
                add(material.grit().get(), material.enGrit());
            }
        }
        add(ModItems.REDUCTION_POWDER.get(), "Reduction Powder");
        add(ModItems.REINFORCED_CORE.get(), "Reinforced Core");
        add(ModItems.POWERFUL_CORE.get(), "Powerful Core");
        add(ModItems.HIGH_ENERGY_CORE.get(), "High-Energy Core");
        add(ModItems.GENERATION_CORE.get(), "Generation Core");
        add(ModItems.REACTION_CORE.get(), "Reaction Core");
        add(ModItems.NUCLEAR_WASTE.get(), "Nuclear Waste");
        add(ModItems.JADE_STEEL_UPGRADE_TEMPLATE.get(), "Jade Steel Upgrade Template");
        add(ModItems.BASALT_ALLOY_FIRESTRIKER.get(), "Basalt Alloy Firestriker");
        add(ModItems.BASALT_ALLOY_DRILL_BIT.get(), "Basalt Alloy Drill Bit");
        add(ModItems.CAPACITOR_PLUGIN.get(), "Capacitor Plugin");
        add(ModItems.HIGH_ENERGY_CAPACITOR_PLUGIN.get(), "High-Energy Capacitor Plugin");
        add(ModItems.INSPIRATION_PLUGIN.get(), "Inspiration Plugin");
        add(ModItems.HIGH_ENERGY_INSPIRATION_PLUGIN.get(), "High-Energy Inspiration Plugin");
        add(ModItems.SPEED_PLUGIN.get(), "Speed Plugin");
        add(ModItems.HIGH_ENERGY_SPEED_PLUGIN.get(), "High-Energy Speed Plugin");
        add(ModItems.CAPACITOR_DOWNGRADE_PLUGIN.get(), "Capacitor Downgrade Plugin");
        add(ModItems.RECYCLING_PLUGIN.get(), "Recycling Upgrade");
        add(ModItems.INSPIRATION_SHARD.get(), "Inspiration Shard");
        add(ModItems.MIND_CRYSTAL.get(), "Mind Crystal");
        add(ModItems.JADE_STEEL_SWORD.get(), "Jade Steel Sword");
        add(ModItems.JADE_STEEL_AXE.get(), "Jade Steel Axe");
        add(ModItems.JADE_STEEL_PICKAXE.get(), "Jade Steel Pickaxe");
        add(ModItems.JADE_STEEL_HOE.get(), "Jade Steel Hoe");
        add(ModItems.JADE_STEEL_SHOVEL.get(), "Jade Steel Shovel");
        add(ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get(), "Energized Jade Steel Upgrade Template");
        add(ModItems.ENERGIZED_JADE_STEEL_SWORD.get(), "Energized Jade Steel Sword");
        add(ModItems.ENERGIZED_JADE_STEEL_AXE.get(), "Energized Jade Steel Axe");
        add(ModItems.ENERGIZED_JADE_STEEL_PICKAXE.get(), "Energized Jade Steel Pickaxe");
        add(ModItems.ENERGIZED_JADE_STEEL_HOE.get(), "Energized Jade Steel Hoe");
        add(ModItems.ENERGIZED_JADE_STEEL_SHOVEL.get(), "Energized Jade Steel Shovel");
        add(ModBlocks.DILUTED_INSPIRATION_CRYSTAL.get(), "Diluted Inspiration Crystal");
        add(ModBlocks.CONCENTRATED_INSPIRATION_CRYSTAL.get(), "Concentrated Inspiration Crystal");
        add(ModBlocks.INSPIRATION_GEL.get(), "Inspiration Gel");
        add(ModBlocks.MIND_CRYSTAL_ROCK.get(), "Mind Crystal Rock");
        add(ModBlocks.CESIUM_SALT_BLOCK.get(), "Cesium Salt Block");
        add(ModBlocks.EMPOWERED_TNT_ITEM.get(), "Empowered TNT");
        // 注入合成
        add(ModInjection.MATTER_BINDER.getBlock(), "Matter Binder");
        add(ModInjection.INJECTION_GUN.getBlock(), "Injection Gun");
        add(ModItems.BINDING_WAND.get(), "Binding Wand");
        add(ModItems.BASALT_BASE.get(), "Basalt Base");
        // 辐射：钫盐系列与盖革计数器
        add(ModBlocks.FRANCIUM_SALT_ORE.get(), "Francium Salt Ore");
        add(ModItems.RAW_FRANCIUM_SALT.get(), "Raw Francium Salt");
        add(ModItems.FRANCIUM_SALT.get(), "Francium Salt");
        add(ModBlocks.FRANCIUM_SALT_BLOCK.get(), "Francium Salt Block");
        add(ModItems.FRANCIUM_DUST.get(), "Francium Dust");
        add(ModItems.GEIGER_COUNTER.get(), "Geiger Counter");
        add(ModItems.END_ALLOY_SHIELD.get(), "End Alloy Shield");
        add(ModItems.WORLD_DATA_DETECTOR.get(), "World Data Detector");
        add(ModItems.GUIDE_BOOK.get(), "Inspiration Guide");
        // 高能阶段
        // 多方块：结构模拟机
        add(ModBlocks.HIGH_ENERGY_MACHINE_CASING.get(), "High-Energy Machine Casing");
        add(ModMultiblocks.INPUT_HATCH.getBlock(), "Input Hatch");
        add(ModMultiblocks.OUTPUT_HATCH.getBlock(), "Output Hatch");
        add(ModMultiblocks.ENERGY_PORT.getBlock(), "Energy Port");
        add(ModMultiblocks.INSPIRATION_HATCH.getBlock(), "Inspiration Hatch");
        add(ModMultiblocks.DATA_HATCH.getBlock(), "Data Hatch");
        add(ModItems.BLANK_DATA.get(), "Blank Data");
        add(ModItems.SHIPWRECK_DATA.get(), "Shipwreck Data");
        add(ModItems.PROJECTOR.get(), "Structure Projector");
        // 机器：显示名写在注册处（MachineRegistry），这里只管遍历
        for (MachineRegistry.MachineEntry machine : MachineRegistry.machines()) {
            add(machine.block(), machine.enName());
        }
        add(ModMultiblocks.STRUCTURE_SIMULATOR.getBlock(), "Structure Simulator");
        add(ModMultiblocks.MINERAL_SIMULATOR.getBlock(), "Mineral Simulator");
        // 流体
        add("fluid_type.inspiration.inspiration_charge", "Inspiration Charge");
        add("fluid_type.inspiration.molten_ephemeral_alloy", "Molten Ephemeral Alloy");
        add("item.inspiration.molten_ephemeral_alloy_bucket", "Molten Ephemeral Alloy Bucket");
        add("fluid_type.inspiration.molten_plasma", "Molten Plasma");
        // 方块
        add(ModBlocks.LIGNITE_BLOCK.get(), "lignite block");
        add(ModBlocks.INTENSIFIED_TNT.get(), "intensified_tnt");
        add(ModBlocks.EPHEMERAL_ORE.get(), "§5Ephemeral Ore");
        add(ModBlocks.MIND_FLUID_BLOCK.get(), "Mind Fluid");

        // JEI 配方分类
        add("jei.inspiration.fusion", "Matter Fusion");
        add("jei.inspiration.reshaping", "Matter Reshaping");
        add("jei.inspiration.right_click_block", "Right-Click Conversion");
        add("jei.inspiration.right_click_block.hint", "Right-click the block while holding the item");
        add("jei.inspiration.right_click_block.consume", "Consumes %s item(s) each time");
        add("jei.inspiration.twisting", "Mineral Twisting");
        add("jei.inspiration.twisting.fortune", "Fortune %s: yields %s-%s, avg %s");
        add("jei.inspiration.assembling", "Inspiration Assembling");
        add("jei.inspiration.assembling.fluid", "Fluid input: %s mB %s");
        // 单方块机器共用的两行：基准耗时与基准每刻耗电
        add("jei.inspiration.machine.duration", "Time: %s ticks");
        add("jei.inspiration.machine.energy_per_tick", "Energy: %s FE/t");
        add("jei.inspiration.fluid_extracting", "Fluid Extracting");
        add("jei.inspiration.fluid_extracting.output", "Output: %s mB");
        add("jei.inspiration.refining", "Raw Refining");
        add("jei.inspiration.refining.byproduct", "Byproduct");
        add("jei.inspiration.refining.recycling", "A Recycling Upgrade doubles byproducts");
        add("jei.inspiration.void_injection", "Void Injection");
        add("jei.inspiration.void_injection.cost", "Spends 10% energy");
        add("jei.inspiration.void_injection.window", "Cost %s~%s ve");
        add("jei.inspiration.void_injection.cluster", "Cluster %s~%s ve");
        add("jei.inspiration.void_condensation", "Void Condensation");
        add("jei.inspiration.void_condensation.intake", "16 ve + 500 FE per tick");
        add("jei.inspiration.void_condensation.ratio", "1 unit per %s ve (%s ticks)");
        add("jei.inspiration.injection", "Matter Injection");
        add("jei.inspiration.injection.role_inject", "Injected material");
        add("jei.inspiration.injection.role_base", "Base material");
        add("jei.inspiration.injection.tier", "Tier %s");
        add("jei.inspiration.injection.min_energy", "Min energy: %s FE");
        add("jei.inspiration.injection.max_energy", "Max energy: %s FE");

        // 侧向配置按钮上方的短标签；必须压在三个字符内，否则会挤到隔壁按钮
        add("gui.inspiration.facing.input", "In");
        add("gui.inspiration.facing.output", "Out");
        add("gui.inspiration.facing.inspiration", "Spr");
        add("gui.inspiration.facing.inspiration_charge", "Chg");
        add("gui.inspiration.facing.fluid_input", "Flu");
        add("gui.inspiration.facing.fluid_input_2", "Flu");
        add("gui.inspiration.facing.fluid_output", "Flu");
        add("gui.inspiration.facing.drill", "Bit");
        add("gui.inspiration.facing.fuel", "Fuel");
        // Titanium 悬停提示：键是 tooltip.titanium.facing_handler.<组件名小写>
        add("tooltip.titanium.facing_handler.input", "Item input");
        add("tooltip.titanium.facing_handler.output", "Item output");
        add("tooltip.titanium.facing_handler.inspiration", "Inspiration item");
        add("tooltip.titanium.facing_handler.inspiration_charge", "Inspiration charge tank");
        add("tooltip.titanium.facing_handler.fluid_input", "Fluid input");
        add("tooltip.titanium.facing_handler.fluid_input_2", "Second fluid input");
        add("tooltip.titanium.facing_handler.fluid_output", "Fluid output");
        add("tooltip.titanium.facing_handler.drill", "Drill bit");
        add("tooltip.titanium.facing_handler.fuel", "Fuel input");
        add("tooltip.titanium.facing_handler.inject", "Material to inject");
        add("tooltip.titanium.facing_handler.base", "Base material");

        // 工具提示
        add("tooltip.inspiration.exp_sword.description", "Drops %s experience per hit");
        add("tooltip.inspiration.storm_sowrd.description", "Right click for windburst");
        add("tooltip.inspiration.sticky_axe.description", "Try Stripping some wood?");
        add("tooltip.inspiration.sticky_axe.experience", "Drops %s experience when stripping");
        add("tooltip.inspiration.exp_pickaxe.description", "Drops %s experience per block mined");
        add("tooltip.inspiration.exp_hoe.description", "Grass blocks yield sod and %s experience");
        add("tooltip.inspiration.exp_shovel.description", "Pathing grass yields sod and %s experience");
        add("tooltip.inspiration.energized_jade_steel.charged_buff",
                "Hold right-click for 1.5s to gain %s (costs %s experience, lasts %s s)");
        add("tooltip.inspiration.exploding_candy.description", " ");
        add("tooltip.inspiration.inspiration_shard.description", "Insert into a machine's inspiration slot for %s mB of charge");

        // 绑定器
        add("tooltip.inspiration.binding_wand.description", "Right-click a Matter Binder to select it, then right-click an Injection Gun to link them; sneak-right-click the gun to unlink");
        add("tooltip.inspiration.binding_wand.none", "No Matter Binder selected");
        add("tooltip.inspiration.binding_wand.selected", "Selected: (%s, %s, %s)");
        add("message.inspiration.binding_wand.selected", "Matter Binder recorded at (%s, %s, %s)");
        add("message.inspiration.binding_wand.bound", "Injection Gun linked to the Matter Binder at (%s, %s, %s)");
        add("message.inspiration.binding_wand.unbound", "Injection Gun unlinked");
        add("message.inspiration.binding_wand.not_bound", "This Injection Gun is not linked to any Matter Binder");
        add("message.inspiration.binding_wand.no_selection", "Right-click a Matter Binder with the wand first");
        add("message.inspiration.binding_wand.out_of_range", "Outside the %s area around the Matter Binder");
        add("message.inspiration.binding_wand.target_lost", "The selected Matter Binder no longer exists");

        // 翡翠钢升级模板（文案结构对齐下界合金升级模板）
        add("upgrade.inspiration.jade_steel_upgrade", "Jade Steel Upgrade");
        add("item.inspiration.energized_jade_steel_upgrade.applies_to",
                "Jade Steel Tools and Netherite Armor");
        add("item.inspiration.energized_jade_steel_upgrade.ingredients", "Energized Jade Steel");
        add("item.inspiration.energized_jade_steel_upgrade.base_slot_description", "Add a jade steel tool or a piece of netherite armor");
        add("item.inspiration.energized_jade_steel_upgrade.additions_slot_description", "Add Energized Jade Steel");
        add("upgrade.inspiration.energized_jade_steel_upgrade", "Energized Jade Steel Upgrade");
        add("item.inspiration.jade_steel_upgrade.applies_to", "Inspiration Sword, Axe, Pickaxe, Shovel and Hoe");
        add("item.inspiration.jade_steel_upgrade.ingredients", "Jade Steel Ingot");
        add("item.inspiration.jade_steel_upgrade.base_slot_description", "Add an inspiration tool");
        add("item.inspiration.jade_steel_upgrade.additions_slot_description", "Add a Jade Steel Ingot");

        // 机器插件
        add("tooltip.inspiration.plugin.slot", "Insert into a plugin slot on the right of a machine");
        add("tooltip.inspiration.plugin.energy_capacity", "Machine energy capacity x%1$s");
        add("tooltip.inspiration.plugin.charge_capacity", "Machine inspiration charge capacity x%1$s");
        add("tooltip.inspiration.plugin.speed", "Speed x%1$s, energy per tick x%2$s, charge per tick x%1$s");
        add("tooltip.inspiration.plugin.energy_capacity_downgrade", "Machine energy capacity x0.67");
        add(ModItems.PARALLEL_PLUGIN.get(), "Parallel Plugin");
        add("tooltip.inspiration.plugin.parallel", "Batch capacity x%1$s. Power and charge scale with the number of recipes run.");
        add("tooltip.inspiration.plugin.byproduct", "Refiner byproduct output +100%");
        add("tooltip.inspiration.plugin.stack", "Same-type plugins stack, each x%s");

        // 辐射机制
        add("tooltip.inspiration.radiation", "Radiation: %s /s");
        add("tooltip.inspiration.radiation.period", "Radiation: %1$s per %2$s seconds");
        // 碱性值（碱性发电机的燃料强度）
        add("tooltip.inspiration.alkalinity", "Alkalinity: %s");

        // 机器 tooltip：加工类写配方类型，发电类写发电机制；一个键可写多行，用 \n 分隔
        add("tooltip.inspiration.machine.matter_fusion_machine",
                "Recipe type: Matter Fusion");
        add("tooltip.inspiration.machine.energized_matter_fusion_machine", "Recipe: Matter Fusion\nThird input slot unlocks 3-material recipes; 2x speed.");
        add("tooltip.inspiration.machine.matter_reshaper", "Recipe: Matter Reshaping\nAlso runs right-click conversion recipes.");
        add("tooltip.inspiration.machine.mineral_twister",
                "Recipe type: Mineral Twisting");
        add("tooltip.inspiration.machine.inspiration_assembler",
                "Recipe type: Inspiration Assembling");
        add("tooltip.inspiration.machine.core_driller", "One run drains all energy and drill durability, digging down until an unbreakable block.\nCan mine Reinforced Deepslate.");
        add("tooltip.inspiration.machine.high_energy_matter_reshaper",
                "Recipe type: Matter Reshaping\nAlso performs right-click conversion recipes; 2x speed.");
        add("tooltip.inspiration.machine.high_energy_mineral_twister",
                "Recipe type: Mineral Twisting\nOutput equals Fortune 12; 2x speed.");
        add("tooltip.inspiration.machine.high_energy_inspiration_assembler",
                "Recipe type: Inspiration Assembling\nSix input slots and two fluid tanks unlock bigger recipes; 2x speed.");
        add("tooltip.inspiration.machine.combustion_generator", "Burns furnace fuel for 40 FE/t. Place beside a machine to power it.");
        add("tooltip.inspiration.machine.alkaline_reactor", "Burns alkaline items: 100x FE/tick, 12000 ticks.\nx is alkalinity.");
        add("tooltip.inspiration.machine.rtg_generator", "Burns radioactive items: 1000x FE/tick, 24000 ticks.\nLeaks (x-1)^3 radiation per minute; if x>9 it may explode with (x-9)/1000 chance. Yields x Nuclear Waste.");
        add("tooltip.inspiration.machine.fluid_extractor", "Recipe: Fluid Extracting\nMelts materials, one ingot = 144 mB, stored in the output tank.");
        add("tooltip.inspiration.machine.raw_refiner", "Recipe: Raw Refining\nOne input, three outputs; the last two are byproducts, doubled by a Recycling Upgrade.");
        add("tooltip.inspiration.machine.culture_tank", "Feeds brains with nutrient and power: villager brain 1 Inspiration / 10 s, enderman brain 1 Mind Crystal / 20 s.");
        add("tooltip.inspiration.machine.inspiration_furnace", "Runs furnace, blast furnace and smoker recipes in 5 s.\\nCosts 10 FE/tick + 1 mB/tick inspiration charge; no experience.");
        add("tooltip.inspiration.machine.energized_inspiration_furnace", "Same as the Inspiration Furnace; 2x speed, 2x power and charge.");
        add("tooltip.inspiration.machine.void_condenser", "Condenses cluster energy into Void Dew: 16 ve + 500 FE/tick, 1 Dew per 1024 ve.\nNeeds a relay, or the cluster in its own block.");
        add("jei.inspiration.resonance.cost", "%s FE/t and %s VE/t");
        add("jei.inspiration.resonance.failure", "Losing VE destroys the item in progress.");
        add("tooltip.inspiration.machine.resonance_chamber", "Uses FE and 2 VE per tick. Losing VE destroys the item in progress.");
        add("item.inspiration.energized_jade_steel_helmet", "Energized Jade Steel Helmet");
        add("item.inspiration.energized_jade_steel_chestplate", "Energized Jade Steel Chestplate");
        add("item.inspiration.energized_jade_steel_leggings", "Energized Jade Steel Leggings");
        add("item.inspiration.energized_jade_steel_boots", "Energized Jade Steel Boots");
        add("item.inspiration.energized_golden_apple", "Energized Golden Apple");
        add("item.inspiration.energized_enchanted_golden_apple", "Energized Enchanted Golden Apple");
        add("effect.inspiration.radiation_resistance", "Radiation Resistance");
        add("tooltip.inspiration.radiation_armor", "Reduces incoming body radiation by %s%% per piece");
        add("tooltip.inspiration.radiation_apple", "Removes %s body radiation and grants %s minutes of Radiation Resistance");
        add("tooltip.inspiration.machine.radiation_absorber", "Uses 400 FE/t to remove 1 radiation from this chunk every 60 seconds. Produces 1 Nuclear Waste per 64 points removed.");
        add("jei.inspiration.absorber.rate", "Removes 1 chunk radiation every %s s");
        add("jei.inspiration.absorber.waste", "Produces 1 Nuclear Waste per %s points");
        add("tooltip.inspiration.machine.void_energy_collector", "Draws up to 16 VE/t from each cluster in a 5×5×5 area into the cluster directly above.");
        add("tooltip.inspiration.machine.zero_point_generator", "Produces 1 + n*1.2G FE per minute (up to 64 amplifiers), buffer 128 GFE.\nIt only pushes power out; a full buffer simply stops accumulating.");
        add("tooltip.inspiration.machine.void_energy_extractor", "Standing on cracked bedrock, feeds 1 ve/tick into the block above.\nCosts 50 FE/tick; no power, no output.");
        add("tooltip.inspiration.machine.void_energy_relay", "Must be placed in the cluster's own block; machines within 5x5x5 can draw from it.");
        add("tooltip.inspiration.machine.void_injection_gun", "Void injection: spends only cluster energy (10% of it), never FE.\nBelow the recipe minimum nothing happens; above the maximum it overloads.");
        add("tooltip.inspiration.machine.mineral_simulator", "3x5x3 multiblock: rolls ores by the weight table of the dimension data in the data hatch.\\nThe data is not consumed; it only draws power from the energy port.");
        add("tooltip.inspiration.machine.high_energy_matter_fusion_machine", "Recipe: Matter Fusion (four materials).\\nFour input slots; every stat is doubled again over the energized machine.");
        add("tooltip.inspiration.machine.high_energy_inspiration_furnace", "Same as the Inspiration Furnace; 4x speed, 4x power and charge.");
        add("tooltip.inspiration.machine.structure_simulator", "Simulates loot from structure data; costs 8 mB/tick inspiration charge.\n4x3x3 hollow casing shell with all five hatches.");
        add("tooltip.inspiration.brain.villager", "Culture Tank: 100 FE/tick + 1 mB/tick nutrient, 1 Inspiration per 10 s.\\nDies when nutrient runs out.");
        add("tooltip.inspiration.brain.enderman", "Culture Tank: 1000 FE/tick + 10 mB/tick nutrient, 1 Mind Crystal per 20 s.\\nNever dies.");
        add("tooltip.inspiration.brain.inactive", "Recycle in a Raw Refiner for 2 Parasites.");
        add("tooltip.inspiration.parasite", "Right-click a villager to infest it; killing it most likely drops a Villager Brain.");
        add("tooltip.inspiration.mechanical_parasite", "Right-click an enderman or villager to infest it.\\nKilling an infested enderman drops an Enderman Brain; a villager returns the parasite.");
        add("gui.inspiration.zero_point_generator.status", "Power %s / %s GFE ・ Amplifiers %s/%s");
        add("gui.inspiration.inspiration_value", "Inspiration");
        add("gui.inspiration.inspiration_value.tip", "Inspiration: %s / %s");
        add("gui.inspiration.inspiration_value.extract", "Click to extract 100 -> 1 Inspiration");
        // ===================== Structure Simulator =====================
        add("tooltip.inspiration.dimension_data.duration", "Duration %s ticks");
        add("tooltip.inspiration.dimension_data.energy", "%s FE per tick");
        add("tooltip.inspiration.dimension_data.ores", "%s ore types");
        add("tooltip.inspiration.dimension_data.unconsumed", "Not consumed");
        add("tooltip.inspiration.structure_data.duration", "Duration: %s ticks");
        add("tooltip.inspiration.structure_data.energy", "Power: %s FE/tick");
        add("tooltip.inspiration.structure_data.charge", "Inspiration charge: %s mB/tick");
        add("tooltip.inspiration.structure_data.unconsumed", "Runs from the data hatch without being consumed");
        // 结构投影仪
        add("tooltip.inspiration.projector.usage", "Right-click the Structure Simulator controller to project the full structure.");
        add("tooltip.inspiration.projector.duration", "The ghost stays for 30 seconds; the projector is not consumed.");
        add("jei.inspiration.structure_preview", "Multiblock Assembly");
        add("jei.inspiration.preview.title", "%s  %s×%s×%s");
        add("jei.inspiration.preview.all", "All floors");
        add("jei.inspiration.preview.floor", "Floor %s / %s");
        add("jei.inspiration.preview.hint", "Drag to rotate · Example build");
        add("jei.inspiration.preview.parts", "Example build: %s × %s");
        add("jei.inspiration.sim.retained", "Data hatch · Reusable");
        add("jei.inspiration.sim.time_power", "%s ticks · %s FE/t");
        add("jei.inspiration.sim.total_fe", "Energy per cycle: %s FE");
        add("jei.inspiration.sim.total_charge", "Charge per cycle: %s mB");
        add("jei.inspiration.sim.charge", "Inspiration charge: %s mB/t");
        add("jei.inspiration.sim.loot", "Roll one loot table per cycle");
        add("jei.inspiration.sim.table_chance", "%s  ·  %s/%s");
        add("jei.inspiration.loot.minecraft.chests.shipwreck_supply", "Supply chest");
        add("jei.inspiration.loot.minecraft.chests.shipwreck_treasure", "Treasure chest");
        add("jei.inspiration.loot.minecraft.chests.shipwreck_map", "Map chest");
        add("jei.inspiration.sim.one_ore", "One of these stacks per cycle");
        add("jei.inspiration.sim.weight", "Draw weight: %s / %s");
        // 主方块界面上的运行状态（顺序即优先级，见 SimulatorStatus）
        add("gui.inspiration.structure_simulator.status.not_formed", "Structure not formed");
        add("gui.inspiration.structure_simulator.status.no_data", "No structure data in the data hatch");
        add("gui.inspiration.structure_simulator.status.no_energy", "Not enough power");
        add("gui.inspiration.structure_simulator.status.no_charge", "Not enough inspiration charge");
        add("gui.inspiration.structure_simulator.status.output_full", "Output hatch is full");
        add("gui.inspiration.structure_simulator.status.working", "Simulating");
        add("tooltip.inspiration.drill_bit.description", "Consumable for the Core Driller: each operation costs 1 durability");
        add("item.inspiration.geiger_counter.desc", "Right-click while holding to measure this chunk");
        add("item.inspiration.geiger_counter.reading", "Chunk %s ・ Body %s (%s)");
        add("item.inspiration.world_data_detector.desc", "Creative only. Right-click to read this world's seeded injection windows");
        add("item.inspiration.world_data_detector.header", "Seed-randomised injection energy (world seed %s):");
        add("item.inspiration.world_data_detector.line", "%s: %s");
        add("item.inspiration.world_data_detector.empty", "This world has no seed-randomised injection recipe.");
        // ===================== Guide book =====================
        add("container.inspiration.guide_book", "Inspiration Guide");
        add("guide.inspiration.chapter.inspiration", "Inspiration");
        add("guide.inspiration.chapter.inspiration.body",
                "Inspiration is the player's own will made solid\n"
                        + "a thought that refuses to stay a thought,\n"
                        + "condensed into something you can hold.\n\n"
                        + "It is both a crafting material and machine fuel -\n"
                        + "without it, a machine with power still will not run.\n\n"
                        + "Crafting, eating and gaining XP build up Inspiration;\n"
                        + "100 points can be exchanged for one in the inventory screen.");
        add("guide.inspiration.chapter.machines", "The Machines");
        add("guide.inspiration.chapter.machines.body",
                "Matter Fusion Machine: fuses materials into one.\n"
                        + "Energized Fusion Machine: the upgraded fusion machine.\n"
                        + "Matter Reshaper: reshapes one material into another.\n"
                        + "Mineral Twister: twists ore, yields are random.\n"
                        + "Inspiration Assembler: assembles parts into a product.\n"
                        + "Core Driller: sinks a shaft straight down.\n\n"
                        + "Every machine only consumes power, never produces it,\n"
                        + "and most also drink inspiration charge.");
        add("guide.inspiration.chapter.energy", "Power and Charge");
        add("guide.inspiration.chapter.energy.body",
                "Burn furnace fuel in the basic generator for 40 FE/t.\n"
                        + "Place a machine next to it.\n"
                        + "Coal supplies 64,000 FE.\n"
                        + "Feed its charge slot too:\n"
                        + "Inspiration = 1,000 mB.\n"
                        + "A shard = 100 mB.");
        add("guide.inspiration.chapter.plugins", "Plugins");
        add("guide.inspiration.chapter.plugins.body",
                "Four slots. Effects stack.\n"
                        + "Capacitor stores FE. Inspiration stores charge. Speed raises both costs.\n"
                        + "Basic x2. High-energy x4.\n"
                        + "Parallel processes up to twice as many recipes per slot.\n"
                        + "Capacitor Downgrade: x0.67.");
        add("guide.inspiration.chapter.injection", "Injection");
        add("guide.inspiration.chapter.injection.body",
                "Set a tier base under the Matter Binder, ring it\n"
                        + "with Injection Guns, and the structure is built.\n\n"
                        + "Base material goes in the binder, the injectant in the guns,\n"
                        + "and a redstone pulse fires one discharge.\n\n"
                        + "The discharge must land inside the recipe window\n"
                        + "too low and nothing happens, too high and it explodes.");
        add("guide.inspiration.chapter.radiation", "Radiation");
        add("guide.inspiration.chapter.radiation.body",
                "Francium materials leak radiation into the chunk,\n"
                        + "and it builds up inside you as well.\n\n"
                        + "Leave the salt ore in the Nether, do not linger,\n"
                        + "and carry a Geiger Counter to read both numbers.");

        add("radiation.inspiration.severity.0", "healthy");
        add("radiation.inspiration.severity.1", "mild sickness");
        add("radiation.inspiration.severity.2", "moderate sickness");
        add("radiation.inspiration.severity.3", "severe sickness");
        add("radiation.inspiration.severity.4", "acute sickness");
        add("radiation.inspiration.severity.5", "lethal sickness");

        // 物品组
        add("itemGroup.inspire_tab", "Inspiration");
        add("itemGroup.material_tab", "Materials");

        // 配置界面
        add("inspiration.configuration.title", "Inspiration Configs");
        add("inspiration.configuration.section.inspiration.common.toml", "Inspiration Configs");
        add("inspiration.configuration.section.inspiration.common.toml.title", "Inspiration Configs");
        add("inspiration.configuration.items", "Item List");
        add("inspiration.configuration.logDirtBlock", "Log Dirt Block");
        add("inspiration.configuration.magicNumberIntroduction", "Magic Number Text");
        add("inspiration.configuration.magicNumber", "Magic Number");
    }
}
