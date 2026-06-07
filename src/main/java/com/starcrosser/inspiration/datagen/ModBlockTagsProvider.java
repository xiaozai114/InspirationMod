package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Inspiration.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 镐子可挖掘
        // 铯盐块/铯块/翡翠钢矿石
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.CESIUM_SALT_BLOCK.get())
                .add(ModBlocks.CESIUM_BLOCK.get())
                .add(ModBlocks.JADE_STEEL_ORE.get())
                .add(ModBlocks.DEEPSLATE_JADE_STEEL_ORE.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.LIGNITE_BLOCK.get())
                .add(ModBlocks.EPHEMERAL_ORE.get())
                .add(ModBlocks.LAPIS_INGOT_BLOCK.get())
                .add(ModBlocks.CESIUM_SALT_ORE.get())
                .add(ModBlocks.REDSTONE_INGOT_BLOCK.get());

        // 挖掘等级：石镐及以上
        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.LIGNITE_BLOCK.get())
                .add(ModBlocks.CESIUM_SALT_ORE.get());

        // 挖掘等级：铁镐及以上
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.LAPIS_INGOT_BLOCK.get())
                .add(ModBlocks.REDSTONE_INGOT_BLOCK.get());

        // 挖掘等级：钻石镐及以上
        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.EPHEMERAL_ORE.get())
                .add(ModBlocks.JADE_STEEL_ORE.get())
                .add(ModBlocks.DEEPSLATE_JADE_STEEL_ORE.get());
    }
}