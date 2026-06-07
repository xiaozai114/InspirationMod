package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
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
        simpleBlock(ModBlocks.MIND_FLUID_BLOCK.get(), cubeAll(ModBlocks.MIND_FLUID_BLOCK.get()));
        simpleBlock(ModBlocks.CESIUM_SALT_BLOCK.get(), cubeAll(ModBlocks.CESIUM_SALT_BLOCK.get()));
        simpleBlock(ModBlocks.CESIUM_BLOCK.get(), cubeAll(ModBlocks.CESIUM_BLOCK.get()));
        simpleBlock(ModBlocks.JADE_STEEL_ORE.get(), cubeAll(ModBlocks.JADE_STEEL_ORE.get()));
        simpleBlock(ModBlocks.DEEPSLATE_JADE_STEEL_ORE.get(), cubeAll(ModBlocks.DEEPSLATE_JADE_STEEL_ORE.get()));
        IntensifiedTNTBlock();
        EmpoweredTNTBlock();
    }

    private void IntensifiedTNTBlock() {
        // 1. 定义纹理：假设你已经把纹理图片放在src/main/resources/assets/your_modid/textures/block/目录下
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.TOP, modLoc("block/intensified_tnt_block_top_bottom")) // 顶部纹理
                .put(TextureSlot.BOTTOM, modLoc("block/intensified_tnt_block_top_bottom")) // 底部纹理，此处与顶部相同
                .put(TextureSlot.SIDE, modLoc("block/intensified_tnt_block_block_side"));       // 侧面纹理

        // 2. 使用cubeBottomTop模型框架生成模型文件
        ModelFile modelFile = models().cubeBottomTop(
                "intensified_tnt",                       // 模型名称
                textures.get(TextureSlot.SIDE),          // 侧面纹理
                textures.get(TextureSlot.BOTTOM),        // 底部纹理
                textures.get(TextureSlot.TOP)            // 顶部纹理
        );

        // 3. 生成方块状态文件和物品模型
        simpleBlock(ModBlocks.INTENSIFIED_TNT.get(), modelFile);
        simpleBlockItem(ModBlocks.INTENSIFIED_TNT.get(), modelFile);
    }



    private void EmpoweredTNTBlock() {
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.TOP, modLoc("block/empowered_tnt_block_top_bottom"))
                .put(TextureSlot.BOTTOM, modLoc("block/empowered_tnt_block_top_bottom"))
                .put(TextureSlot.SIDE, modLoc("block/empowered_tnt_block_side"));

        ModelFile modelFile = models().cubeBottomTop(
                "empowered_tnt",
                textures.get(TextureSlot.SIDE),
                textures.get(TextureSlot.BOTTOM),
                textures.get(TextureSlot.TOP)
        );

        simpleBlock(ModBlocks.EMPOWERED_TNT.get(), modelFile);
        simpleBlockItem(ModBlocks.EMPOWERED_TNT.get(), modelFile);
    }
}