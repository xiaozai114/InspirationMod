// 文件：ModZhCnLangProvider.java
package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.fluid.ModFluids;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import com.starcrosser.inspiration.registry.MachineRegistry;
import com.starcrosser.inspiration.material.ModMaterials;
import com.starcrosser.inspiration.material.InspirationMaterial;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModZhCnLangProvider extends LanguageProvider {

    public ModZhCnLangProvider(PackOutput output) {
        super(output, Inspiration.MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        // 物品
        add(ModItems.INSPIRATION.get(), "灵感");
        add(ModItems.COOKED_INSPIRATION.get(), "烤灵感");
        add(ModItems.LIGNITE.get(), "褐煤");
        add(ModItems.INSPIREAD.get(), "灵感面包");
        add(ModItems.INSPIRRY.get(), "灵感浆果");
        add(ModItems.COKE.get(), "焦煤");
        add(ModItems.EXP_SWORD.get(), "灵感之剑");
        add(ModItems.STORM_SWORD.get(), "风暴军刀");
        add(ModItems.STICKY_AXE.get(), "灵感之斧");
        add(ModItems.HEAD_HUNTER_AXE.get(), "斩首者");
        add(ModItems.EXP_PICKAXE.get(), "灵感之镐");
        add(ModItems.UNSTABLE_CORE.get(), "§7不稳核心");
        add(ModItems.UNSTABLE_SMITHING_TEMPLATE.get(), "§7不稳锻造模板");
        add(ModItems.WORLD_BREAKER.get(), "§9破§a界§4者§5XX");
        add(ModItems.EPHEMERAL_MATERIAL.get(), "§5无恒物质");
        add(ModItems.SOD.get(), "草皮");
        add(ModItems.EXP_HOE.get(), "灵感之锄");
        add(ModItems.EXP_SHOVEL.get(), "灵感之铲");
        add(ModItems.SCYTHE_OF_DEATH.get(), "死神之镰");
        add(ModItems.EMPTY_BEACON.get(), "空信标");
        add(ModItems.CESIUM_SALT.get(), "铯盐");
        add(ModBlocks.CESIUM_SALT_ORE.get(), "铯盐矿");
        add(ModItems.RAW_CESIUM_SALT.get(), "粗铯盐");
        add(ModItems.EXPLODING_CANDY.get(),"跳跳糖");
        add(ModBlocks.MIND_FLUID_BLOCK.get(),"思维流体");
        add(ModItems.MIND_FLUID_BUCKET.get(),"思维流体桶");
        add(ModItems.POWER_BEACON.get(),"动力信标");
        add(ModItems.CALCIUM_HYDROXIDE_DUST.get(),"氢氧化钙粉");
        add(ModItems.CALCIUM_DUST.get(),"钙粉");
        add(ModItems.CESIUM_DUST.get(),"铯粉");
        // 材料：锭 / 方块 / 粒 / 尘，遍历清单（语言文件按 key 排序输出，这里的顺序不影响生成结果）
        for (InspirationMaterial material : ModMaterials.ALL) {
            add(material.item().get(), material.zhItem());
            if (material.hasBlock()) {
                add(material.block().get(), material.zhBlock());
            }
            if (material.hasNugget()) {
                add(material.nugget().get(), material.zhNugget());
            }
            if (material.hasGrit()) {
                add(material.grit().get(), material.zhGrit());
            }
        }
        add(ModItems.REDUCTION_POWDER.get(),"还原粉");
        add(ModItems.REINFORCED_CORE.get(),"强化核心");
        add(ModItems.POWERFUL_CORE.get(),"强能核心");
        add(ModItems.HIGH_ENERGY_CORE.get(),"高能核心");
        add(ModItems.GENERATION_CORE.get(),"发电核心");
        add(ModItems.REACTION_CORE.get(),"反应核心");
        add(ModItems.NUCLEAR_WASTE.get(),"核废料");
        add(ModItems.JADE_STEEL_UPGRADE_TEMPLATE.get(),"翡翠钢升级模板");
        add(ModItems.BASALT_ALLOY_FIRESTRIKER.get(),"玄武岩合金打火石");
        add(ModItems.BASALT_ALLOY_DRILL_BIT.get(),"玄武岩合金钻头");
        add(ModItems.CAPACITOR_PLUGIN.get(),"电容插件");
        add(ModItems.INSPIRATION_PLUGIN.get(),"灵感插件");
        add(ModItems.SPEED_PLUGIN.get(),"速度插件");
        add(ModItems.CAPACITOR_DOWNGRADE_PLUGIN.get(),"电容降级插件");
        add(ModItems.INSPIRATION_SHARD.get(),"灵感碎片");
        add(ModItems.MIND_CRYSTAL.get(),"思维晶体");
        add(ModItems.JADE_STEEL_SWORD.get(),"翡翠钢剑");
        add(ModItems.JADE_STEEL_AXE.get(),"翡翠钢斧");
        add(ModItems.JADE_STEEL_PICKAXE.get(),"翡翠钢镐");
        add(ModItems.JADE_STEEL_HOE.get(),"翡翠钢锄");
        add(ModItems.JADE_STEEL_SHOVEL.get(),"翡翠钢铲");
        add(ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get(),"注能翡翠钢升级模板");
        add(ModItems.ENERGIZED_JADE_STEEL_SWORD.get(),"注能翡翠钢剑");
        add(ModItems.ENERGIZED_JADE_STEEL_AXE.get(),"注能翡翠钢斧");
        add(ModItems.ENERGIZED_JADE_STEEL_PICKAXE.get(),"注能翡翠钢镐");
        add(ModItems.ENERGIZED_JADE_STEEL_HOE.get(),"注能翡翠钢锄");
        add(ModItems.ENERGIZED_JADE_STEEL_SHOVEL.get(),"注能翡翠钢铲");
        add(ModBlocks.DILUTED_INSPIRATION_CRYSTAL.get(),"稀释灵感结晶");
        add(ModBlocks.CONCENTRATED_INSPIRATION_CRYSTAL.get(),"浓缩灵感结晶");
        add(ModBlocks.INSPIRATION_GEL.get(),"灵感凝胶");
        add(ModBlocks.MIND_CRYSTAL_ROCK.get(),"思维水晶岩");
        add(ModBlocks.CESIUM_SALT_BLOCK.get(),"铯盐块");
        add(ModBlocks.EMPOWERED_TNT_ITEM.get(),"强能TNT");
        // 注入合成
        add(ModInjection.MATTER_BINDER.getBlock(),"物质束缚器");
        add(ModInjection.INJECTION_GUN.getBlock(),"注入枪");
        add(ModItems.BINDING_WAND.get(),"绑定器");
        add(ModItems.BASALT_BASE.get(),"玄武岩基底");
        // 辐射：钫盐系列与盖革计数器
        add(ModBlocks.FRANCIUM_SALT_ORE.get(),"钫盐矿");
        add(ModItems.RAW_FRANCIUM_SALT.get(),"粗钫盐");
        add(ModItems.FRANCIUM_SALT.get(),"钫盐");
        add(ModBlocks.FRANCIUM_SALT_BLOCK.get(),"钫盐块");
        add(ModItems.FRANCIUM_DUST.get(),"钫粉");
        add(ModItems.GEIGER_COUNTER.get(),"盖革计数器");
        add(ModItems.END_ALLOY_SHIELD.get(),"末地合金盾牌");
        add(ModItems.WORLD_DATA_DETECTOR.get(),"世界数据探测器");
        add(ModItems.GUIDE_BOOK.get(),"灵感指导书");
        // 高能阶段

        // 多方块：结构模拟机
        add(ModBlocks.HIGH_ENERGY_MACHINE_CASING.get(),"高能机器外壳");
        add(ModMultiblocks.INPUT_HATCH.getBlock(),"输入仓");
        add(ModMultiblocks.OUTPUT_HATCH.getBlock(),"输出仓");
        add(ModMultiblocks.ENERGY_PORT.getBlock(),"能量端口");
        add(ModMultiblocks.INSPIRATION_HATCH.getBlock(),"灵感提供仓");
        add(ModMultiblocks.DATA_HATCH.getBlock(),"数据仓");
        add(ModItems.BLANK_DATA.get(),"空白数据");
        add(ModItems.SHIPWRECK_DATA.get(),"沉船数据");
        add(ModItems.PROJECTOR.get(),"结构投影仪");

        // 机器：显示名写在注册处（MachineRegistry），这里只管遍历
        for (MachineRegistry.MachineEntry machine : MachineRegistry.machines()) {
            add(machine.block(), machine.zhName());
        }
        add(ModMultiblocks.STRUCTURE_SIMULATOR.getBlock(),"结构模拟机");

        // 流体
        add("fluid_type.inspiration.inspiration_charge", "灵感充能");

        // 方块
        add(ModBlocks.LIGNITE_BLOCK.get(), "褐煤块");
        add(ModBlocks.INTENSIFIED_TNT.get(), "强化TNT");
        add(ModBlocks.EPHEMERAL_ORE.get(), "§5无恒矿石");


        // JEI 配方分类
        add("jei.inspiration.fusion", "物质融合");
        add("jei.inspiration.reshaping", "物质重塑");
        add("jei.inspiration.right_click_block", "右键转化");
        add("jei.inspiration.right_click_block.hint", "手持物品右键点击方块");
        add("jei.inspiration.right_click_block.consume", "每次消耗 %s 个手持物品");
        add("jei.inspiration.twisting", "矿物扭曲");
        add("jei.inspiration.twisting.fortune", "等效时运 %s：产量 %s~%s，期望 %s");
        add("jei.inspiration.assembling", "灵感装配");
        add("jei.inspiration.assembling.fluid", "流体原料：%s mB %s");
        // 单方块机器共用的两行：基准耗时与基准每刻耗电
        add("jei.inspiration.machine.duration", "耗时：%s 刻");
        add("jei.inspiration.machine.energy_per_tick", "每刻耗电：%s FE");
        add("jei.inspiration.injection", "物质注入");
        add("jei.inspiration.injection.role_inject", "注入物质");
        add("jei.inspiration.injection.role_base", "基底物质");
        add("jei.inspiration.injection.tier", "需求等级 %s 级");
        add("jei.inspiration.injection.min_energy", "最小能量 %s FE");
        add("jei.inspiration.injection.max_energy", "最大能量 %s FE");

        // 侧向配置按钮上方的短标签；两个汉字以内，否则会挤到隔壁按钮
        add("gui.inspiration.facing.input", "输入");
        add("gui.inspiration.facing.output", "输出");
        add("gui.inspiration.facing.inspiration", "灵感");
        add("gui.inspiration.facing.inspiration_charge", "充能");
        add("gui.inspiration.facing.fluid_input", "流体");
        add("gui.inspiration.facing.fluid_input_2", "流体");
        add("gui.inspiration.facing.drill", "钻头");
        add("gui.inspiration.facing.fuel", "燃料");
        // Titanium 悬停提示：键是 tooltip.titanium.facing_handler.<组件名小写>
        add("tooltip.titanium.facing_handler.input", "物品输入");
        add("tooltip.titanium.facing_handler.output", "物品输出");
        add("tooltip.titanium.facing_handler.inspiration", "灵感充能物品");
        add("tooltip.titanium.facing_handler.inspiration_charge", "灵感充能罐");
        add("tooltip.titanium.facing_handler.fluid_input", "流体输入");
        add("tooltip.titanium.facing_handler.fluid_input_2", "第二个流体输入");
        add("tooltip.titanium.facing_handler.drill", "钻头槽");
        add("tooltip.titanium.facing_handler.fuel", "燃料输入");
        add("tooltip.titanium.facing_handler.inject", "待注入材料");
        add("tooltip.titanium.facing_handler.base", "基底材料");

        // 工具提示
        add("tooltip.inspiration.exp_sword.description", "攻击敌人时掉落 %s 点经验");
        add("tooltip.inspiration.storm_sowrd.description", "右键发射风弹攻击");
        add("tooltip.inspiration.sticky_axe.description", "为什么要给树木去皮？");
        add("tooltip.inspiration.sticky_axe.experience", "剥皮时掉落 %s 点经验");
        add("tooltip.inspiration.exp_pickaxe.description", "挖掘方块时掉落 %s 点经验");
        add("tooltip.inspiration.exp_hoe.description", "锄掉草方块时掉落草皮与 %s 点经验");
        add("tooltip.inspiration.exp_shovel.description", "把草方块压成土径时掉落草皮与 %s 点经验");
        add("tooltip.inspiration.energized_jade_steel.charged_buff",
                "长按右键蓄力 1.5 秒：获得 %s（消耗 %s 点经验，持续 %s 秒）");
        add("tooltip.inspiration.exploding_candy.description", "好吃到爆炸！");
        add("tooltip.inspiration.inspiration_shard.description", "放入机器的灵感槽，可提供 %s mB 灵感充能");

        // 绑定器
        add("tooltip.inspiration.binding_wand.description", "右键物质束缚器记下位置，再右键注入枪完成绑定；潜行右键注入枪可解除");
        add("tooltip.inspiration.binding_wand.none", "当前未选中物质束缚器");
        add("tooltip.inspiration.binding_wand.selected", "当前选中：(%s, %s, %s)");
        add("message.inspiration.binding_wand.selected", "已记下物质束缚器 (%s, %s, %s)");
        add("message.inspiration.binding_wand.bound", "已将该注入枪绑定到物质束缚器 (%s, %s, %s)");
        add("message.inspiration.binding_wand.unbound", "已解除该注入枪的绑定");
        add("message.inspiration.binding_wand.not_bound", "该注入枪尚未绑定任何物质束缚器");
        add("message.inspiration.binding_wand.no_selection", "请先用绑定器右键一台物质束缚器");
        add("message.inspiration.binding_wand.out_of_range", "超出物质束缚器周围 %s 的范围");
        add("message.inspiration.binding_wand.target_lost", "原先选中的物质束缚器已不存在");

        // 翡翠钢升级模板（文案结构对齐下界合金升级模板）
        add("upgrade.inspiration.jade_steel_upgrade", "翡翠钢升级");
        add("item.inspiration.energized_jade_steel_upgrade.applies_to",
                "翡翠钢剑、翡翠钢斧、翡翠钢镐、翡翠钢铲、翡翠钢锄");
        add("item.inspiration.energized_jade_steel_upgrade.ingredients", "注能翡翠钢");
        add("item.inspiration.energized_jade_steel_upgrade.base_slot_description", "放入一件翡翠钢系列工具");
        add("item.inspiration.energized_jade_steel_upgrade.additions_slot_description", "放入注能翡翠钢");
        add("upgrade.inspiration.energized_jade_steel_upgrade", "注能翡翠钢升级");
        add("item.inspiration.jade_steel_upgrade.applies_to", "灵感之剑、灵感之斧、灵感之镐、灵感之锄、灵感之铲");
        add("item.inspiration.jade_steel_upgrade.ingredients", "翡翠钢锭");
        add("item.inspiration.jade_steel_upgrade.base_slot_description", "放入一件灵感系列工具");
        add("item.inspiration.jade_steel_upgrade.additions_slot_description", "放入翡翠钢锭");

        // 机器插件
        add("tooltip.inspiration.plugin.slot", "放入机器右侧的插件槽后生效");
        add("tooltip.inspiration.plugin.energy_capacity", "机器能量缓冲容量 ×2");
        add("tooltip.inspiration.plugin.charge_capacity", "机器灵感充能上限 ×2");
        add("tooltip.inspiration.plugin.speed", "运行速度 ×2，每刻耗电 ×4，每刻灵感充能消耗 ×2");
        add("tooltip.inspiration.plugin.energy_capacity_downgrade", "机器能量缓冲容量 ×0.67");
        add("tooltip.inspiration.plugin.stack", "同类插件可叠加，每个 ×%s");
        // 物品组
        // 辐射机制
        add("tooltip.inspiration.radiation", "辐射值：%s /秒");
        // 碱性值（碱性发电机的燃料强度）
        add("tooltip.inspiration.alkalinity", "碱性值：%s");

        // 机器 tooltip：加工类写配方类型，发电类写发电机制；一个键可写多行，用 \n 分隔
        add("tooltip.inspiration.machine.matter_fusion_machine", "配方类型：物质融合");
        add("tooltip.inspiration.machine.energized_matter_fusion_machine", "配方类型：物质融合\n有第三个输入槽，可做三材料配方；速度 ×2。");
        add("tooltip.inspiration.machine.matter_reshaper", "配方类型：物质重塑\n也可以执行右键转化配方。");
        add("tooltip.inspiration.machine.mineral_twister", "配方类型：矿物扭曲");
        add("tooltip.inspiration.machine.inspiration_assembler", "配方类型：灵感装配");
        add("tooltip.inspiration.machine.core_driller", "通电后，消耗存储的所有电量和钻头耐久向下挖掘直到遇到不可采集方块。\n可以挖掘强化深板岩。");
        add("tooltip.inspiration.machine.high_energy_matter_reshaper", "配方类型：物质重塑\n也可以执行右键转化配方；速度 ×2。");
        add("tooltip.inspiration.machine.high_energy_mineral_twister", "配方类型：矿物扭曲\n产出等效时运 12；速度 ×2。");
        add("tooltip.inspiration.machine.high_energy_inspiration_assembler", "配方类型：灵感装配\n有六个输入槽与两个流体罐，可做材料更多的配方；速度 ×2。");
        add("tooltip.inspiration.machine.alkaline_reactor", "投入碱性物质发电。\n发电量：100x/tick；燃烧时间：12000tick。\nx为物品碱性值");
        add("tooltip.inspiration.machine.rtg_generator", "投入放射性物质发电。\n发电量：1000x/tick；燃烧时间：24000tick。\nx为物品辐射值\n每分钟向所在区块释放(x-1)³点辐射；x>9时每分钟有(x-9)/1000的概率爆炸。\n烧完后产出x个核废料。");
        add("tooltip.inspiration.machine.structure_simulator", "投入结构数据进行模拟。\n灵感充能：8mB/tick；电力消耗随数据而定。\n需要用高能机器外壳搭出4宽*3高*3长的中空结构，并装上五种仓室（内部两格留空或填上都算成型）。");
        // ===================== 结构模拟机 =====================
        // 结构数据物品的三行参数
        add("tooltip.inspiration.structure_data.duration", "模拟时长：%s tick");
        add("tooltip.inspiration.structure_data.energy", "耗电：%s FE/tick");
        add("tooltip.inspiration.structure_data.charge", "灵感充能：%s mB/tick");
        add("tooltip.inspiration.structure_data.unconsumed", "放入数据仓即可运行，不会被消耗");
        // 结构投影仪
        add("tooltip.inspiration.projector.usage", "手持右键结构模拟机的主方块，投影出完整结构。");
        add("tooltip.inspiration.projector.duration", "虚影持续 30 秒，不消耗物品。");
        // JEI 里的结构预览：每层一页
        add("jei.inspiration.structure_preview", "结构模拟机结构");
        add("jei.inspiration.structure_preview.layer", "第%s/%s层·%s");
        add("jei.inspiration.structure_preview.layer.0", "后");
        add("jei.inspiration.structure_preview.layer.1", "中");
        add("jei.inspiration.structure_preview.layer.2", "前");
        // 主方块界面上的运行状态（顺序即优先级，见 SimulatorStatus）
        add("gui.inspiration.structure_simulator.status.not_formed", "结构未成型");
        add("gui.inspiration.structure_simulator.status.no_data", "数据仓里没有结构数据");
        add("gui.inspiration.structure_simulator.status.no_energy", "电力不足");
        add("gui.inspiration.structure_simulator.status.no_charge", "灵感充能不足");
        add("gui.inspiration.structure_simulator.status.output_full", "输出仓已满");
        add("gui.inspiration.structure_simulator.status.working", "正在模拟");

        add("tooltip.inspiration.drill_bit.description", "地核钻探者的耗材：每次作业消耗 1 点耐久");
        add("item.inspiration.geiger_counter.desc", "手持右键测量所在区块的辐射值");
        add("item.inspiration.geiger_counter.reading", "区块 %s ・ 体内 %s（%s）");
        add("item.inspiration.world_data_detector.desc", "仅创造模式可得：右键读出本存档种子随机的注入能量窗口");
        add("item.inspiration.world_data_detector.header", "本存档的种子随机注入能量（种子 %s）：");
        add("item.inspiration.world_data_detector.line", "%s：%s");
        add("item.inspiration.world_data_detector.empty", "本存档没有种子随机的注入配方。");
        // ===================== 灵感指导书 =====================
        add("container.inspiration.guide_book", "灵感指导书");
        add("guide.inspiration.chapter.inspiration", "灵感");
        add("guide.inspiration.chapter.inspiration.body",
                "灵感是玩家思维力量的具体化：\n"
                        + "头脑里那些说不清的想法凝成实体，\n"
                        + "就成了这样一枚能握在手里的东西。\n\n"
                        + "它既是合成材料，也是机器的燃料 ——\n"
                        + "只有电、没有灵感的机器是转不动的。");
        add("guide.inspiration.chapter.machines", "机器总览");
        add("guide.inspiration.chapter.machines.body",
                "物质融合器：把几样材料融成一样。\n"
                        + "强能物质融合机：融合器的上位机型。\n"
                        + "物质重塑机：把材料重塑成另一种。\n"
                        + "矿物扭曲机：扭曲矿物，产量随机。\n"
                        + "灵感装配台：把多份材料组装成成品。\n"
                        + "地核钻探者：向下掏出一条竖井。\n\n"
                        + "机器都只进不出，需要从外部接电，\n"
                        + "多数还要持续补充灵感充能。");
        add("guide.inspiration.chapter.energy", "能量与充能");
        add("guide.inspiration.chapter.energy.body",
                "机器界面上的能量条只减不增，\n"
                        + "电要从外部接进来。\n\n"
                        + "灵感充能单独存在罐体里：\n"
                        + "灵感 = 1000 mB，灵感碎片 = 100 mB。\n"
                        + "充能见底时进度会停下，\n"
                        + "但已经投进去的材料不会损失。");
        add("guide.inspiration.chapter.plugins", "插件");
        add("guide.inspiration.chapter.plugins.body",
                "每台机器右侧都有四个插件槽。\n\n"
                        + "速度插件：速度翻倍，耗电与耗充能随之上升。\n"
                        + "容量插件：抬高能量缓冲的上限。\n"
                        + "电容降级插件：把上限压到 67%。\n"
                        + "它不是削弱，而是用来压低一次放电的规模。");
        add("guide.inspiration.chapter.injection", "注入合成");
        add("guide.inspiration.chapter.injection.body",
                "在物质束缚器下方摆好底座、四周放上注入枪，\n"
                        + "就搭成了一套注入结构。\n\n"
                        + "底材放进束缚器，注入物放进枪，\n"
                        + "红石脉冲触发一次放电。\n\n"
                        + "放电量必须落在配方要求的窗口内：\n"
                        + "低了不反应，高了会炸。");
        add("guide.inspiration.chapter.radiation", "辐射");
        add("guide.inspiration.chapter.radiation.body",
                "钫系材料会向区块泄漏辐射，\n"
                        + "也会在体内一点一点累积。\n\n"
                        + "把钫盐矿留在地狱，别在矿区久留；\n"
                        + "随身的盖革计数器能读出区块与体内的数值。");

        add("radiation.inspiration.severity.0", "无症状");
        add("radiation.inspiration.severity.1", "轻度辐射病");
        add("radiation.inspiration.severity.2", "中度辐射病");
        add("radiation.inspiration.severity.3", "重度辐射病");
        add("radiation.inspiration.severity.4", "急性辐射病");
        add("radiation.inspiration.severity.5", "致命辐射病");

        add("itemGroup.inspire_tab", "灵感");

        // 配置界面（保持与英文相同，未汉化）
        add("inspiration.configuration.title", "Inspiration Configs");
        add("inspiration.configuration.section.inspiration.common.toml", "Inspiration Configs");
        add("inspiration.configuration.section.inspiration.common.toml.title", "Inspiration Configs");
        add("inspiration.configuration.items", "Item List");
        add("inspiration.configuration.logDirtBlock", "Log Dirt Block");
        add("inspiration.configuration.magicNumberIntroduction", "Magic Number Text");
        add("inspiration.configuration.magicNumber", "Magic Number");
    }
}