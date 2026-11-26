package com.starcrosser.inspiration.datagen.recipe;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.fluid.ModFluids;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.machine.recipe.AssemblingRecipe;
import com.starcrosser.inspiration.machine.recipe.FluidRequirement;
import com.starcrosser.inspiration.multiblock.data.StructureSite;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

/** Assemble a reusable card from a blank and samples of the site it represents. */
public final class SimulatorCardRecipes {
    private SimulatorCardRecipes() {}

    public static void build(RecipeOutput output) {
        card(output, ModItems.NETHER_DATA.get(),
                List.of(new FluidRequirement(FluidIngredient.of(Fluids.LAVA), 16_000)),
                sample(Items.NETHERRACK, 64), sample(Items.BASALT, 64),
                sample(Items.BLACKSTONE, 64), sample(Items.SOUL_SAND, 64));
        card(output, ModItems.END_DATA.get(),
                List.of(new FluidRequirement(FluidIngredient.of(ModFluids.Fluids.VOID_FLUID.get()), 16_000)),
                sample(Items.END_STONE, 64), sample(Items.OBSIDIAN, 64), sample(Items.PURPUR_BLOCK, 64));

        structure(output, StructureSite.MINESHAFT,
                sample(Items.RAIL, 32),
                sample(Items.OAK_FENCE, 16),
                sample(Items.COBWEB, 4));
        structure(output, StructureSite.MONSTER_ROOM,
                sample(Items.MOSSY_COBBLESTONE, 32),
                sample(Items.BONE, 16),
                sample(Items.ROTTEN_FLESH, 16));
        structure(output, StructureSite.DESERT_PYRAMID,
                sample(Items.CUT_SANDSTONE, 32),
                sample(Items.ORANGE_TERRACOTTA, 16),
                sample(Items.TNT, 4),
                sample(Items.BRUSH, 1));
        structure(output, StructureSite.DESERT_WELL,
                sample(Items.SANDSTONE, 32),
                sample(Items.SANDSTONE_SLAB, 16),
                sample(Items.BRUSH, 1));
        structure(output, StructureSite.JUNGLE_TEMPLE,
                sample(Items.MOSSY_COBBLESTONE, 32),
                sample(Items.TRIPWIRE_HOOK, 4),
                sample(Items.DISPENSER, 2));
        structure(output, StructureSite.IGLOO,
                sample(Items.SNOW_BLOCK, 32),
                sample(Items.WHITE_BED, 1),
                sample(Items.GOLDEN_APPLE, 1));
        structure(output, StructureSite.OCEAN_RUIN,
                sample(Items.GRAVEL, 32),
                sample(Items.SAND, 32),
                sample(Items.PRISMARINE, 16),
                sample(Items.BRUSH, 1));
        structure(output, StructureSite.TRAIL_RUINS,
                sample(Items.TERRACOTTA, 32),
                sample(Items.MUD_BRICKS, 32),
                sample(Items.GRAVEL, 16),
                sample(Items.BRUSH, 1));
        structure(output, StructureSite.BURIED_TREASURE,
                sample(Items.SAND, 64),
                sample(Items.CHEST, 1),
                sample(Items.HEART_OF_THE_SEA, 1));
        structure(output, StructureSite.VILLAGE,
                sample(Items.HAY_BLOCK, 16),
                sample(Items.EMERALD, 8),
                sample(Items.BELL, 1));
        structure(output, StructureSite.PILLAGER_OUTPOST,
                sample(Items.DARK_OAK_LOG, 32),
                sample(Items.CROSSBOW, 1),
                sample(Items.WHITE_BANNER, 1));
        structure(output, StructureSite.STRONGHOLD,
                sample(Items.STONE_BRICKS, 64),
                sample(Items.BOOKSHELF, 16),
                sample(Items.ENDER_EYE, 4));
        structure(output, StructureSite.WOODLAND_MANSION,
                sample(Items.DARK_OAK_PLANKS, 64),
                sample(Items.BOOKSHELF, 16),
                sample(Items.TOTEM_OF_UNDYING, 1));
        structure(output, StructureSite.ANCIENT_CITY,
                sample(Items.DEEPSLATE_TILES, 64),
                sample(Items.SCULK, 32),
                sample(Items.ECHO_SHARD, 2));
        structure(output, StructureSite.RUINED_PORTAL,
                sample(Items.OBSIDIAN, 16),
                sample(Items.CRYING_OBSIDIAN, 4),
                sample(Items.GOLD_INGOT, 8));
        structure(output, StructureSite.NETHER_FORTRESS,
                sample(Items.NETHER_BRICKS, 64),
                sample(Items.BLAZE_ROD, 8),
                sample(Items.NETHER_WART, 16));
        structure(output, StructureSite.BASTION_REMNANT,
                sample(Items.POLISHED_BLACKSTONE_BRICKS, 64),
                sample(Items.GILDED_BLACKSTONE, 8),
                sample(Items.GOLD_BLOCK, 4));
        structure(output, StructureSite.END_CITY,
                sample(Items.PURPUR_BLOCK, 64),
                sample(Items.END_ROD, 16),
                sample(Items.SHULKER_SHELL, 2));
        structure(output, StructureSite.TRIAL_CHAMBERS,
                sample(Items.TUFF_BRICKS, 64),
                sample(Items.COPPER_GRATE, 16),
                sample(Items.BREEZE_ROD, 2));
        structure(output, StructureSite.TRIAL_VAULT,
                sample(Items.TUFF_BRICKS, 32),
                sample(Items.CHISELED_COPPER, 8),
                sample(Items.TRIAL_KEY, 1));
        structure(output, StructureSite.OMINOUS_VAULT,
                sample(Items.POLISHED_TUFF, 32),
                sample(Items.CHISELED_COPPER, 8),
                sample(Items.OMINOUS_TRIAL_KEY, 1));
    }

    private static void structure(RecipeOutput output, StructureSite site, ItemStack... samples) {
        card(output, ModItems.STRUCTURE_DATA.get(site).get(), List.of(), samples);
    }

    private static ItemStack sample(ItemLike item, int count) {
        return new ItemStack(item, count);
    }

    private static void card(RecipeOutput output, Item result,
                             List<FluidRequirement> fluids, ItemStack... samples) {
        var inputs = new ArrayList<Ingredient>();
        var counts = new ArrayList<Integer>();
        inputs.add(Ingredient.of(ModItems.BLANK_DATA.get()));
        counts.add(1);
        for (ItemStack sample : samples) {
            inputs.add(Ingredient.of(sample.getItem()));
            counts.add(sample.getCount());
        }
        var id = BuiltInRegistries.ITEM.getKey(result);
        output.accept(ResourceLocation.fromNamespaceAndPath(Inspiration.MODID, "assembling/" + id.getPath()),
                new AssemblingRecipe(inputs, counts, fluids, new ItemStack(result), 600, 400), null);
    }
}
