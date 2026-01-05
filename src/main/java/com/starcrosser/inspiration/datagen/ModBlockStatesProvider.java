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
        simpleBlock(ModBlocks.LIGNITE_BLOCK.get(), cubeAll(ModBlocks.LIGNITE_BLOCK.get()));
    }
}
