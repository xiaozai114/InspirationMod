package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.voidenergy.ModVoidEnergy;
import com.starcrosser.inspiration.injection.ModInjection;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.multiblock.ModMultiblocks;
import com.starcrosser.inspiration.registry.MachineRegistry;
import com.starcrosser.inspiration.material.ModMaterials;
import com.starcrosser.inspiration.material.InspirationMaterial;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModBlockLootTablesProvider extends BlockLootSubProvider {
    public ModBlockLootTablesProvider( HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.LIGNITE_BLOCK.get());
        dropSelf(ModBlocks.INTENSIFIED_TNT.get());
        dropSelf(ModBlocks.CESIUM_SALT_BLOCK.get());
        // 材料块掉自身，遍历清单
        for (InspirationMaterial material : ModMaterials.ALL) {
            if (material.hasBlock()) {
                dropSelf(material.block().get());
            }
        }
        dropSelf(ModBlocks.FRANCIUM_SALT_BLOCK.get());
        dropSelf(ModBlocks.EMPOWERED_TNT.get());

        dropOther(ModBlocks.EPHEMERAL_ORE.get(), ModItems.EPHEMERAL_MATERIAL.get());
        dropOther(ModBlocks.CESIUM_SALT_ORE.get(), ModItems.RAW_CESIUM_SALT.get());
        dropOther(ModBlocks.FRANCIUM_SALT_ORE.get(), ModItems.RAW_FRANCIUM_SALT.get());

        // 思维流体与水 / 岩浆反应生成的结晶与凝胶。
        addFortuneDrop(ModBlocks.DILUTED_INSPIRATION_CRYSTAL.get(), ModItems.INSPIRATION_SHARD.get(), 2);
        addFortuneDrop(ModBlocks.CONCENTRATED_INSPIRATION_CRYSTAL.get(), ModItems.INSPIRATION.get(), 1);
        addCrystalDrop(ModBlocks.MIND_CRYSTAL_ROCK.get(), ModItems.MIND_CRYSTAL.get());
        dropSelf(ModBlocks.INSPIRATION_GEL.get());

        // 机器方块（注册在 Titanium 的注册表中，需要手动补战利品表）
        MachineRegistry.blocks().forEach(this::dropSelf);
        // 结构模拟机：外壳、五种仓室与控制器（Titanium 注册的方块不在 ModBlocks.BLOCKS 里，要手动补）
        dropSelf(ModBlocks.HIGH_ENERGY_MACHINE_CASING.get());
        dropSelf(ModMultiblocks.INPUT_HATCH.getBlock());
        dropSelf(ModMultiblocks.OUTPUT_HATCH.getBlock());
        dropSelf(ModMultiblocks.ENERGY_PORT.getBlock());
        dropSelf(ModMultiblocks.INSPIRATION_HATCH.getBlock());
        dropSelf(ModMultiblocks.DATA_HATCH.getBlock());
        dropSelf(ModMultiblocks.STRUCTURE_SIMULATOR.getBlock());

        // 注入结构的两个方块同样注册在 Titanium 的注册表中
        dropSelf(ModInjection.MATTER_BINDER.getBlock());
        dropSelf(ModInjection.INJECTION_GUN.getBlock());

        // 虚空能量体系：提取器与中继器掉落自身；开裂的基岩不可破坏，掉落表为空
        dropSelf(ModVoidEnergy.VOID_ENERGY_EXTRACTOR.get());
        dropSelf(ModVoidEnergy.VOID_ENERGY_RELAY.get());
        add(ModVoidEnergy.CRACKED_BEDROCK.get(), LootTable.lootTable());
    }
    /**
     * 结晶类方块的掉落表：精准采集掉落方块本身，否则掉落指定物品并受时运加成
     * （时运每级额外 +0~1 个，与原版荧石、西瓜一类「均匀加成」的做法一致）。
     */
    private void addFortuneDrop(Block block, Item drop, int count) {
        HolderLookup.RegistryLookup<Enchantment> enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT);
        add(block, createSilkTouchDispatchTable(block,
                applyExplosionDecay(block, LootItem.lootTableItem(drop)
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(count)))
                        .apply(ApplyBonusCount.addUniformBonusCount(enchantments.getOrThrow(Enchantments.FORTUNE))))));
    }

    /** 固定产出的方块：精准采集掉落方块本身，否则固定掉落一件物品，不受时运影响。 */
    private void addCrystalDrop(Block block, Item drop) {
        add(block, createSilkTouchDispatchTable(block,
                applyExplosionDecay(block, LootItem.lootTableItem(drop))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        List<Block> blocks = new ArrayList<>(ModBlocks.BLOCKS.getEntries().stream().map(Holder::value).toList());
        // 机器方块注册在 Titanium 的注册表中，需要手动加入，否则会被判定为“非方块战利品表”
        blocks.addAll(MachineRegistry.blocks());
        blocks.add(ModBlocks.HIGH_ENERGY_MACHINE_CASING.get());
        blocks.add(ModMultiblocks.INPUT_HATCH.getBlock());
        blocks.add(ModMultiblocks.OUTPUT_HATCH.getBlock());
        blocks.add(ModMultiblocks.ENERGY_PORT.getBlock());
        blocks.add(ModMultiblocks.INSPIRATION_HATCH.getBlock());
        blocks.add(ModMultiblocks.DATA_HATCH.getBlock());
        blocks.add(ModMultiblocks.STRUCTURE_SIMULATOR.getBlock());
        blocks.add(ModInjection.MATTER_BINDER.getBlock());
        blocks.add(ModInjection.INJECTION_GUN.getBlock());
        blocks.add(ModVoidEnergy.CRACKED_BEDROCK.get());
        blocks.add(ModVoidEnergy.VOID_ENERGY_EXTRACTOR.get());
        blocks.add(ModVoidEnergy.VOID_ENERGY_RELAY.get());
        return blocks;
    }
}
