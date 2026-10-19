package com.starcrosser.inspiration.block;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.custom.ConcentratedInspirationCrystalBlock;
import com.starcrosser.inspiration.block.custom.DilutedInspirationCrystalBlock;
import com.starcrosser.inspiration.block.custom.InspirationGelBlock;
import com.starcrosser.inspiration.block.custom.MindCrystalRockBlock;
import com.starcrosser.inspiration.block.custom.MindFluidBlock;
import com.starcrosser.inspiration.fluid.ModFluids;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.item.custom.AlkaliBlockItem;
import com.starcrosser.inspiration.multiblock.block.MachineCasingBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.starcrosser.inspiration.Inspiration.MODID;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MODID);
    /** 高能机器外壳：结构模拟机的机体材料，外观是淡紫色的铁块。 */
    public static final DeferredBlock<Block> HIGH_ENERGY_MACHINE_CASING =
            registerBlocks("high_energy_machine_casing", () -> new MachineCasingBlock());
    public static final DeferredRegister.Blocks FLUID_BLOCKS = DeferredRegister.createBlocks(MODID);
    private static <T extends Block> void registerBlockItems(String name , DeferredBlock<T> block){
        ModItems.ITEMS.register(name,()->new BlockItem(block.get(), new Item.Properties()));
    }
    private static <T extends Block> DeferredBlock<T> registerBlocks(String name , Supplier<T> block){
        DeferredBlock<T> blocks=BLOCKS.register(name,block);
        registerBlockItems(name,blocks);
        return blocks;
    }
    /**
     * 只注册方块、不注册方块物品。
     * <p>
     * 流体方块拿在手里没有意义（装它的桶才是物品），而一旦给它注册了物品却没有
     * 对应的物品模型，它在 JEI 物品列表里就会显示成黑紫块。
     */
    private static <T extends Block> DeferredBlock<T> registerBlockOnly(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }
    public static final DeferredBlock<Block> LIGNITE_BLOCK =
            registerBlocks("lignite_block",()->new Block(BlockBehaviour.Properties.of().strength(1.5F,6.0F)
                    .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> LAPIS_INGOT_BLOCK =
            registerBlocks("lapis_ingot_block",()->new Block(BlockBehaviour.Properties.of().strength(2.5F,6.0F)
                    .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> REDSTONE_INGOT_BLOCK =
            registerBlocks("redstone_ingot_block",()->new Block(BlockBehaviour.Properties.of().strength(2.5F,6.0F)
                    .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> EPHEMERAL_ORE =
            registerBlocks("ephemeral_ore",()->new Block(BlockBehaviour.Properties.of().strength(1.0F,10.0F)
                    .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CESIUM_SALT_ORE =
            registerBlocks("cesium_salt_ore",()->new Block(BlockBehaviour.Properties.of().strength(1.0F,6.0F)
                    .requiresCorrectToolForDrops()));
    /* ==================== 钫盐系列（辐射材料） ==================== */

    /** 钫盐矿：地狱上层（Y>90）稀有的碱金属矿，由世界生成保证不露天，见 radiation/README.md。 */
    public static final DeferredBlock<Block> FRANCIUM_SALT_ORE =
            registerBlocks("francium_salt_ore", () -> new Block(BlockBehaviour.Properties.of()
                    .strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
    /** 钫盐块：9 份钫盐压成一块。 */
    public static final DeferredBlock<Block> FRANCIUM_SALT_BLOCK =
            registerBlocks("francium_salt_block", () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
    /** 钫块：9 枚钫锭压成一块。 */
    public static final DeferredBlock<Block> FRANCIUM_BLOCK =
            registerBlocks("francium_block", () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2.5F, 6.0F).requiresCorrectToolForDrops()));

    public static final DeferredBlock<ReinforcedTNTBlock> INTENSIFIED_TNT =
            registerBlocks("intensified_tnt",()->new ReinforcedTNTBlock(BlockBehaviour.Properties.of().strength(0.0F,0.0F)
                    ));
    public static final DeferredBlock<MindFluidBlock> MIND_FLUID_BLOCK = registerBlockOnly(
            "mind_fluid_block",
            () -> new MindFluidBlock(
                    ModFluids.Fluids.MIND_FLUID.get(),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_CYAN)
                            .replaceable()
                            .strength(100.0f)
                            .lightLevel(state -> 2)
                            .pushReaction(PushReaction.DESTROY)
                            .noLootTable()
                            .liquid()
                            .sound(SoundType.EMPTY)
            )
    );
        public static final DeferredBlock<Block> CESIUM_SALT_BLOCK =
            registerBlocks("cesium_salt_block", () ->new Block(BlockBehaviour.Properties.of()
                    .strength(2.0F, 6.0F).requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CESIUM_BLOCK =
            registerBlocks("cesium_block", () ->new Block(BlockBehaviour.Properties.of()
                    .strength(2.5F, 6.0F).requiresCorrectToolForDrops()));
        public static final DeferredBlock<EmpoweredTNTBlock> EMPOWERED_TNT =
            BLOCKS.register("empowered_tnt", () -> new EmpoweredTNTBlock(
                    BlockBehaviour.Properties.of().strength(0.0F, 0.0F)
            ));

    /** 玄武岩合金块：注入结构用的 1 级底座，方块等级由 injection_tier_1 标签声明。 */
    public static final DeferredBlock<Block> BASALT_ALLOY_BLOCK =
            registerBlocks("basalt_alloy_block", () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3.0F, 9.0F).requiresCorrectToolForDrops()));

    /** 幽匿合金块：注入结构用的 2 级底座，方块等级由 injection_tier_2 标签声明。 */
    public static final DeferredBlock<Block> SCULK_ALLOY_BLOCK =
            registerBlocks("sculk_alloy_block", () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .sound(SoundType.SCULK)
                    .strength(4.0F, 12.0F).requiresCorrectToolForDrops()));

    /* ==================== 思维流体反应生成的四类方块 ==================== */

    /** 稀释灵感结晶：流动的思维流体遇到水。半透明，需要镐。 */
    public static final DeferredBlock<DilutedInspirationCrystalBlock> DILUTED_INSPIRATION_CRYSTAL =
            registerBlocks("diluted_inspiration_crystal", () -> new DilutedInspirationCrystalBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_LIGHT_GREEN)
                            .strength(1.5F, 3.0F)
                            .sound(SoundType.GLASS)
                            .noOcclusion()
                            .requiresCorrectToolForDrops()));

    /** 浓缩灵感结晶：思维流体源遇到水。几乎不透明，比稀释结晶更硬。 */
    public static final DeferredBlock<ConcentratedInspirationCrystalBlock> CONCENTRATED_INSPIRATION_CRYSTAL =
            registerBlocks("concentrated_inspiration_crystal", () -> new ConcentratedInspirationCrystalBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_GREEN)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.GLASS)
                            .noOcclusion()
                            .requiresCorrectToolForDrops()));

    /** 灵感凝胶：流动的思维流体遇到岩浆。属性对齐原版粘液块，行为由父类提供。 */
    public static final DeferredBlock<InspirationGelBlock> INSPIRATION_GEL =
            registerBlocks("inspiration_gel", () -> new InspirationGelBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_LIGHT_GREEN)
                            .friction(0.8F)
                            .strength(0.6F)
                            .sound(SoundType.SLIME_BLOCK)
                            .noOcclusion()));

    /** 思维水晶岩：思维流体源遇到岩浆。需要镐，掉落思维晶体。 */
    public static final DeferredBlock<MindCrystalRockBlock> MIND_CRYSTAL_ROCK =
            registerBlocks("mind_crystal_rock", () -> new MindCrystalRockBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .strength(4.0F, 9.0F)
                            .sound(SoundType.DEEPSLATE)
                            .requiresCorrectToolForDrops()));

        public static final DeferredItem<Item> EMPOWERED_TNT_ITEM =
            ModItems.ITEMS.register("empowered_tnt", () -> new AlkaliBlockItem(
                    EMPOWERED_TNT.get(),
                    new Item.Properties(),
                    ModItems.CESIUM_SALT.get(),
                    6.0f
            ));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
