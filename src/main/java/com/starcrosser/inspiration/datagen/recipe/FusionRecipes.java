package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.machine.recipe.FusionRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * 物质融合机的专属配方。
 * <p>
 * 材料种类为 3 的配方只有**强能**机型（三个输入槽）做得了，老机型凑不齐；
 * 每种材料还能单独声明消耗数量（见 {@link FusionRecipe}）。
 */
public final class FusionRecipes {

    private FusionRecipes() {
    }

    public static void build(RecipeOutput recipeOutput) {
        // 绿宝石 + 铁锭 -> 翡翠钢锭
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "jade_steel_ingot_from_fusion"),
                new FusionRecipe(Ingredient.of(Items.EMERALD), Ingredient.of(Items.IRON_INGOT),
                        new ItemStack(ModItems.JADE_STEEL_INGOT.get(), 2), 200, 40),
                null);

        // 玄武岩 + 远古残骸 -> 玄武岩合金
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "basalt_alloy_from_fusion"),
                new FusionRecipe(Ingredient.of(Items.BASALT), Ingredient.of(Items.ANCIENT_DEBRIS),
                        new ItemStack(ModItems.BASALT_ALLOY.get(), 2), 400, 120),
                null);

        // 思维晶体 + 紫水晶 + 下界石英 -> 思维矩阵锭（三材料配方，只有强能机型能做）
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "mind_matrix_ingot_from_fusion"),
                new FusionRecipe(Ingredient.of(ModItems.MIND_CRYSTAL.get()),
                        Ingredient.of(Items.AMETHYST_SHARD),
                        new ItemStack(ModItems.MIND_MATRIX_INGOT.get()),
                        600, 300,
                        Ingredient.of(Items.QUARTZ)),
                null);

        // 4 黑曜石 + 末影珍珠 + 4 紫珀块 -> 末地合金
        // 三种材料各自声明消耗数量（4/1/4），同样只有三槽的强能机型能凑齐
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "end_alloy_from_fusion"),
                new FusionRecipe(Ingredient.of(Items.OBSIDIAN), 4,
                        Ingredient.of(Items.ENDER_PEARL), 1,
                        Ingredient.of(Items.PURPUR_BLOCK), 4,
                        new ItemStack(ModItems.END_ALLOY.get()),
                        800, 600),
                null);

        // 回响碎片 + 强化深板岩 -> 幽匿合金 ×2
        // 强化深板岩只有地核钻探者能挖，所以这是中期之后才做得出来的材料
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "sculk_alloy_from_fusion"),
                new FusionRecipe(Ingredient.of(Items.ECHO_SHARD),
                        Ingredient.of(Items.REINFORCED_DEEPSLATE),
                        new ItemStack(ModItems.SCULK_ALLOY.get(), 2), 400, 200),
                null);

        // 焦煤 + 钙粉 -> 还原粉 ×1
        // 工作台那条（焦煤 + 钙粉 + 灵感 -> 3 个）产量更高但要吃灵感；这条是「不耗灵感」的替代路线，
        // 代价是慢且只出 1 个。两槽机型就能做。
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "reduction_powder_from_fusion"),
                new FusionRecipe(Ingredient.of(ModItems.COKE.get()),
                        Ingredient.of(ModItems.CALCIUM_DUST.get()),
                        new ItemStack(ModItems.REDUCTION_POWDER.get()), 200, 40),
                null);
    }
}
