package com.starcrosser.inspiration.material;

import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;

import java.util.ArrayList;
import java.util.List;

/**
 * 本模组的锭类材料清单。
 * <p>
 * 这里只放「声明」：每个材料一行调用链，把物品、方块、data_maps 条目一次说清。
 * 配套资源（中英语言、物品模型、方块状态、方块模型、战利品表、创造栏）
 * 由各 datagen Provider 遍历 {@link #ALL} 自动生成，加材料不用再逐个补。
 * <p>
 * 见 {@code material/README.md} 里的开关说明与「什么不该放进这里」。
 */
public final class ModMaterials {

    /** 全部材料，按声明顺序；datagen 遍历它生成配套资源。 */
    public static final List<InspirationMaterial> ALL = new ArrayList<>();

    /* ==================== 基础材料 ==================== */

    /**
     * 铯：铯粉高炉冶炼得到，遇水炸开并变回铯盐（半径 4 格），碱性 4。
     * 方块是铯块（9 枚锭压一块，硬度 2.5/6.0，镐挖）。
     */
    public static final InspirationMaterial CESIUM = InspirationMaterial.create("cesium")
            .name("铯", "Cesium")
            .alkalinity(4)
            .alkaliRevert(() -> ModItems.CESIUM_SALT.get(), 4f)
            .block(true)
            .register();

    /** 红石锭：红石粉在物质重塑机里压成，方块名沿用「红石锭方块」。 */
    public static final InspirationMaterial REDSTONE = InspirationMaterial.create("redstone")
            .name("红石", "Redstone")
            .itemId("redstone_ingot")
            .blockId("redstone_ingot_block")
            .itemName("红石锭", "Redstone Ingot")
            .blockName("红石锭方块", "Redstone Ingot Block")
            .block(true)
            .register();

    /** 青金石锭：青金石在物质重塑机里压成。 */
    public static final InspirationMaterial LAPIS = InspirationMaterial.create("lapis")
            .name("青金石", "Lapis")
            .itemId("lapis_ingot")
            .blockId("lapis_ingot_block")
            .itemName("青金石锭", "Lapis Ingot")
            .blockName("青金石锭方块", "Lapis Ingot Block")
            .block(true)
            .register();

    /** 翡翠钢锭：翡翠钢系列工具的材料，没有方块形态。 */
    public static final InspirationMaterial JADE_STEEL = InspirationMaterial.create("jade_steel")
            .name("翡翠钢", "Jade Steel")
            .register();

    /** 玄武岩合金：地核钻探者与注入结构 1 级底座的材料，方块硬度 3.0/9.0。 */
    public static final InspirationMaterial BASALT_ALLOY = InspirationMaterial.create("basalt_alloy")
            .name("玄武岩合金", "Basalt Alloy")
            .itemId("basalt_alloy")
            .itemName("玄武岩合金", "Basalt Alloy")
            .hardness(3.0F, 9.0F)
            .block(true)
            .register();

    /** 等离子锭：钻石经物质重塑得到的高阶材料，没有方块形态。 */
    public static final InspirationMaterial PLASMA = InspirationMaterial.create("plasma")
            .name("等离子", "Plasma")
            .rarity(Rarity.UNCOMMON)
            .register();

    /** 思维矩阵锭：思维晶体 + 紫水晶 + 下界石英融合而成，没有方块形态。 */
    public static final InspirationMaterial MIND_MATRIX = InspirationMaterial.create("mind_matrix")
            .name("思维矩阵", "Mind Matrix")
            .rarity(Rarity.UNCOMMON)
            .register();

    /* ==================== 钫（碱性强于铯，且全流程带辐射） ==================== */

    /**
     * 钫锭：钫粉高炉冶炼得到，遇水炸开并变回钫盐（半径 6 格），碱性 6、辐射 3。
     * 钫块的辐射取 27（= 3 × 9），物品表与方块表各登记一份 —— 玩家把钫块带在背包里同样会放辐射。
     */
    public static final InspirationMaterial FRANCIUM = InspirationMaterial.create("francium")
            .name("钫", "Francium")
            .alkalinity(6)
            .alkaliRevert(() -> ModItems.FRANCIUM_SALT.get(), 6f)
            .radiation(3)
            .block(true)
            .register();

    /* ==================== 注能锭（向普通锭注入灵感得到） ==================== */

    /** 注能青金石锭：青金石锭注入灵感（10k~15k FE），高能矿物扭曲机的材料。 */
    public static final InspirationMaterial ENERGIZED_LAPIS = InspirationMaterial.create("energized_lapis")
            .name("注能青金石", "Energized Lapis")
            .rarity(Rarity.UNCOMMON)
            .register();

    /** 注能等离子锭：等离子锭注入灵感（1M~2M FE），高能灵感装配台的材料。 */
    public static final InspirationMaterial ENERGIZED_PLASMA = InspirationMaterial.create("energized_plasma")
            .name("注能等离子", "Energized Plasma")
            .rarity(Rarity.RARE)
            .register();

    /** 注能红石锭：红石锭注入灵感（150k~300k FE），高能物质重塑机的材料。 */
    public static final InspirationMaterial ENERGIZED_REDSTONE = InspirationMaterial.create("energized_redstone")
            .name("注能红石", "Energized Redstone")
            .rarity(Rarity.RARE)
            .register();

    /** 注能翡翠钢：翡翠钢注入灵感的产物，名字不带「锭」。 */
    public static final InspirationMaterial ENERGIZED_JADE_STEEL =
            InspirationMaterial.create("energized_jade_steel")
                    .name("注能翡翠钢", "Energized Jade Steel")
                    .itemId("energized_jade_steel")
                    .itemName("注能翡翠钢", "Energized Jade Steel")
                    .rarity(Rarity.UNCOMMON)
                    .register();

