package com.starcrosser.inspiration.item;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.material.InspirationMaterial;
import com.starcrosser.inspiration.material.ModMaterials;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import com.starcrosser.inspiration.registry.MachineRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 创造模式物品栏：两页。
 * <p>
 * 分页规则是「是不是锭类材料」：<b>材料</b>页只放锭类材料及其附属形态（锭 / 粒 / 块 / 尘），
 * 遍历 {@link ModMaterials#ALL} 生成；<b>灵感</b>页放其余一切 —— 能拿起来就用的东西
 * （食物、工具、机器、功能方块、插件、模板、结构数据、指导书），
 * 以及不是锭类材料的原料（矿、粉、盐、晶、煤、核心部件、废料）放在末尾。
 * <p>
 * <b>不必手动往里加东西</b>：
 * <ul>
 *   <li>锭类材料 —— 遍历 {@link ModMaterials#ALL}；</li>
 *   <li>机器 —— 遍历 {@link com.starcrosser.inspiration.registry.MachineRegistry}（注册即入栏）；</li>
 *   <li>其余物品 —— 写了就有明确位置，没写的由灵感页末尾的**兜底循环**补齐
 *       （凡是本模组注册、又没被这两页收走的物品，一律追加到灵感页最后）。</li>
 * </ul>
 * 显式清单只用来决定「排在哪儿」——一条生产线的上下游要挨在一起，而注册顺序跟玩法脉络对不上。
 */
public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MOD_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Inspiration.MODID);

    /* ==================== 灵感页：能直接用的东西 ==================== */

    public static final Supplier<CreativeModeTab> INSPIRATION_TAB =
            CREATIVE_MOD_TABS.register("inspire_tab", ()-> CreativeModeTab.builder()
                    .icon(()->new ItemStack(ModItems.INSPIRATION.get()))
                    .title(Component.translatable("itemGroup.inspire_tab"))
                    .displayItems((parameters, raw) -> {
                        // 包一层：记下已经排过版的物品，末尾的兜底循环靠它判断「谁还没进栏」。
                        // 只需实现唯一的抽象方法，其余 accept 重载都会默认转到这里。
                        Set<Item> placed = new HashSet<>();
                        CreativeModeTab.Output output = new CreativeModeTab.Output() {
                            @Override
                            public void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
                                placed.add(stack.getItem());
                                raw.accept(stack, visibility);
                            }
                        };
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
                        // 机器插件：五种，装进机器侧栏改变性能
                        output.accept(ModItems.CAPACITOR_PLUGIN);
                        output.accept(ModItems.INSPIRATION_PLUGIN);
                        output.accept(ModItems.SPEED_PLUGIN);
                        output.accept(ModItems.CAPACITOR_DOWNGRADE_PLUGIN);
                        output.accept(ModItems.RECYCLING_PLUGIN);
                        // 信标与 TNT
                        output.accept(ModItems.EMPTY_BEACON);
                        output.accept(ModItems.POWER_BEACON);
                        output.accept(ModBlocks.INTENSIFIED_TNT);
                        output.accept(ModBlocks.EMPOWERED_TNT_ITEM);
                        output.accept(ModItems.MIND_FLUID_BUCKET.get());
                        output.accept(ModItems.MOLTEN_PLASMA_BUCKET.get());
                        // 思维流体反应生成的四类方块
                        output.accept(ModBlocks.DILUTED_INSPIRATION_CRYSTAL);
                        output.accept(ModBlocks.CONCENTRATED_INSPIRATION_CRYSTAL);
                        output.accept(ModBlocks.INSPIRATION_GEL);
                        output.accept(ModBlocks.MIND_CRYSTAL_ROCK);
                        // 机器：遍历 MachineRegistry，注册了就会出现在这里，顺序 = 注册顺序
                        // （也就是《机器体系》里那张表的顺序：先加工机器，最后两台发电机）
                        for (MachineRegistry.MachineEntry entry : MachineRegistry.machines()) {
                            output.accept(entry.block());
                        }
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
                        // 以下是非锭类原料：锭类材料都在材料页，这些没有锭形态，放在灵感页末尾
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
                        // 兜底：凡是本模组注册、又没被上面排到（也不属于材料页）的物品，一律追加到这里
                        // —— 以后加物品忘了写位置也不会「从创造栏里消失」
                        Set<Item> materials = materialItems();
                        for (Item item : BuiltInRegistries.ITEM) {
                            if (placed.contains(item) || materials.contains(item)) {
                                continue;
                            }
                            if (Inspiration.MODID.equals(BuiltInRegistries.ITEM.getKey(item).getNamespace())) {
                                output.accept(item);
                            }
                        }
                    })
                    .build());

    /* ==================== 材料页：锭类材料与附属形态 ==================== */

    public static final Supplier<CreativeModeTab> MATERIAL_TAB =
            CREATIVE_MOD_TABS.register("material_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModMaterials.CESIUM.item().get()))
                    .title(Component.translatable("itemGroup.material_tab"))
                    .displayItems((parameters, output) -> {
                        // 只放锭类材料：锭 —— 块 —— 粒 —— 尘，同一材料的四种形态挨在一起
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
                    })
                    .build());

    /**
     * 锭类材料的全部形态（锭 / 块 / 粒 / 尘）。
     * <p>
     * 这些是材料页的常客，灵感页的兜底循环要把它们排除掉，否则同一件东西会出现两页。
     * 只在 {@code displayItems} 里调用，那时材料已经注册完毕。
     */
    private static Set<Item> materialItems() {
        Set<Item> items = new HashSet<>();
        for (InspirationMaterial material : ModMaterials.ALL) {
            items.add(material.item().get());
            if (material.hasBlock()) {
                items.add(material.block().get().asItem());
            }
            if (material.hasNugget()) {
                items.add(material.nugget().get());
            }
            if (material.hasGrit()) {
                items.add(material.grit().get());
            }
        }
        return items;
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MOD_TABS.register(eventBus);
    }
}
