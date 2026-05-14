package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStatesProvider extends BlockStateProvider {

    public ModBlockStatesProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Inspiration.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // 褐煤块
        simpleBlock(ModBlocks.LIGNITE_BLOCK.get(), cubeAll(ModBlocks.LIGNITE_BLOCK.get()));
        // 红石锭块
        simpleBlock(ModBlocks.REDSTONE_INGOT_BLOCK.get(), cubeAll(ModBlocks.REDSTONE_INGOT_BLOCK.get()));
        // 青金石锭块
        simpleBlock(ModBlocks.LAPIS_INGOT_BLOCK.get(), cubeAll(ModBlocks.LAPIS_INGOT_BLOCK.get()));
        // 无恒矿石
        simpleBlock(ModBlocks.EPHEMERAL_ORE.get(), cubeAll(ModBlocks.EPHEMERAL_ORE.get()));
        simpleBlock(ModBlocks.CESIUM_SALT_ORE.get(), cubeAll(ModBlocks.CESIUM_SALT_ORE.get()));
    }
}