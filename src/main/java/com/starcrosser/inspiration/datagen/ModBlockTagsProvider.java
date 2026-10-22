package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.injection.InjectionTiers;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import com.starcrosser.inspiration.registry.MachineRegistry;
import com.starcrosser.inspiration.material.ModMaterials;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
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
        // 同一个 tag 反复 tag(...) 拿到的是同一个收集器，这里存成局部变量好往里续机器清单
        var pickaxeTag = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        // 铯盐块/铯块
        pickaxeTag
                .add(ModBlocks.CESIUM_SALT_BLOCK.get())
                .add(ModMaterials.CESIUM.block().get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.LIGNITE_BLOCK.get())
                .add(ModBlocks.EPHEMERAL_ORE.get())
                .add(ModMaterials.LAPIS.block().get())
                .add(ModBlocks.CESIUM_SALT_ORE.get())
                .add(ModMaterials.REDSTONE.block().get());

        // 思维流体反应生成的方块：三种结晶/岩石都要镐，灵感凝胶与原版粘液块一样徒手可取
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.DILUTED_INSPIRATION_CRYSTAL.get())
                .add(ModBlocks.CONCENTRATED_INSPIRATION_CRYSTAL.get())
                .add(ModBlocks.MIND_CRYSTAL_ROCK.get());

        // 钫盐系列：矿石与两种块都要镐
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.FRANCIUM_SALT_ORE.get())
                .add(ModBlocks.FRANCIUM_SALT_BLOCK.get())
                .add(ModMaterials.FRANCIUM.block().get());

        // 机器（遍历 MachineRegistry，加机器不用再往这里补）
        for (Block machine : MachineRegistry.blocks()) {
            pickaxeTag.add(machine);
        }
        pickaxeTag
                .add(ModBlocks.HIGH_ENERGY_MACHINE_CASING.get())
                .add(ModMultiblocks.INPUT_HATCH.getBlock())
                .add(ModMultiblocks.OUTPUT_HATCH.getBlock())
                .add(ModMultiblocks.ENERGY_PORT.getBlock())
                .add(ModMultiblocks.INSPIRATION_HATCH.getBlock())
                .add(ModMultiblocks.DATA_HATCH.getBlock())
                .add(ModMultiblocks.STRUCTURE_SIMULATOR.getBlock());

        // 注入结构
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModMaterials.BASALT_ALLOY.block().get())
                .add(ModMaterials.SCULK_ALLOY.block().get())
                .add(ModInjection.MATTER_BINDER.getBlock())
                .add(ModInjection.INJECTION_GUN.getBlock());

        // 注入等级：底座方块决定这套结构能执行的配方等级上限。
        // 1 级 = 玄武岩合金块，2 级 = 幽匿合金块；将来再加底座只需续写 tag(injection_tier_N)。
        tag(InjectionTiers.tag(1))
                .add(ModMaterials.BASALT_ALLOY.block().get());
        tag(InjectionTiers.tag(2))
                .add(ModMaterials.SCULK_ALLOY.block().get());

        // 挖掘等级：石镐及以上
        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.LIGNITE_BLOCK.get())
                .add(ModBlocks.CESIUM_SALT_ORE.get())
                .add(ModBlocks.DILUTED_INSPIRATION_CRYSTAL.get());

        // 挖掘等级：铁镐及以上
        var ironToolTag = tag(BlockTags.NEEDS_IRON_TOOL);
        ironToolTag
                // 钫盐矿埋在地狱上层，比铯盐矿（石镐）更苛刻
                .add(ModBlocks.FRANCIUM_SALT_ORE.get())
                .add(ModBlocks.CONCENTRATED_INSPIRATION_CRYSTAL.get())
                .add(ModBlocks.MIND_CRYSTAL_ROCK.get())
                .add(ModMaterials.LAPIS.block().get())
                .add(ModMaterials.REDSTONE.block().get());
        // 机器同样遍历清单
        for (Block machine : MachineRegistry.blocks()) {
            ironToolTag.add(machine);
        }
        ironToolTag
                .add(ModMaterials.BASALT_ALLOY.block().get())
                .add(ModMaterials.SCULK_ALLOY.block().get())
                .add(ModInjection.MATTER_BINDER.getBlock())
                .add(ModInjection.INJECTION_GUN.getBlock());

        // 挖掘等级：钻石镐及以上
        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.EPHEMERAL_ORE.get());
    }
}