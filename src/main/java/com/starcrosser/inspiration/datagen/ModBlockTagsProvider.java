package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.injection.InjectionTiers;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.machine.ModMachines;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
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
        // 铯盐块/铯块
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.CESIUM_SALT_BLOCK.get())
                .add(ModBlocks.CESIUM_BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.LIGNITE_BLOCK.get())
                .add(ModBlocks.EPHEMERAL_ORE.get())
                .add(ModBlocks.LAPIS_INGOT_BLOCK.get())
                .add(ModBlocks.CESIUM_SALT_ORE.get())
                .add(ModBlocks.REDSTONE_INGOT_BLOCK.get());

        // 思维流体反应生成的方块：三种结晶/岩石都要镐，灵感凝胶与原版粘液块一样徒手可取
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.DILUTED_INSPIRATION_CRYSTAL.get())
                .add(ModBlocks.CONCENTRATED_INSPIRATION_CRYSTAL.get())
                .add(ModBlocks.MIND_CRYSTAL_ROCK.get());

        // 钫盐系列：矿石与两种块都要镐
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.FRANCIUM_SALT_ORE.get())
                .add(ModBlocks.FRANCIUM_SALT_BLOCK.get())
                .add(ModBlocks.FRANCIUM_BLOCK.get());

        // 机器
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModMachines.MATTER_FUSION_MACHINE.getBlock())
                .add(ModMachines.ENERGIZED_MATTER_FUSION_MACHINE.getBlock())
                .add(ModMachines.MATTER_RESHAPER.getBlock())
                .add(ModMachines.MINERAL_TWISTER.getBlock())
                .add(ModMachines.INSPIRATION_ASSEMBLER.getBlock())
                .add(ModMachines.CORE_DRILLER.getBlock())
                .add(ModMachines.HIGH_ENERGY_MINERAL_TWISTER.getBlock())
                .add(ModMachines.HIGH_ENERGY_INSPIRATION_ASSEMBLER.getBlock())
                .add(ModMachines.HIGH_ENERGY_MATTER_RESHAPER.getBlock())
                .add(ModMachines.ALKALINE_REACTOR.getBlock())
                .add(ModMachines.RTG_GENERATOR.getBlock())
                .add(ModBlocks.HIGH_ENERGY_MACHINE_CASING.get())
                .add(ModMultiblocks.INPUT_HATCH.getBlock())
                .add(ModMultiblocks.OUTPUT_HATCH.getBlock())
                .add(ModMultiblocks.ENERGY_PORT.getBlock())
                .add(ModMultiblocks.INSPIRATION_HATCH.getBlock())
                .add(ModMultiblocks.DATA_HATCH.getBlock())
                .add(ModMultiblocks.STRUCTURE_SIMULATOR.getBlock());

        // 注入结构
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.BASALT_ALLOY_BLOCK.get())
                .add(ModBlocks.SCULK_ALLOY_BLOCK.get())
                .add(ModInjection.MATTER_BINDER.getBlock())
                .add(ModInjection.INJECTION_GUN.getBlock());

        // 注入等级：底座方块决定这套结构能执行的配方等级上限。
        // 1 级 = 玄武岩合金块，2 级 = 幽匿合金块；将来再加底座只需续写 tag(injection_tier_N)。
        tag(InjectionTiers.tag(1))
                .add(ModBlocks.BASALT_ALLOY_BLOCK.get());
        tag(InjectionTiers.tag(2))
                .add(ModBlocks.SCULK_ALLOY_BLOCK.get());

        // 挖掘等级：石镐及以上
        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.LIGNITE_BLOCK.get())
                .add(ModBlocks.CESIUM_SALT_ORE.get())
                .add(ModBlocks.DILUTED_INSPIRATION_CRYSTAL.get());

        // 挖掘等级：铁镐及以上
        tag(BlockTags.NEEDS_IRON_TOOL)
                // 钫盐矿埋在地狱上层，比铯盐矿（石镐）更苛刻
                .add(ModBlocks.FRANCIUM_SALT_ORE.get())
                .add(ModBlocks.CONCENTRATED_INSPIRATION_CRYSTAL.get())
                .add(ModBlocks.MIND_CRYSTAL_ROCK.get())
                .add(ModBlocks.LAPIS_INGOT_BLOCK.get())
                .add(ModBlocks.REDSTONE_INGOT_BLOCK.get())
                .add(ModMachines.MATTER_FUSION_MACHINE.getBlock())
                .add(ModMachines.ENERGIZED_MATTER_FUSION_MACHINE.getBlock())
                .add(ModMachines.MATTER_RESHAPER.getBlock())
                .add(ModMachines.MINERAL_TWISTER.getBlock())
                .add(ModMachines.INSPIRATION_ASSEMBLER.getBlock())
                .add(ModMachines.CORE_DRILLER.getBlock())
                .add(ModMachines.HIGH_ENERGY_MINERAL_TWISTER.getBlock())
                .add(ModMachines.HIGH_ENERGY_INSPIRATION_ASSEMBLER.getBlock())
                .add(ModMachines.HIGH_ENERGY_MATTER_RESHAPER.getBlock())
                .add(ModMachines.ALKALINE_REACTOR.getBlock())
                .add(ModMachines.RTG_GENERATOR.getBlock())
                .add(ModBlocks.BASALT_ALLOY_BLOCK.get())
                .add(ModBlocks.SCULK_ALLOY_BLOCK.get())
                .add(ModInjection.MATTER_BINDER.getBlock())
                .add(ModInjection.INJECTION_GUN.getBlock());

        // 挖掘等级：钻石镐及以上
        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.EPHEMERAL_ORE.get());
    }
}