    /* ==================== 合金 ==================== */

    /** 末地合金：4 黑曜石 + 末影珍珠 + 4 紫珀块融合而成，下一阶段的主材。 */
    public static final InspirationMaterial END_ALLOY = InspirationMaterial.create("end_alloy")
            .name("末地合金", "End Alloy")
            .itemId("end_alloy")
            .itemName("末地合金", "End Alloy")
            .rarity(Rarity.RARE)
            .register();

    /** 无恒合金：无恒物质注入末地合金得到；注入所需的能量窗口由存档种子决定。 */
    public static final InspirationMaterial EPHEMERAL_ALLOY = InspirationMaterial.create("ephemeral_alloy")
            .name("无恒合金", "Ephemeral Alloy")
            .itemId("ephemeral_alloy")
            .itemName("§d无恒合金", "§dEphemeral Alloy")
            .rarity(Rarity.EPIC)
                    .molten(true)
            .register();

    /** 光谱合金：镍 + 钍 + 虚空露珠 + 思维矩阵融合而来，带轻微辐射；矿物模拟机主方块的副材料。 */
    public static final InspirationMaterial SPECTRUM_ALLOY = InspirationMaterial.create("spectrum_alloy")
            .name("光谱合金", "Spectrum Alloy")
            .itemId("spectrum_alloy")
            .rarity(Rarity.EPIC)
            .radiation(1)
            .register();

    /** 幽匿合金：回响碎片 + 强化深板岩融合得到，方块是注入结构的 2 级底座（硬度 4.0/12.0）。 */
    public static final InspirationMaterial SCULK_ALLOY = InspirationMaterial.create("sculk_alloy")
            .name("幽匿合金", "Sculk Alloy")
            .itemId("sculk_alloy")
            .itemName("幽匿合金", "Sculk Alloy")
            .rarity(Rarity.RARE)
            .hardness(4.0F, 12.0F)
            .blockTweak(p -> p.mapColor(MapColor.COLOR_CYAN).sound(SoundType.SCULK))
            .block(true)
            .register();

    /** 极限合金：名字带颜色代码，是最早加进来的合金之一。 */
    public static final InspirationMaterial EXTREME_ALLOY = InspirationMaterial.create("extreme_alloy")
            .name("极限合金", "Extreme Alloy")
            .itemId("extreme_alloy")
            .itemName("§9极限§4合金", "§9Extreme §4Alloy")
            .register();

    /* ==================== 精炼金属（原料精炼机的产物） ==================== */
    //
    // 这一组都由原料精炼机产出：主产物是锭，副产物是粒（9 粒 = 1 锭），
    // 镎与钚更细，副产物是尘（9 尘 = 1 粒、81 尘 = 1 锭）。
    // 辐射值写在材料上（锭的量级），粒与尘的周期由 InspirationMaterial 自动派生，
    // 因此这里只写「铀 2」一个数，粒尘不用管。

    /** 钙锭：钙粉烧成；原料精炼机的机壳材料。没有碱性与辐射（碱金属行为属于铯/钫）。 */
    public static final InspirationMaterial CALCIUM = InspirationMaterial.create("calcium")
            .name("钙", "Calcium")
            .register();

    /** 镍：粗铁的副产物，只有粒是配方直接产出的（锭靠 9 粒合成）。 */
    public static final InspirationMaterial NICKEL = InspirationMaterial.create("nickel")
            .name("镍", "Nickel")
            .nugget(true)
            .register();

    /** 铀：粗钫盐与铯盐的副产物，辐射 2。 */
    public static final InspirationMaterial URANIUM = InspirationMaterial.create("uranium")
            .name("铀", "Uranium")
            .rarity(Rarity.UNCOMMON)
            .radiation(2)
            .nugget(true)
            .register();

    /** 钍：粗钫盐的副产物，辐射 1。 */
    public static final InspirationMaterial THORIUM = InspirationMaterial.create("thorium")
            .name("钍", "Thorium")
            .rarity(Rarity.UNCOMMON)
            .radiation(1)
            .nugget(true)
            .register();

    /** 铷：粗铯盐的副产物，碱性 2（与铯同源但弱一档）。 */
    public static final InspirationMaterial RUBIDIUM = InspirationMaterial.create("rubidium")
            .name("铷", "Rubidium")
            .alkalinity(2)
            .nugget(true)
            .register();

    /** 锕：钫盐的副产物，辐射 1。 */
    public static final InspirationMaterial ACTINIUM = InspirationMaterial.create("actinium")
            .name("锕", "Actinium")
            .rarity(Rarity.UNCOMMON)
            .radiation(1)
            .nugget(true)
            .register();

    /** 镎：核废料的副产物，辐射 6；副产物是尘而不是粒（9 尘 = 1 粒）。 */
    public static final InspirationMaterial NEPTUNIUM = InspirationMaterial.create("neptunium")
            .name("镎", "Neptunium")
            .rarity(Rarity.RARE)
            .radiation(6)
            .nugget(true)
            .grit(true)
            .register();

    /** 钚：核废料的副产物，辐射 4；副产物同样是尘。 */
    public static final InspirationMaterial PLUTONIUM = InspirationMaterial.create("plutonium")
            .name("钚", "Plutonium")
            .rarity(Rarity.RARE)
            .radiation(4)
            .nugget(true)
            .grit(true)
            .register();

    private ModMaterials() {
    }

    /**
     * 触发静态初始化（把所有材料注册进物品表与方块表）。
     * 必须在 {@code ModItems.register(bus)} 与 {@code ModBlocks.register(bus)} 之前调用，
     * 否则注册表已经冻结，再注册会抛异常。
     */
    public static void init() {
    }
}
