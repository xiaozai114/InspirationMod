package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class ModBlockLootTablesProvider extends BlockLootSubProvider {
    public ModBlockLootTablesProvider( HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.LIGNITE_BLOCK.get());
        dropSelf(ModBlocks.LAPIS_INGOT_BLOCK.get());
        dropSelf(ModBlocks.INTENSIFIED_TNT.get());
        dropSelf(ModBlocks.CESIUM_SALT_BLOCK.get());
        dropSelf(ModBlocks.CESIUM_BLOCK.get());
        dropOther(ModBlocks.JADE_STEEL_ORE.get(), ModItems.ROUGH_JADE_STEEL.get());
        dropOther(ModBlocks.DEEPSLATE_JADE_STEEL_ORE.get(), ModItems.ROUGH_JADE_STEEL.get());
        dropSelf(ModBlocks.EMPOWERED_TNT.get());
        dropSelf(ModBlocks.REDSTONE_INGOT_BLOCK.get());

        dropOther(ModBlocks.EPHEMERAL_ORE.get(), ModItems.EPHEMERAL_MATERIAL.get());
        dropOther(ModBlocks.CESIUM_SALT_ORE.get(), ModItems.RAW_CESIUM_SALT.get());

    }
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
