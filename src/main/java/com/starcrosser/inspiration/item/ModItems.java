package com.starcrosser.inspiration.item;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.fluid.ModFluids;
import com.starcrosser.inspiration.injection.item.BindingWandItem;
import com.starcrosser.inspiration.item.custom.*;
import com.starcrosser.inspiration.radiation.item.GeigerCounterItem;
import com.starcrosser.inspiration.item.tool.ModToolTiers;
import com.starcrosser.inspiration.item.tool.ToolDurability;
import com.starcrosser.inspiration.machine.plugin.MachinePluginType;
import com.starcrosser.inspiration.multiblock.data.SimulationSpec;
import com.starcrosser.inspiration.multiblock.data.StructureDataItem;
import com.starcrosser.inspiration.multiblock.item.ProjectorItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

import java.util.List;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Inspiration.MODID);
    public static final DeferredItem<Item> INSPIRATION=
            ITEMS.register("inspiration", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> CESIUM_SALT=
            ITEMS.register("cesium_salt", () ->new Item(new Item.Properties()));

    public static final DeferredItem<Item> CALCIUM_HYDROXIDE_DUST=
            ITEMS.register("calcium_hydroxide_dust", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> CALCIUM_DUST=
            ITEMS.register("calcium_dust", () ->new AlkaliItem(new Item.Properties(),ModItems.CALCIUM_HYDROXIDE_DUST.get(),0.1f));
    public static final DeferredItem<Item> CESIUM_DUST=
            ITEMS.register("cesium_dust", () ->new AlkaliItem(new Item.Properties(),ModItems.CESIUM_SALT.get(),4f));
    // 铯锭改由 material/ModMaterials 声明（一次性生成锭、块、data_maps 与配套资源）

    public static final DeferredItem<Item> RAW_CESIUM_SALT=
            ITEMS.register("raw_cesium_salt", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> COOKED_INSPIRATION=
            ITEMS.register("cooked_inspiration", () ->new Item(new Item.Properties().food(ModFoods.COOKED_INSPIRATION)));
    public static final DeferredItem<Item> LIGNITE=
            ITEMS.register("lignite", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> EMPTY_BEACON=
            ITEMS.register("empty_beacon", () ->new EmptyBeacon(new Item.Properties()));
    public static final DeferredItem<Item> POWER_BEACON=
            ITEMS.register("power_beacon", () ->new PowerBeacon(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> COKE=
            ITEMS.register("coke", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> UNSTABLE_SMITHING_TEMPLATE=
            ITEMS.register("unstable_smithing_template", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> INSPIREAD=
            ITEMS.register("inspiread", () ->new Item(new Item.Properties().food(ModFoods.INSPIREAD)));
    public static final DeferredItem<Item> UNSTABLE_CORE=
            ITEMS.register("unstable_core", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> EPHEMERAL_MATERIAL=
            ITEMS.register("ephemeral_material", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> SOD=
            ITEMS.register("sod", () ->new Item(new Item.Properties().food(ModFoods.SOD)));
    public static final DeferredItem<Item> INSPIRRY=
            ITEMS.register("inspirry", () ->new Item(new Item.Properties().food(ModFoods.INSPIRRY)));
    public static final DeferredItem<Item> EXPLODING_CANDY=
            ITEMS.register("exploding_candy", () ->new ExplodingCandy(new Item.Properties().food(ModFoods.EXPLODING_CANDY)));
    public static final DeferredItem<Item> EXP_SWORD=
            ITEMS.register("exp_sword", () ->new ExperienceSword(ModToolTiers.withDurability(Tiers.IRON, ToolDurability.EXP_SWORD), 3, -2.2F, 5,
                    new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredItem<Item> STORM_SWORD=
            ITEMS.register("storm_sword",()->new StormSword(ModToolTiers.withDurability(Tiers.DIAMOND, ToolDurability.STORM_SWORD), 4, -2.2F, 5, 60,
                    new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> STICKY_AXE=
        ITEMS.register("sticky_axe", () ->new StickyAxe(ModToolTiers.withDurability(Tiers.IRON, ToolDurability.STICKY_AXE), 7, -3.2F, 1,
                    new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredItem<Item> HEAD_HUNTER_AXE=
            ITEMS.register("head_hunter_axe", () ->new HeadHunterAxe(ModToolTiers.withDurability(Tiers.DIAMOND, ToolDurability.HEAD_HUNTER_AXE), 9, -3.2F,
                    new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> EXP_PICKAXE=
            ITEMS.register("exp_pickaxe", () ->new ExperiencePickaxe(ModToolTiers.withDurability(Tiers.DIAMOND, ToolDurability.EXP_PICKAXE), 0, -2.4F, 1,
                    new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredItem<Item> WORLD_BREAKER=
            ITEMS.register("world_breaker", () ->new BedrockPickaxe(ModToolTiers.withDurability(Tiers.NETHERITE, ToolDurability.WORLD_BREAKER), 0, -2.0F,
                    new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> EXP_HOE=
            ITEMS.register("exp_hoe", () ->new ExperienceHoe(ModToolTiers.withDurability(Tiers.IRON, ToolDurability.EXP_HOE), -2.0F, 0.0F, 1,
                    new Item.Properties().rarity(Rarity.RARE)));
    /** 灵感之铲：把草方块压成土径时掉草皮与经验，是本系列第五件工具。 */
    public static final DeferredItem<Item> EXP_SHOVEL=
            ITEMS.register("exp_shovel", () ->new ExperienceShovel(ModToolTiers.withDurability(Tiers.IRON, ToolDurability.EXP_SHOVEL), 1.0F, -3.0F, 1,
                    new Item.Properties().rarity(Rarity.RARE)));

    /* ===================== 翡翠钢系列：灵感工具的升级版 =====================
     * 用翡翠钢升级模板在锻造台上升级得到，数值见 ModToolTiers.JADE_STEEL。
     * 参数依次是：品质、攻击力加成、攻击/使用速度、每次动作的经验产出。
     * 品质要用 ModToolTiers#withDurability 包一层来指定本件装备的耐久
     * —— 直接写 Item.Properties#durability 会被 TieredItem 用品质耐久覆盖掉。
     */
    /** 翡翠钢剑：攻速高于下界合金剑，单次伤害略低。 */
    public static final DeferredItem<Item> JADE_STEEL_SWORD=
            ITEMS.register("jade_steel_sword", () ->new ExperienceSword(ModToolTiers.withDurability(ModToolTiers.JADE_STEEL, ToolDurability.JADE_STEEL_SWORD),
                    3, -2.0F, 12, new Item.Properties().rarity(Rarity.RARE)));
    /** 翡翠钢斧：伤害高于下界合金斧，攻速略低。 */
    public static final DeferredItem<Item> JADE_STEEL_AXE=
            ITEMS.register("jade_steel_axe", () ->new StickyAxe(ModToolTiers.withDurability(ModToolTiers.JADE_STEEL, ToolDurability.JADE_STEEL_AXE),
                    7.0F, -3.2F, 3, new Item.Properties().rarity(Rarity.RARE)));
    /** 翡翠钢镐：挖掘速度高于下界合金镐，攻击伤害略低。 */
    public static final DeferredItem<Item> JADE_STEEL_PICKAXE=
            ITEMS.register("jade_steel_pickaxe", () ->new ExperiencePickaxe(ModToolTiers.withDurability(ModToolTiers.JADE_STEEL, ToolDurability.JADE_STEEL_PICKAXE),
                    1.0F, -2.4F, 3, new Item.Properties().rarity(Rarity.RARE)));
    /** 翡翠钢锄：耐久高于下界合金锄，其余数值持平。 */
    public static final DeferredItem<Item> JADE_STEEL_HOE=
            ITEMS.register("jade_steel_hoe", () ->new ExperienceHoe(ModToolTiers.withDurability(ModToolTiers.JADE_STEEL, ToolDurability.JADE_STEEL_HOE),
                    -3.5F, 0.0F, 3, new Item.Properties().rarity(Rarity.RARE)));
    /** 翡翠钢铲：与灵感之铲同行为（压土径掉草皮），数值按翡翠钢档。 */
    public static final DeferredItem<Item> JADE_STEEL_SHOVEL=
            ITEMS.register("jade_steel_shovel", () ->new ExperienceShovel(ModToolTiers.withDurability(ModToolTiers.JADE_STEEL, ToolDurability.JADE_STEEL_SHOVEL),
                    2.0F, -3.0F, 3, new Item.Properties().rarity(Rarity.RARE)));

    /* ===================== 注能翡翠钢系列：翡翠钢工具的再升级 =====================
     * 用注能翡翠钢升级模板在锻造台上升级得到。数值比翡翠钢再高一档，
     * 并且都能「长按右键蓄力 1.5 秒、消耗 20 点经验」换取 30 秒的增益：
     * 剑=速度、镐=急迫、斧=力量、锄=幸运、铲=跳跃提升。
     * 经验产出沿用上一档的工具类，数值再抬高（剑 18、其余 5）。
     */
    public static final DeferredItem<Item> ENERGIZED_JADE_STEEL_SWORD=
            ITEMS.register("energized_jade_steel_sword", () ->new EnergizedJadeSteelSword(
                    ModToolTiers.withDurability(ModToolTiers.ENERGIZED_JADE_STEEL, ToolDurability.ENERGIZED_JADE_STEEL_SWORD), 4, -1.9F, 18,
                    new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> ENERGIZED_JADE_STEEL_AXE=
            ITEMS.register("energized_jade_steel_axe", () ->new EnergizedJadeSteelAxe(
                    ModToolTiers.withDurability(ModToolTiers.ENERGIZED_JADE_STEEL, ToolDurability.ENERGIZED_JADE_STEEL_AXE), 8.0F, -3.0F, 5,
                    new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> ENERGIZED_JADE_STEEL_PICKAXE=
            ITEMS.register("energized_jade_steel_pickaxe", () ->new EnergizedJadeSteelPickaxe(
                    ModToolTiers.withDurability(ModToolTiers.ENERGIZED_JADE_STEEL, ToolDurability.ENERGIZED_JADE_STEEL_PICKAXE), 2.0F, -2.2F, 5,
                    new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> ENERGIZED_JADE_STEEL_HOE=
            ITEMS.register("energized_jade_steel_hoe", () ->new EnergizedJadeSteelHoe(
                    ModToolTiers.withDurability(ModToolTiers.ENERGIZED_JADE_STEEL, ToolDurability.ENERGIZED_JADE_STEEL_HOE), -2.5F, 0.0F, 5,
                    new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> ENERGIZED_JADE_STEEL_SHOVEL=
            ITEMS.register("energized_jade_steel_shovel", () ->new EnergizedJadeSteelShovel(
                    ModToolTiers.withDurability(ModToolTiers.ENERGIZED_JADE_STEEL, ToolDurability.ENERGIZED_JADE_STEEL_SHOVEL), 3.0F, -2.8F, 5,
                    new Item.Properties().rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> REDUCTION_POWDER=
            ITEMS.register("reduction_powder", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> REINFORCED_CORE=
            ITEMS.register("reinforced_core", () ->new Item(new Item.Properties()));
    /** 强能核心：强化核心的上级产物，用于合成物质重塑机。 */
    public static final DeferredItem<Item> POWERFUL_CORE=
            ITEMS.register("powerful_core", () ->new Item(new Item.Properties()));
    /** 高能核心：强能核心 + 钫粉 + 强化深板岩在装配台合成，更高一档的能量部件。 */
    public static final DeferredItem<Item> HIGH_ENERGY_CORE=
            ITEMS.register("high_energy_core", () ->new Item(new Item.Properties().rarity(Rarity.RARE)));
    /** 发电核心：强能核心经物质重塑得到，炉心变绿，是碱性发电机的核心部件。 */
    public static final DeferredItem<Item> GENERATION_CORE=
            ITEMS.register("generation_core", () ->new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    /** 反应核心：高能核心经物质重塑得到，炉心变红，是 RTG反应堆的核心部件。 */
    public static final DeferredItem<Item> REACTION_CORE=
            ITEMS.register("reaction_core", () ->new Item(new Item.Properties().rarity(Rarity.RARE)));
    /** 核废料：RTG反应堆烧完一份放射性物质后的产物，按燃料的辐射值等量产出，目前暂无用途。 */
    public static final DeferredItem<Item> NUCLEAR_WASTE=
            ITEMS.register("nuclear_waste", () ->new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    /* ==================== 结构模拟机的结构数据 ==================== */

    /** 沉船模拟的持续时间（刻）。 */
    private static final int SHIPWRECK_DURATION = 12000;
    /** 沉船模拟每刻消耗的 FE。 */
    private static final int SHIPWRECK_ENERGY_PER_TICK = 1000;
    /** 沉船的三种箱子，产出时随机取一张 roll。 */
    private static final List<ResourceLocation> SHIPWRECK_LOOT_TABLES = List.of(
            ResourceLocation.withDefaultNamespace("chests/shipwreck_supply"),
            ResourceLocation.withDefaultNamespace("chests/shipwreck_treasure"),
            ResourceLocation.withDefaultNamespace("chests/shipwreck_map"));

    /** 空白数据：结构数据的底材，本身不带模拟参数，装配成具体结构数据后才能用。 */
    public static final DeferredItem<Item> BLANK_DATA=
            ITEMS.register("blank_data", () ->new Item(new Item.Properties()));
    /**
     * 沉船数据：模拟一艘沉船，12000 刻、每刻 1000 FE，
     * 产出是沉船三种箱子（补给 / 宝藏 / 地图）里随机一种的内容。
     */
    public static final DeferredItem<Item> SHIPWRECK_DATA=
            ITEMS.register("shipwreck_data", () ->new StructureDataItem(
                    new Item.Properties().rarity(Rarity.UNCOMMON), new SimulationSpec(
                            SHIPWRECK_DURATION, SHIPWRECK_ENERGY_PER_TICK, SHIPWRECK_LOOT_TABLES)));

    /**
     * 结构投影仪：手持右键结构模拟机的主方块，把 4×3×3 的结构以虚影的形式投在原地，
     * 方便照着搭。由「铁锭注入思维矩阵锭」得到。
     */
    public static final DeferredItem<Item> PROJECTOR =
            ITEMS.register("projector", () -> new ProjectorItem(
                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    /** 翡翠钢升级模板：在锻造台上把灵感系列工具升成翡翠钢版本。 */
    public static final DeferredItem<Item> JADE_STEEL_UPGRADE_TEMPLATE=
            ITEMS.register("jade_steel_upgrade_template",
                    () ->JadeSteelUpgradeTemplate.create());
    /** 玄武岩合金打火石：无法破坏的打火石。 */
    public static final DeferredItem<Item> BASALT_ALLOY_FIRESTRIKER=
            ITEMS.register("basalt_alloy_firestriker",
                    () ->new BasaltAlloyFirestriker(new Item.Properties().durability(0).rarity(Rarity.RARE)));

    /** 玄武岩合金钻头：地核钻探者的耗材，一次作业消耗 1 点耐久。 */
    public static final DeferredItem<Item> BASALT_ALLOY_DRILL_BIT=
            ITEMS.register("basalt_alloy_drill_bit",
                    () ->new DrillBitItem(new Item.Properties().durability(128).stacksTo(1)
                            .rarity(Rarity.RARE)));

    /** 等离子锭：钻石经物质重塑得到的高阶材料。 */

    /** 灵感碎片：稀释灵感结晶的掉落物，可直接投入机器充能槽。 */
    public static final DeferredItem<InspirationShardItem> INSPIRATION_SHARD=
            ITEMS.register("inspiration_shard", () ->new InspirationShardItem(new Item.Properties()));
    /** 思维矩阵锭：思维晶体 + 紫水晶 + 下界石英在强能物质融合机里融合而成。 */
    /** 思维晶体：思维水晶岩的掉落物，目前暂无用途。 */
    public static final DeferredItem<Item> MIND_CRYSTAL=
            ITEMS.register("mind_crystal", () ->new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    /** 电容插件：机器能量缓冲容量翻倍。 */
    public static final DeferredItem<Item> CAPACITOR_PLUGIN=
            ITEMS.register("capacitor_plugin",
                    () ->new MachinePluginItem(MachinePluginType.ENERGY_CAPACITY));
    /** 灵感插件：机器灵感充能上限翻倍。 */
    public static final DeferredItem<Item> INSPIRATION_PLUGIN=
            ITEMS.register("inspiration_plugin",
                    () ->new MachinePluginItem(MachinePluginType.CHARGE_CAPACITY));
    /** 速度插件：机器运行速度翻倍，代价是耗电与耗充能同时上升。 */
    public static final DeferredItem<Item> SPEED_PLUGIN=
            ITEMS.register("speed_plugin",
                    () ->new MachinePluginItem(MachinePluginType.SPEED));
    /** 电容降级插件：机器能量缓冲上限降到 67%，用来压低一次注入的放电量。 */
    public static final DeferredItem<Item> CAPACITOR_DOWNGRADE_PLUGIN=
            ITEMS.register("capacitor_downgrade_plugin",
                    () ->new MachinePluginItem(MachinePluginType.ENERGY_CAPACITY_DOWNGRADE));
    /** 回收升级：原料精炼机的副产物产量 +100%，多个按加算叠加。 */
    public static final DeferredItem<Item> RECYCLING_PLUGIN=
            ITEMS.register("recycling_plugin",
                    () ->new MachinePluginItem(MachinePluginType.BYPRODUCT));

    /* ===================== 钫盐系列（辐射材料） =====================
     * 处理流程与铯盐逐一对应，但全流程产物都带辐射，数值见
     * data_maps/item|block/radiation.json 与 radiation/README.md。
     * 钫的碱性略强于铯，因此碱类物品的爆炸半径取 6f（铯是 4f）。
     */
    /** 粗钫盐：钫盐矿的挖掘掉落物，也由矿物扭曲机产出。 */
    public static final DeferredItem<Item> RAW_FRANCIUM_SALT =
            ITEMS.register("raw_francium_salt", () -> new Item(new Item.Properties()));
    /** 钫盐：粗钫盐熔炼得到，与铯盐同流程。 */
    public static final DeferredItem<Item> FRANCIUM_SALT =
            ITEMS.register("francium_salt", () -> new Item(new Item.Properties()));
    /** 钫粉：碱类物品，遇水炸开并变回钫盐。 */
    public static final DeferredItem<Item> FRANCIUM_DUST =
            ITEMS.register("francium_dust", () -> new AlkaliItem(new Item.Properties(),
                    ModItems.FRANCIUM_SALT.get(), 6f));
    /** 钫锭：钫粉高炉冶炼得到。 */

    /* ===================== 注入合成相关 ===================== */

    /** 玄武岩基底：玄武岩经物质重塑得到，是注入配方的底材。 */
    public static final DeferredItem<Item> BASALT_BASE=
            ITEMS.register("basalt_base", () ->new Item(new Item.Properties()));
    /** 注能翡翠钢：向翡翠钢注入灵感的产物，目前暂无进一步用途。 */
    /** 绑定器：把注入枪与物质束缚器关联起来的工具。 */
    public static final DeferredItem<Item> BINDING_WAND=
            ITEMS.register("binding_wand", () ->new BindingWandItem(
                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    /** 盖革计数器：铁锭注入铯锭得到，手持右键读出所在区块的辐射值。 */
    public static final DeferredItem<Item> GEIGER_COUNTER=
            ITEMS.register("geiger_counter", () ->new GeigerCounterItem(
                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    /** 末地合金：4 黑曜石 + 末影珍珠 + 4 紫珀块在强能物质融合机里融合而成，下一阶段的主材。 */
    /** 无恒合金：把无恒物质注入末地合金得到；注入所需的能量窗口由存档种子决定。 */
    /** 末地合金盾牌：把末地合金注入盾牌得到，耐久是普通盾牌的 10 倍，破盾时会炸开一圈传送波。 */
    public static final DeferredItem<EndAlloyShieldItem> END_ALLOY_SHIELD=
            ITEMS.register("end_alloy_shield", () ->new EndAlloyShieldItem(new Item.Properties()
                    .durability(EndAlloyShieldItem.MAX_DAMAGE).rarity(Rarity.EPIC)));
    /* ===================== 高能阶段（无恒合金线） =====================
     * 高能机器统一以无恒合金为外壳、高能核心为中枢，材料由「注能锭」提供：
     * 向普通锭注入灵感得到注能锭，能量窗口越高，注能锭的档位越高。
     */
    /** 注能青金石锭：向青金石锭注入灵感（10k~15k FE），高能矿物扭曲机的材料。 */
    /** 注能等离子锭：向等离子锭注入灵感（1M~2M FE），高能灵感装配台的材料。 */
    /** 注能红石锭：向红石锭注入灵感（150k~300k FE），高能物质重塑机的材料。 */
    /** 幽匿合金：回响碎片 + 强化深板岩在物质融合器里融合得到 2 个，用作注入结构的 2 级底座。 */
    /** 注能翡翠钢升级模板：由翡翠钢升级模板注入注能翡翠钢得到，可把翡翠钢工具升为注能翡翠钢工具。 */
    public static final DeferredItem<Item> ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE =
            ITEMS.register("energized_jade_steel_upgrade_template", () -> JadeSteelUpgradeTemplate.createEnergized());
    /** 世界数据探测器：仅创造模式可得，右键读出本存档里种子随机配方所需的能量。 */
    public static final DeferredItem<Item> WORLD_DATA_DETECTOR=
            ITEMS.register("world_data_detector", () ->new WorldDataDetectorItem(
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    /* ===================== 指导书 ===================== */

    /** 灵感指导书：书 + 灵感合成，右键翻开，内容是各机器的入门说明。 */
    public static final DeferredItem<Item> GUIDE_BOOK=
            ITEMS.register("guide_book", () ->new GuideBookItem(
                    new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> SCYTHE_OF_DEATH=
            ITEMS.register("scythe_of_death", () ->new Scythe(ModToolTiers.withDurability(Tiers.DIAMOND, ToolDurability.SCYTHE_OF_DEATH), 3.0F, -2.8F,
                    new Item.Properties().rarity(Rarity.EPIC)));

    public static final DeferredHolder<Item, BucketItem> MIND_FLUID_BUCKET = ITEMS.register(
            "mind_fluid_bucket",
            ()->new BucketItem(ModFluids.Fluids.MIND_FLUID.get(),
                    new Item.Properties())
    );

    /** 液态等离子桶：流体提取机的产物，也是装配台的流体原料容器。 */
    public static final DeferredHolder<Item, BucketItem> MOLTEN_PLASMA_BUCKET = ITEMS.register(
            "molten_plasma_bucket",
            ()->new BucketItem(ModFluids.Fluids.MOLTEN_PLASMA.get(),
                    new Item.Properties().rarity(Rarity.UNCOMMON))
    );
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
