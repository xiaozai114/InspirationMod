package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.machine.recipe.TwistingRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * 矿物扭曲机配方：矿石 -> 对应的粗矿。
 * <p>
 * 产出数量由方块实体按等效时运掷出，配方里只声明基准产量与等效时运等级。
 */
public final class TwistingRecipes {

    /** 扭曲配方的档位：矿石越稀有，耗时与能耗越高。 */
    private record TwistingTier(int duration, int energyPerTick) {
    }

    private static final TwistingTier COMMON_ORE = new TwistingTier(400, 300);
    private static final TwistingTier RARE_ORE = new TwistingTier(600, 400);

    /**
     * 一种矿石的扭曲配方。
     *
     * @param name      用于生成配方 ID 的后缀
     * @param result    扭出的粗矿
     * @param baseCount 基准产量，实际产量再乘以等效时运的倍率
     * @param ores      该矿族的全部矿石方块（含深板岩变种）
     */
    private record OreSet(String name, Item result, int baseCount, TwistingTier tier, List<Item> ores) {
    }

    private TwistingRecipes() {
    }

    public static void build(RecipeOutput recipeOutput) {
        List<OreSet> sets = List.of(
                new OreSet("coal", Items.COAL, 1, COMMON_ORE,
                        List.of(Items.COAL_ORE, Items.DEEPSLATE_COAL_ORE)),
                new OreSet("iron", Items.RAW_IRON, 1, COMMON_ORE,
                        List.of(Items.IRON_ORE, Items.DEEPSLATE_IRON_ORE)),
                new OreSet("copper", Items.RAW_COPPER, 1, COMMON_ORE,
                        List.of(Items.COPPER_ORE, Items.DEEPSLATE_COPPER_ORE)),
                new OreSet("gold", Items.RAW_GOLD, 1, RARE_ORE,
                        List.of(Items.GOLD_ORE, Items.DEEPSLATE_GOLD_ORE)),
                // 红石与青金石在原版就是成簇掉落，基准产量取原版下限
                new OreSet("redstone", Items.REDSTONE, 4, COMMON_ORE,
                        List.of(Items.REDSTONE_ORE, Items.DEEPSLATE_REDSTONE_ORE)),
                new OreSet("lapis", Items.LAPIS_LAZULI, 4, COMMON_ORE,
                        List.of(Items.LAPIS_ORE, Items.DEEPSLATE_LAPIS_ORE)),
                new OreSet("diamond", Items.DIAMOND, 1, RARE_ORE,
                        List.of(Items.DIAMOND_ORE, Items.DEEPSLATE_DIAMOND_ORE)),
                new OreSet("emerald", Items.EMERALD, 1, RARE_ORE,
                        List.of(Items.EMERALD_ORE, Items.DEEPSLATE_EMERALD_ORE)),
                new OreSet("nether_gold", Items.GOLD_NUGGET, 2, RARE_ORE,
                        List.of(Items.NETHER_GOLD_ORE)),
                new OreSet("nether_quartz", Items.QUARTZ, 1, COMMON_ORE,
                        List.of(Items.NETHER_QUARTZ_ORE)),
                new OreSet("cesium_salt", ModItems.RAW_CESIUM_SALT.get(), 1, COMMON_ORE,
                        List.of(ModBlocks.CESIUM_SALT_ORE.get().asItem())),
                // 钫盐矿：地狱上层稀有矿，产量基准同铯盐
                new OreSet("francium_salt", ModItems.RAW_FRANCIUM_SALT.get(), 1, COMMON_ORE,
                        List.of(ModBlocks.FRANCIUM_SALT_ORE.get().asItem())));

        for (OreSet set : sets) {
            recipeOutput.accept(
                    ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "twisting/" + set.name()),
                    new TwistingRecipe(Ingredient.of(set.ores().toArray(new Item[0])),
                            new ItemStack(set.result(), set.baseCount()),
                            TwistingRecipe.DEFAULT_FORTUNE_LEVEL,
                            set.tier().duration(), set.tier().energyPerTick()),
                    null);
        }
    }
}
