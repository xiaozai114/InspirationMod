package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.tags.ItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Inspiration.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ItemTags.HEAD_ARMOR).add(ModItems.ENERGIZED_JADE_STEEL_HELMET.get());
        tag(ItemTags.CHEST_ARMOR).add(ModItems.ENERGIZED_JADE_STEEL_CHESTPLATE.get());
        tag(ItemTags.LEG_ARMOR).add(ModItems.ENERGIZED_JADE_STEEL_LEGGINGS.get());
        tag(ItemTags.FOOT_ARMOR).add(ModItems.ENERGIZED_JADE_STEEL_BOOTS.get());

    }
}
