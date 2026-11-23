package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.injection.recipe.InjectionRecipe;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.material.ModMaterials;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * 注入配方：基底物质在物质束缚器上，注入物质在注入枪上。
 * <p>
 * 能量窗口是本机制的核心参数 —— 放电量低于下限白费材料，高于上限直接爆炸，
 * 因此每条配方的上下限都要和「玩家可能拿到多少电」一起考虑。
 */
public final class InjectionRecipes {

    private InjectionRecipes() {
    }

    public static void build(RecipeOutput recipeOutput) {
        // 翡翠钢锭 + 灵感 -> 注能翡翠钢（1 级：玄武岩合金块底座）
        injection(recipeOutput, "injection/energized_jade_steel",
                Ingredient.of(ModMaterials.JADE_STEEL.item().get()),
                Ingredient.of(ModItems.INSPIRATION.get()),
                1, 50_000, 150_000,
                new ItemStack(ModMaterials.ENERGIZED_JADE_STEEL.item().get()));

        // 注能红石块 + 铀锭 -> 压缩红石锭（2 级；窗口 1M~2M FE）
        injection(recipeOutput, "injection/compressed_redstone_ingot",
                Ingredient.of(ModMaterials.ENERGIZED_REDSTONE.block().get()),
                Ingredient.of(ModMaterials.URANIUM.item().get()),
                2, 1_000_000, 2_000_000,
                new ItemStack(ModItems.COMPRESSED_REDSTONE_INGOT.get()));

        // 玻璃 + 虚空露珠 -> 虚空护目镜（2 级；窗口 100k~125k FE）
        injection(recipeOutput, "injection/void_goggles",
                Ingredient.of(Items.GLASS),
                Ingredient.of(ModItems.VOID_DEW.get()),
                2, 100_000, 125_000,
                new ItemStack(ModItems.VOID_GOGGLES.get()));

        // 末地合金 + 虚空露珠 -> 虚空合金（2 级；枪里必须恰好 1 FE）
        injection(recipeOutput, "injection/void_alloy",
                Ingredient.of(ModMaterials.END_ALLOY.item().get()),
                Ingredient.of(ModItems.VOID_DEW.get()),
                2, 1, 1,
                new ItemStack(ModItems.VOID_ALLOY.get()));

        // 玄武岩基底 + 下界合金碎片 -> 3 玄武岩合金
        injection(recipeOutput, "injection/basalt_alloy",
                Ingredient.of(ModItems.BASALT_BASE.get()),
                Ingredient.of(Items.NETHERITE_SCRAP),
                1, 25_000, 75_000,
                new ItemStack(ModMaterials.BASALT_ALLOY.item().get(), 3));

        // 末地合金 + 无恒物质 -> 无恒合金
        // 这条不写死能量：窗口由存档种子随机决定（见 SeededEnergyWindow），JEI 里只显示 ???
        seededInjection(recipeOutput, "injection/ephemeral_alloy",
                Ingredient.of(ModMaterials.END_ALLOY.item().get()),
                Ingredient.of(ModItems.EPHEMERAL_MATERIAL.get()),
                1, new ItemStack(ModMaterials.EPHEMERAL_ALLOY.item().get()));

        // 打火石 + 玄武岩合金 -> 玄武岩合金打火石
        // 窗口很窄（25k~50k），逼玩家把注入枪精确充到这一档再用
        injection(recipeOutput, "injection/basalt_alloy_firestriker",
                Ingredient.of(Items.FLINT_AND_STEEL),
                Ingredient.of(ModMaterials.BASALT_ALLOY.item().get()),
                1, 25_000, 50_000,
                new ItemStack(ModItems.BASALT_ALLOY_FIRESTRIKER.get()));

        // 盾牌 + 末地合金 -> 末地合金盾牌：目前的最高档注入，窗口 500k~1500k
        injection(recipeOutput, "injection/end_alloy_shield",
                Ingredient.of(Items.SHIELD),
                Ingredient.of(ModMaterials.END_ALLOY.item().get()),
                1, 500_000, 1_500_000,
                new ItemStack(ModItems.END_ALLOY_SHIELD.get()));

        // 青金石锭 + 灵感 -> 注能青金石锭：窗口很窄（10k~15k），高能矿物扭曲机的材料
        injection(recipeOutput, "injection/energized_lapis_ingot",
                Ingredient.of(ModMaterials.LAPIS.item().get()),
                Ingredient.of(ModItems.INSPIRATION.get()),
                1, 10_000, 15_000,
                new ItemStack(ModMaterials.ENERGIZED_LAPIS.item().get()));

        // 等离子锭 + 灵感 -> 注能等离子锭：目前窗口最高的一条（1M~2M），
        // 必须靠电容插件把注入枪的容量堆上去才够得着
        injection(recipeOutput, "injection/energized_plasma_ingot",
                Ingredient.of(ModMaterials.PLASMA.item().get()),
                Ingredient.of(ModItems.INSPIRATION.get()),
                1, 1_000_000, 2_000_000,
                new ItemStack(ModMaterials.ENERGIZED_PLASMA.item().get()));
        // 注能红石锭：红石锭 ← 灵感（150k~300k FE），高能物质重塑机的材料
        injection(recipeOutput, "injection/energized_redstone_ingot",
                Ingredient.of(ModMaterials.REDSTONE.item().get()),
                Ingredient.of(ModItems.INSPIRATION.get()),
                1, 150_000, 300_000,
                new ItemStack(ModMaterials.ENERGIZED_REDSTONE.item().get()));

        // 铁锭 + 思维矩阵锭 -> 结构投影仪：窗口 50k~150k FE
        injection(recipeOutput, "injection/projector",
                Ingredient.of(Items.IRON_INGOT),
                Ingredient.of(ModMaterials.MIND_MATRIX.item().get()),
                1, 50_000, 150_000,
                new ItemStack(ModItems.PROJECTOR.get()));

        // 翡翠钢升级模板 + 注能翡翠钢 -> 注能翡翠钢升级模板
        // 本条是**二级配方**：束缚器下方必须换成幽匿合金块，玄武岩合金底座做不了
        injection(recipeOutput, "injection/energized_jade_steel_upgrade_template",
                Ingredient.of(ModItems.JADE_STEEL_UPGRADE_TEMPLATE.get()),
                Ingredient.of(ModMaterials.ENERGIZED_JADE_STEEL.item().get()),
                2, 500_000, 1_000_000,
                new ItemStack(ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get()));

        // 盖革计数器：铁锭（束缚器）注入铯锭（注入枪）
        // 能量窗口 150k~250k FE —— 下限很高，逼玩家先把注入枪充满再动手
        injection(recipeOutput, "injection/geiger_counter",
                Ingredient.of(Items.IRON_INGOT),
                Ingredient.of(ModMaterials.CESIUM.item().get()),
                1, 150_000, 250_000,
                new ItemStack(ModItems.GEIGER_COUNTER.get()));

        injection(recipeOutput, "injection/mechanical_parasite",
                Ingredient.of(ModItems.PARASITE.get()),
                Ingredient.of(ModMaterials.EPHEMERAL_ALLOY.item().get()),
                1, 10_000, 20_000,
                new ItemStack(ModItems.MECHANICAL_PARASITE.get()));
    }

    private static void injection(RecipeOutput recipeOutput, String path, Ingredient base, Ingredient inject,
                                  int tier, int minEnergy, int maxEnergy, ItemStack result) {
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, path),
                new InjectionRecipe(base, inject, tier, minEnergy, maxEnergy,
                        result, InjectionRecipe.DEFAULT_DURATION),
                null);
    }

    /**
     * 种子随机窗口的注入配方：JSON 里不写 {@code min_energy} / {@code max_energy}，
     * 实际窗口由存档种子决定，玩家只能用世界数据探测器读。
     */
    private static void seededInjection(RecipeOutput recipeOutput, String path, Ingredient base, Ingredient inject,
                                        int tier, ItemStack result) {
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, path),
                new InjectionRecipe(base, inject, result, tier, InjectionRecipe.DEFAULT_DURATION),
                null);
    }
}
