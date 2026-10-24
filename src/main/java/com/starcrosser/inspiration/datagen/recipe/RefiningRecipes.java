package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.machine.recipe.RefiningRecipe;
import com.starcrosser.inspiration.material.ModMaterials;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * 原料精炼机的配方：一份原料 → 一份主产物 + 最多两份副产物。
 * <p>
 * 副产物按「槽位顺序」写进 {@code byproducts} 数组：第 0 项是机器第 2 个输出槽，
 * 第 1 项是第 3 个输出槽。想只给一份副产物就只写一项。
 * <p>
 * 副产物的量级规则（与「9 粒 = 1 锭」一致）：主产物给锭 / 盐，副产物给粒或尘 ——
 * 一份粗料里那点杂质本来就不该凑成一整锭。
 */
public final class RefiningRecipes {

    /** 核废料比粗矿难拆得多，单独给一份更重的耗时与耗电。 */
    private static final int NUCLEAR_DURATION = 400;
    private static final int NUCLEAR_ENERGY_PER_TICK = 320;

    private RefiningRecipes() {
    }

    public static void build(RecipeOutput recipeOutput) {
        ItemStack nickelNugget = new ItemStack(ModMaterials.NICKEL.nugget().get());
        ItemStack uraniumNugget = new ItemStack(ModMaterials.URANIUM.nugget().get());
        ItemStack thoriumNugget = new ItemStack(ModMaterials.THORIUM.nugget().get());
        ItemStack rubidiumNugget = new ItemStack(ModMaterials.RUBIDIUM.nugget().get());
        ItemStack actiniumNugget = new ItemStack(ModMaterials.ACTINIUM.nugget().get());
        ItemStack neptuniumGrit = new ItemStack(ModMaterials.NEPTUNIUM.grit().get());
        ItemStack plutoniumGrit = new ItemStack(ModMaterials.PLUTONIUM.grit().get());

        // ===== 原版粗矿：主产物是锭，副产物是别的金属的粒 =====

        refine(recipeOutput, "raw_iron", Ingredient.of(Items.RAW_IRON),
                new ItemStack(Items.IRON_INGOT), List.of(nickelNugget));
        refine(recipeOutput, "raw_copper", Ingredient.of(Items.RAW_COPPER),
                new ItemStack(Items.COPPER_INGOT), List.of(new ItemStack(Items.GOLD_NUGGET)));
        refine(recipeOutput, "raw_gold", Ingredient.of(Items.RAW_GOLD),
                new ItemStack(Items.GOLD_INGOT), List.of(new ItemStack(Items.IRON_NUGGET)));

        // ===== 本模组的两种盐：粗盐先精炼成盐，盐再精炼成锭 =====

        // 粗钫盐 -> 钫盐 + 铀粒 + 钍粒（两份副产物，都带辐射）
        refine(recipeOutput, "raw_francium_salt", Ingredient.of(ModItems.RAW_FRANCIUM_SALT.get()),
                new ItemStack(ModItems.FRANCIUM_SALT.get()), List.of(uraniumNugget, thoriumNugget));
        // 粗铯盐 -> 铯盐 + 铷粒（铷与铯同源，碱性弱一档）
        refine(recipeOutput, "raw_cesium_salt", Ingredient.of(ModItems.RAW_CESIUM_SALT.get()),
                new ItemStack(ModItems.CESIUM_SALT.get()), List.of(rubidiumNugget));
        // 铯盐 -> 铯锭 + 铀粒
        refine(recipeOutput, "cesium_salt", Ingredient.of(ModItems.CESIUM_SALT.get()),
                new ItemStack(ModMaterials.CESIUM.item().get()), List.of(uraniumNugget));
        // 钫盐 -> 钫锭 + 锕粒
        refine(recipeOutput, "francium_salt", Ingredient.of(ModItems.FRANCIUM_SALT.get()),
                new ItemStack(ModMaterials.FRANCIUM.item().get()), List.of(actiniumNugget));

        // ===== 核废料：主产物铀锭，副产物是两个更重的锕系元素（尘） =====
        refine(recipeOutput, "nuclear_waste", Ingredient.of(ModItems.NUCLEAR_WASTE.get()),
                new ItemStack(ModMaterials.URANIUM.item().get()),
                List.of(neptuniumGrit, plutoniumGrit), NUCLEAR_DURATION, NUCLEAR_ENERGY_PER_TICK);
    }

    /** 走默认耗时与耗电。 */
    private static void refine(RecipeOutput recipeOutput, String id, Ingredient input,
                               ItemStack result, List<ItemStack> byproducts) {
        refine(recipeOutput, id, input, result, byproducts,
                RefiningRecipe.DEFAULT_DURATION, RefiningRecipe.DEFAULT_ENERGY_PER_TICK);
    }

    private static void refine(RecipeOutput recipeOutput, String id, Ingredient input, ItemStack result,
                               List<ItemStack> byproducts, int duration, int energyPerTick) {
        recipeOutput.accept(
                ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "refining/" + id),
                new RefiningRecipe(input, 1, result, byproducts, duration, energyPerTick),
                null);
    }
}
