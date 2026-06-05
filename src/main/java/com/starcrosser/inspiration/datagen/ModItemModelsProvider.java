package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelsProvider extends ItemModelProvider {

    public ModItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Inspiration.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // ========== 普通物品（generated） ==========
        basicItem(ModItems.INSPIRATION.get());
        basicItem(ModItems.COOKED_INSPIRATION.get());
        basicItem(ModItems.LIGNITE.get());
        basicItem(ModItems.INSPIREAD.get());
        basicItem(ModItems.INSPIRRY.get());
        basicItem(ModItems.COKE.get());
        basicItem(ModItems.REDSTONE_INGOT.get());
        basicItem(ModItems.LAPIS_INGOT.get());
        basicItem(ModItems.UNSTABLE_CORE.get());
        basicItem(ModItems.UNSTABLE_SMITHING_TEMPLATE.get());
        basicItem(ModItems.EXTREME_ALLOY.get());
        basicItem(ModItems.EPHEMERAL_MATERIAL.get());
        basicItem(ModItems.SOD.get());
        basicItem(ModItems.EMPTY_BEACON.get());
        basicItem(ModItems.POWER_BEACON.get());
        basicItem(ModItems.EXP_HOE.get());
        basicItem(ModItems.SCYTHE_OF_DEATH.get());
        basicItem(ModItems.EXP_SWORD.get());
        basicItem(ModItems.EXP_PICKAXE.get());
        basicItem(ModItems.CESIUM_SALT.get());
        basicItem(ModItems.RAW_CESIUM_SALT.get());
        basicItem(ModItems.EXPLODING_CANDY.get());
        basicItem(ModItems.MIND_FLUID_BUCKET.get());
        basicItem(ModItems.CALCIUM_HYDROXIDE_DUST.get());
        basicItem(ModItems.CALCIUM_DUST.get());

        // ========== 手持物品（handheld）直接使用父类方法 ==========
        handheldItem(ModItems.STICKY_AXE.get());
        handheldItem(ModItems.HEAD_HUNTER_AXE.get());
        handheldItem(ModItems.STORM_SWORD.get());
        handheldItem(ModItems.WORLD_BREAKER.get());


        // ========== 方块物品（指向对应的方块模型） ==========
        blockItem(ModBlocks.LIGNITE_BLOCK.get());
        blockItem(ModBlocks.REDSTONE_INGOT_BLOCK.get());
        blockItem(ModBlocks.LAPIS_INGOT_BLOCK.get());
        blockItem(ModBlocks.EPHEMERAL_ORE.get());
        blockItem(ModBlocks.CESIUM_SALT_ORE.get());
        blockItem(ModBlocks.INTENSIFIED_TNT.get());

    }

    /** 生成方块物品模型：父模型为对应方块的模型 */
    private void blockItem(net.minecraft.world.level.block.Block block) {
        String name = block.getDescriptionId().replace("block." + Inspiration.MODID + ".", "");
        withExistingParent(name, modLoc("block/" + name));
    }

}