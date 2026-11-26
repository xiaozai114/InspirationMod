package com.starcrosser.inspiration.material;

import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.item.custom.AlkaliItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * 一个「锭类材料」= 一次声明。
 * <p>
 * 以前注册一个带方块的锭要动 10 个文件：物品、方块、方块物品、中英语言、物品模型、
 * 方块状态、方块模型、战利品表、挖掘标签、创造栏，若还带碱性或辐射，再手写 3 条
 * data_maps。这里把它们收成一处：声明完调 {@link #register()}，注册行为一次性完成，
 * 剩下的配套资源由各 datagen Provider 遍历 {@link ModMaterials#ALL} 自动生成。
 * <p>
 * <b>「0」的语义</b>：{@code alkalinity = 0} 或 {@code radiation = 0} 表示「没有这个属性」——
 * 不写进 data_map、不显示 tooltip、不能当对应发电机的燃料。所以不要在表里塞一堆 0。
 * <p>
 * <b>开关</b>：锭本体总是生成；方块、粒、尘、熔融流体各自一个开关，默认全关。
 * 关着的开关不产生任何东西，因此「加材料」和「加内容」是两件事，可以分开提交。
 */
public final class InspirationMaterial {

    /** 一粒的辐射周期：9 粒 = 1 锭，所以粒写成「每 9 秒释放锭那么多点」。 */
    public static final int NUGGET_RADIATION_PERIOD = 9;

    /** 一撮尘的辐射周期：9 尘 = 1 粒、81 尘 = 1 锭。 */
    public static final int GRIT_RADIATION_PERIOD = 81;

    /* ==================== 声明期字段 ==================== */

    private final String baseId;
    private String itemId;
    private String blockId;
    private String nuggetId;
    private String gritId;
    private final String moltenId;

    private String zhBase, enBase;
    private String zhItem, enItem;
    private String zhBlock, enBlock;
    private String zhNugget, enNugget;
    private String zhGrit, enGrit;

    private Rarity rarity = Rarity.COMMON;
    private int alkalinity;
    private int radiation;

    private float alkaliRadius;

    private float hardness = 2.5F;
    private float resistance = 6.0F;
    private UnaryOperator<BlockBehaviour.Properties> blockTweak = p -> p;
    private Function<BlockBehaviour.Properties, Block> blockFactory = Block::new;

    private boolean withBlock;
    private boolean withNugget;
    private boolean withGrit;
    private boolean withMolten;
    /** 熔融金属的染色（ARGB）；0xFFFFFF = 不染色，贴图自带颜色。 */
    private int moltenTint = 0xFFFFFF;
    private com.starcrosser.inspiration.fluid.MoltenMetals.Entry moltenEntry;

    /* ==================== 注册期产物 ==================== */

    private DeferredItem<Item> item;
    private DeferredBlock<Block> block;
    private DeferredItem<Item> blockItem;
    private DeferredItem<Item> nugget;
    private DeferredItem<Item> grit;

    private InspirationMaterial(String baseId) {
        this.baseId = baseId;
        this.itemId = baseId + "_ingot";
        this.blockId = baseId + "_block";
        this.nuggetId = baseId + "_nugget";
        this.gritId = baseId + "_grit";
        this.moltenId = "molten_" + baseId;
    }

    /** 以基础名起手：{@code "cesium"} 派生出 {@code cesium_ingot} / {@code cesium_block} / … */
    public static InspirationMaterial create(String baseId) {
        return new InspirationMaterial(baseId);
    }

    /* ==================== 声明 API ==================== */

    /** 中英基础名，默认派生：物品「名 + 锭」、方块「名 + 块」、粒「名 + 粒」、尘「名 + 尘」。 */
    public InspirationMaterial name(String zh, String en) {
        this.zhBase = zh;
        this.enBase = en;
        return this;
    }

    /** 物品 ID 与默认派生不同时单独指定（如玄武岩合金是 {@code basalt_alloy} 而非 {@code basalt_alloy_ingot}）。 */
    public InspirationMaterial itemId(String id) {
        this.itemId = id;
        return this;
    }

    /** 方块 ID 与默认派生不同时单独指定。 */
    public InspirationMaterial blockId(String id) {
        this.blockId = id;
        return this;
    }

    /** 物品的显示名与「基础名 + 锭」不同时单独指定。 */
    public InspirationMaterial itemName(String zh, String en) {
        this.zhItem = zh;
        this.enItem = en;
        return this;
    }

    /** 方块的显示名与「基础名 + 块」不同时单独指定。 */
    public InspirationMaterial blockName(String zh, String en) {
        this.zhBlock = zh;
        this.enBlock = en;
        return this;
    }

    public InspirationMaterial rarity(Rarity rarity) {
        this.rarity = rarity;
        return this;
    }

    /** 碱性值：{@code > 0} 才写进 data_map，碱性发电机每刻产电「100 × 值」FE。 */
    public InspirationMaterial alkalinity(int value) {
        this.alkalinity = value;
        return this;
    }

    /**
     * 碱金属行为：锭用 {@code AlkaliItem}，遇水炸开，爆炸半径按格计。
     * 与 {@link #alkalinity(int)} 是两回事 —— 钙粉碱性 1、半径却只有 0.1f。
     */
    public InspirationMaterial alkaliExplosion(float radius) {
        this.alkaliRadius = radius;
        return this;
    }

    /** 辐射值：{@code > 0} 才写进 data_map；有方块时方块值取「物品值 × 9」。 */
    public InspirationMaterial radiation(int value) {
        this.radiation = value;
        return this;
    }

    /** 方块硬度与爆炸抗性，默认 2.5F / 6.0F（与绝大多数材料块一致）。 */
    public InspirationMaterial hardness(float hardness, float resistance) {
        this.hardness = hardness;
        this.resistance = resistance;
        return this;
    }

    /** 方块属性的额外调整（地图色、音效等），在硬度之后套用。 */
    public InspirationMaterial blockTweak(UnaryOperator<BlockBehaviour.Properties> tweak) {
        this.blockTweak = tweak;
        return this;
    }

    /** 需要自定义方块类时指定工厂，默认是 {@code Block}。 */
    public InspirationMaterial blockFactory(Function<BlockBehaviour.Properties, Block> factory) {
        this.blockFactory = factory;
        return this;
    }

    /** 是否生成方块（+ 方块物品）。默认关。 */
    public InspirationMaterial block(boolean on) {
        this.withBlock = on;
        return this;
    }

    /** 是否生成粒（{@code <base>_nugget}）。默认关。 */
    public InspirationMaterial nugget(boolean on) {
        this.withNugget = on;
        return this;
    }

    /** 是否生成尘（{@code <base>_grit}）。默认关。 */
    public InspirationMaterial grit(boolean on) {
        this.withGrit = on;
        return this;
    }

    /**
     * 是否生成熔融流体（{@code molten_<base>}）。默认关。
     * <p>
     * 打开后会一并登记：流体类型、源、流动、桶。贴图由
     * {@code tools/textures/gen_molten_metals.py} 按需补（只补缺失的，不覆盖已有美术）。
     */
    public InspirationMaterial molten(boolean on) {
        this.withMolten = on;
        return this;
    }

    /**
     * 熔融流体的染色（ARGB 的 RGB 部分）。{@code 0xFFFFFF} 表示不染色 —— 已经有专属贴图时就用它，
     * 没有专属贴图才靠这个颜色把通用白色纹理染成材料色。
     */
    public InspirationMaterial moltenTint(int rgb) {
        this.moltenTint = rgb & 0xFFFFFF;
        return this;
    }

    /* ==================== 注册 ==================== */

    public InspirationMaterial register() {
        this.item = ModItems.ITEMS.register(itemId, this::createItem);
        if (withBlock) {
            this.block = ModBlocks.BLOCKS.register(blockId,
                    () -> blockFactory.apply(blockTweak.apply(BlockBehaviour.Properties.of()
                            .strength(hardness, resistance).requiresCorrectToolForDrops())));
            // 方块物品与方块同名（原版惯例），这也是它能在语言文件里复用方块名的原因
            this.blockItem = ModItems.ITEMS.register(blockId,
                    () -> new BlockItem(block.get(), new Item.Properties()));
        }
        if (withNugget) {
            this.nugget = ModItems.ITEMS.register(nuggetId, () -> new Item(new Item.Properties()));
        }
        if (withGrit) {
            this.grit = ModItems.ITEMS.register(gritId, () -> new Item(new Item.Properties()));
        }
        if (withMolten) {
            this.moltenEntry = com.starcrosser.inspiration.fluid.MoltenMetals.register(moltenId, moltenTint);
        }
        ModMaterials.ALL.add(this);
        return this;
    }

    private Item createItem() {
        Item.Properties props = new Item.Properties().rarity(rarity);
        if (alkaliRadius > 0) {
            return new AlkaliItem(props, alkaliRadius);
        }
        return new Item(props);
    }

    /* ==================== 取值（datagen 与游戏逻辑用） ==================== */

    public String baseId() {
        return baseId;
    }

    public String itemId() {
        return itemId;
    }

    public String blockId() {
        return blockId;
    }

    public String nuggetId() {
        return nuggetId;
    }

    public String gritId() {
        return gritId;
    }

    /** 声明了熔融流体时返回登记结果，否则为 null。 */
    public com.starcrosser.inspiration.fluid.MoltenMetals.Entry moltenEntry() {
        return moltenEntry;
    }

    public String moltenId() {
        return moltenId;
    }

    public DeferredItem<Item> item() {
        return item;
    }

    /** 方块物品：与方块同名，注册在物品表里。材料没有方块时返回 null。 */
    public DeferredItem<Item> blockItem() {
        return blockItem;
    }

    public DeferredBlock<Block> block() {
        return block;
    }

    public DeferredItem<Item> nugget() {
        return nugget;
    }

    public DeferredItem<Item> grit() {
        return grit;
    }

    public boolean hasBlock() {
        return withBlock;
    }

    public boolean hasNugget() {
        return withNugget;
    }

    public boolean hasGrit() {
        return withGrit;
    }

    public int alkalinity() {
        return alkalinity;
    }

    public int radiation() {
        return radiation;
    }

    /** 方块的辐射值：物品值的 9 倍（9 枚锭压一块）。没有方块或没有辐射时返回 0。 */
    public int blockRadiation() {
        return (withBlock && radiation > 0) ? radiation * 9 : 0;
    }

    public String zhItem() {
        return zhItem != null ? zhItem : zhBase + "锭";
    }

    public String enItem() {
        return enItem != null ? enItem : enBase + " Ingot";
    }

    public String zhBlock() {
        return zhBlock != null ? zhBlock : zhBase + "块";
    }

    public String enBlock() {
        return enBlock != null ? enBlock : enBase + " Block";
    }

    public String zhNugget() {
        return zhNugget != null ? zhNugget : zhBase + "粒";
    }

    public String enNugget() {
        return enNugget != null ? enNugget : enBase + " Nugget";
    }

    public String zhGrit() {
        return zhGrit != null ? zhGrit : zhBase + "尘";
    }

    public String enGrit() {
        return enGrit != null ? enGrit : enBase + " Grit";
    }

    /**
     * 把本材料自带的碱性/辐射条目写进 data_map。
     * 值为 0 时不写任何条目（见类注释的「0」语义）。
     * <p>
     * 辐射的分子跟着锭走，周期（分母）按形态派生，因此「9 枚粒」与「1 枚锭」的
     * 平均速率相同：锭 {@code 值/秒}、粒 {@code 值/9 秒}、尘 {@code 值/81 秒}。
     */
    public void addDataMaps(DataMapProvider.Builder<Integer, Item> alkalinity,
                            DataMapProvider.Builder<Integer, Item> itemRadiation,
                            DataMapProvider.Builder<Integer, Item> radiationPeriod,
                            DataMapProvider.Builder<Integer, Block> blockRadiation) {
        if (alkalinity != null && this.alkalinity > 0) {
            alkalinity.add(item.getKey(), this.alkalinity, false);
        }
        if (itemRadiation == null || radiation <= 0) {
            return;
        }
        itemRadiation.add(item.getKey(), radiation, false);       // 锭：周期 1，不用写分母表
        if (withNugget && radiationPeriod != null) {
            itemRadiation.add(nugget.getKey(), radiation, false);
            radiationPeriod.add(nugget.getKey(), NUGGET_RADIATION_PERIOD, false);
        }
        if (withGrit && radiationPeriod != null) {
            itemRadiation.add(grit.getKey(), radiation, false);
            radiationPeriod.add(grit.getKey(), GRIT_RADIATION_PERIOD, false);
        }
        if (blockRadiation != null && blockRadiation() > 0) {
            // 方块物品也要登记：玩家把它拿在背包里同样会往外放辐射
            blockRadiation.add(block.getKey(), blockRadiation(), false);
            itemRadiation.add(blockItem.getKey(), blockRadiation(), false);
        }
    }
}
