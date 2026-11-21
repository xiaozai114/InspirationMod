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
        add("block.inspiration.void_injection_gun", "Void Injection Gun");
        add("block.inspiration.void_energy_relay", "Void Energy Relay");
        add("item.inspiration.sculk_alloy_drill_bit", "Sculk Alloy Drill Bit");
        add("item.inspiration.unstable_metal", "Unstable Metal");
        add("item.inspiration.weakened_core", "Weakened Core");
        add("item.inspiration.void_plasma_ingot", "Void Plasma Ingot");
        add("item.inspiration.void_dew", "Void Dew");
        add("item.inspiration.null_point_module", "Null Point Module");
        add("item.inspiration.zero_point_amplifier", "Zero-Point Amplifier");
        add("item.inspiration.void_alloy", "Void Alloy");
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
        add(ModItems.INSPIRATION_PLUGIN.get(), "Inspiration Plugin");
        add(ModItems.SPEED_PLUGIN.get(), "Speed Plugin");
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
                "Jade Steel Sword, Axe, Pickaxe, Shovel and Hoe");
        add("item.inspiration.energized_jade_steel_upgrade.ingredients", "Energized Jade Steel");
        add("item.inspiration.energized_jade_steel_upgrade.base_slot_description", "Add a jade steel tool");
        add("item.inspiration.energized_jade_steel_upgrade.additions_slot_description", "Add Energized Jade Steel");
        add("upgrade.inspiration.energized_jade_steel_upgrade", "Energized Jade Steel Upgrade");
        add("item.inspiration.jade_steel_upgrade.applies_to", "Inspiration Sword, Axe, Pickaxe, Shovel and Hoe");
        add("item.inspiration.jade_steel_upgrade.ingredients", "Jade Steel Ingot");
        add("item.inspiration.jade_steel_upgrade.base_slot_description", "Add an inspiration tool");
        add("item.inspiration.jade_steel_upgrade.additions_slot_description", "Add a Jade Steel Ingot");

        // 机器插件
        add("tooltip.inspiration.plugin.slot", "Insert into a plugin slot on the right of a machine");
        add("tooltip.inspiration.plugin.energy_capacity", "Machine energy capacity x2");
        add("tooltip.inspiration.plugin.charge_capacity", "Machine inspiration charge capacity x2");
        add("tooltip.inspiration.plugin.speed", "Speed x2, energy per tick x4, charge per tick x2");
        add("tooltip.inspiration.plugin.energy_capacity_downgrade", "Machine energy capacity x0.67");
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
        add("tooltip.inspiration.machine.alkaline_reactor", "Burns alkaline items: 100x FE/tick, 12000 ticks.\nx is alkalinity.");
        add("tooltip.inspiration.machine.rtg_generator", "Burns radioactive items: 1000x FE/tick, 24000 ticks.\nLeaks (x-1)^3 radiation per minute; if x>9 it may explode with (x-9)/1000 chance. Yields x Nuclear Waste.");
        add("tooltip.inspiration.machine.fluid_extractor", "Recipe: Fluid Extracting\nMelts materials, one ingot = 144 mB, stored in the output tank.");
        add("tooltip.inspiration.machine.raw_refiner", "Recipe: Raw Refining\nOne input, three outputs; the last two are byproducts, doubled by a Recycling Upgrade.");
        add("tooltip.inspiration.machine.culture_tank", "Feeds brains with nutrient and power: villager brain 1 Inspiration / 10 s, enderman brain 1 Mind Crystal / 20 s.");
        add("tooltip.inspiration.machine.inspiration_furnace", "Runs furnace, blast furnace and smoker recipes in 5 s.\\nCosts 10 FE/tick + 1 mB/tick inspiration charge; no experience.");
        add("tooltip.inspiration.machine.energized_inspiration_furnace", "Same as the Inspiration Furnace; 2x speed, 2x power and charge.");
        add("tooltip.inspiration.machine.high_energy_inspiration_furnace", "Same as the Inspiration Furnace; 4x speed, 4x power and charge.");
        add("tooltip.inspiration.machine.structure_simulator", "Simulates loot from structure data; costs 8 mB/tick inspiration charge.\n4x3x3 hollow casing shell with all five hatches.");
        add("tooltip.inspiration.brain.villager", "Culture Tank: 100 FE/tick + 1 mB/tick nutrient, 1 Inspiration per 10 s.\\nDies when nutrient runs out.");
        add("tooltip.inspiration.brain.enderman", "Culture Tank: 1000 FE/tick + 10 mB/tick nutrient, 1 Mind Crystal per 20 s.\\nNever dies.");
        add("tooltip.inspiration.brain.inactive", "Recycle in a Raw Refiner for 2 Parasites.");
        add("tooltip.inspiration.parasite", "Right-click a villager to infest it; killing it most likely drops a Villager Brain.");
        add("tooltip.inspiration.mechanical_parasite", "Right-click an enderman or villager to infest it.\\nKilling an infested enderman drops an Enderman Brain; a villager returns the parasite.");
        add("gui.inspiration.inspiration_value", "Inspiration");
        add("gui.inspiration.inspiration_value.tip", "Inspiration: %s / %s");
        add("gui.inspiration.inspiration_value.extract", "Click to extract 100 -> 1 Inspiration");
        // ===================== Structure Simulator =====================
        add("tooltip.inspiration.structure_data.duration", "Duration: %s ticks");
        add("tooltip.inspiration.structure_data.energy", "Power: %s FE/tick");
        add("tooltip.inspiration.structure_data.charge", "Inspiration charge: %s mB/tick");
        add("tooltip.inspiration.structure_data.unconsumed", "Runs from the data hatch without being consumed");
        // 结构投影仪
        add("tooltip.inspiration.projector.usage", "Right-click the Structure Simulator controller to project the full structure.");
        add("tooltip.inspiration.projector.duration", "The ghost stays for 30 seconds; the projector is not consumed.");
        // JEI 里的结构预览：每层一页
        add("jei.inspiration.structure_preview", "Structure Simulator Structure");
        add("jei.inspiration.structure_preview.layer", "Layer %s/%s · %s");
        add("jei.inspiration.structure_preview.layer.0", "Back");
        add("jei.inspiration.structure_preview.layer.1", "Middle");
        add("jei.inspiration.structure_preview.layer.2", "Front");
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
                "The energy bar on a machine only ever goes down,\n"
                        + "so power has to be fed in from outside.\n\n"
                        + "Inspiration charge lives in its own tank\n"
                        + "1 inspiration = 1000 mB, 1 shard = 100 mB.\n"
                        + "Run dry and progress halts,\n"
                        + "but nothing already inserted is lost.");
        add("guide.inspiration.chapter.plugins", "Plugins");
        add("guide.inspiration.chapter.plugins.body",
                "Each machine has four plugin slots on the right.\n\n"
                        + "Speed Plugin: twice the speed, more power and charge.\n"
                        + "Capacity Plugin: raises the energy buffer.\n"
                        + "Capacitor Downgrade: caps the buffer at 67%.\n"
                        + "Not a downgrade - it shrinks a single discharge.");
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