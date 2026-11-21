package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import com.starcrosser.inspiration.voidenergy.ModVoidEnergy;
import com.starcrosser.inspiration.material.ModMaterials;
import com.starcrosser.inspiration.material.InspirationMaterial;
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
        basicItem(ModItems.UNSTABLE_CORE.get());
        basicItem(ModItems.UNSTABLE_SMITHING_TEMPLATE.get());
        basicItem(ModItems.EPHEMERAL_MATERIAL.get());
        basicItem(ModItems.SOD.get());
        basicItem(ModItems.EMPTY_BEACON.get());
        basicItem(ModItems.POWER_BEACON.get());
        basicItem(ModItems.EXP_HOE.get());
        basicItem(ModItems.SCYTHE_OF_DEATH.get());
        basicItem(ModItems.EXP_SWORD.get());
        basicItem(ModItems.EXP_PICKAXE.get());
        basicItem(ModItems.EXP_SHOVEL.get());
        basicItem(ModItems.CESIUM_SALT.get());
        basicItem(ModItems.RAW_CESIUM_SALT.get());
        basicItem(ModItems.EXPLODING_CANDY.get());
        basicItem(ModItems.MIND_FLUID_BUCKET.get());
        basicItem(ModItems.MOLTEN_PLASMA_BUCKET.get());
        basicItem(ModItems.NUTRIENT_FLUID_BUCKET.get());
        basicItem(ModItems.VILLAGER_BRAIN.get());
        basicItem(ModItems.ENDERMAN_BRAIN.get());
        basicItem(ModItems.INACTIVE_BRAIN.get());
        basicItem(ModItems.PARASITE.get());
        basicItem(ModItems.MECHANICAL_PARASITE.get());
        basicItem(ModItems.MOLTEN_EPHEMERAL_ALLOY_BUCKET.get());
        basicItem(ModItems.SCULK_ALLOY_DRILL_BIT.get());
        basicItem(ModItems.UNSTABLE_METAL.get());
        basicItem(ModItems.WEAKENED_CORE.get());
        basicItem(ModItems.VOID_PLASMA_INGOT.get());
        basicItem(ModItems.VOID_DEW.get());
        basicItem(ModItems.NULL_POINT_MODULE.get());
        basicItem(ModItems.ZERO_POINT_AMPLIFIER.get());
        basicItem(ModItems.VOID_ALLOY.get());
        blockItem(ModVoidEnergy.CRACKED_BEDROCK.get());
        blockItem(ModVoidEnergy.VOID_ENERGY_EXTRACTOR.get());
        blockItem(ModVoidEnergy.VOID_ENERGY_RELAY.get());
        basicItem(ModItems.CALCIUM_HYDROXIDE_DUST.get());
        basicItem(ModItems.CALCIUM_DUST.get());
        basicItem(ModItems.CESIUM_DUST.get());
        // 材料：锭本体（粒/尘开关打开时一并生成），遍历清单，加材料不用再往这里补
        for (InspirationMaterial material : ModMaterials.ALL) {
            basicItem(material.item().get());
            if (material.hasNugget()) {
                basicItem(material.nugget().get());
            }
            if (material.hasGrit()) {
                basicItem(material.grit().get());
            }
        }
        basicItem(ModItems.REDUCTION_POWDER.get());
        basicItem(ModItems.REINFORCED_CORE.get());
        basicItem(ModItems.POWERFUL_CORE.get());
        basicItem(ModItems.HIGH_ENERGY_CORE.get());
        // 发电核心 / 反应核心（两种发电机的核心部件）与核废料
        basicItem(ModItems.GENERATION_CORE.get());
        basicItem(ModItems.REACTION_CORE.get());
        basicItem(ModItems.NUCLEAR_WASTE.get());
        basicItem(ModItems.BLANK_DATA.get());
        basicItem(ModItems.SHIPWRECK_DATA.get());
        basicItem(ModItems.PROJECTOR.get());
        basicItem(ModItems.JADE_STEEL_UPGRADE_TEMPLATE.get());
        basicItem(ModItems.JADE_STEEL_SWORD.get());
        basicItem(ModItems.JADE_STEEL_PICKAXE.get());
        basicItem(ModItems.JADE_STEEL_HOE.get());
        basicItem(ModItems.JADE_STEEL_SHOVEL.get());
        basicItem(ModItems.ENERGIZED_JADE_STEEL_UPGRADE_TEMPLATE.get());
        basicItem(ModItems.ENERGIZED_JADE_STEEL_SWORD.get());
        basicItem(ModItems.ENERGIZED_JADE_STEEL_PICKAXE.get());
        basicItem(ModItems.ENERGIZED_JADE_STEEL_HOE.get());
        basicItem(ModItems.ENERGIZED_JADE_STEEL_SHOVEL.get());
        basicItem(ModItems.CAPACITOR_PLUGIN.get());
        basicItem(ModItems.INSPIRATION_PLUGIN.get());
        basicItem(ModItems.SPEED_PLUGIN.get());
        basicItem(ModItems.CAPACITOR_DOWNGRADE_PLUGIN.get());
        basicItem(ModItems.RECYCLING_PLUGIN.get());
        basicItem(ModItems.INSPIRATION_SHARD.get());
        basicItem(ModItems.MIND_CRYSTAL.get());
        // 地核钻探者与它的耗材
        basicItem(ModItems.BASALT_ALLOY_DRILL_BIT.get());
        // 注入合成相关的物品
        basicItem(ModItems.BASALT_BASE.get());
        basicItem(ModItems.BINDING_WAND.get());
        // 钫盐系列与盖革计数器
        basicItem(ModItems.RAW_FRANCIUM_SALT.get());
        basicItem(ModItems.FRANCIUM_SALT.get());
        basicItem(ModItems.FRANCIUM_DUST.get());
        basicItem(ModItems.GEIGER_COUNTER.get());
        // 下一阶段主材与量具
        basicItem(ModItems.WORLD_DATA_DETECTOR.get());
        // 高能阶段：三种注能锭（高能机器的材料）
        basicItem(ModItems.GUIDE_BOOK.get());
        // 末地合金盾牌的模型是手写的 builtin/entity（见 assets/inspiration/models/item/
        // end_alloy_shield.json 与 _blocking.json），这里不能生成，否则会把它顶掉

        // ========== 手持物品（handheld）直接使用父类方法 ==========
        handheldItem(ModItems.STICKY_AXE.get());
        handheldItem(ModItems.JADE_STEEL_AXE.get());
        handheldItem(ModItems.ENERGIZED_JADE_STEEL_AXE.get());
        handheldItem(ModItems.HEAD_HUNTER_AXE.get());
        handheldItem(ModItems.STORM_SWORD.get());
        handheldItem(ModItems.WORLD_BREAKER.get());
        handheldItem(ModItems.BASALT_ALLOY_FIRESTRIKER.get());


        // ========== 方块物品（指向对应的方块模型） ==========
        blockItem(ModBlocks.LIGNITE_BLOCK.get());
        blockItem(ModBlocks.EPHEMERAL_ORE.get());
        blockItem(ModBlocks.CESIUM_SALT_ORE.get());
        blockItem(ModBlocks.INTENSIFIED_TNT.get());
        blockItem(ModBlocks.CESIUM_SALT_BLOCK.get());
        // 材料的方块物品（只有开了方块开关的材料才有）
        for (InspirationMaterial material : ModMaterials.ALL) {
            if (material.hasBlock()) {
                blockItem(material.block().get());
            }
        }
        blockItem(ModBlocks.FRANCIUM_SALT_ORE.get());
        blockItem(ModBlocks.FRANCIUM_SALT_BLOCK.get());
        blockItem(ModBlocks.EMPOWERED_TNT.get());
        blockItem(ModBlocks.DILUTED_INSPIRATION_CRYSTAL.get());
        blockItem(ModBlocks.CONCENTRATED_INSPIRATION_CRYSTAL.get());
        blockItem(ModBlocks.INSPIRATION_GEL.get());
        blockItem(ModBlocks.MIND_CRYSTAL_ROCK.get());
        // 结构模拟机的机体材料：没有这一条，方块物品就没有模型，物品栏里是黑紫的缺失贴图
        blockItem(ModBlocks.HIGH_ENERGY_MACHINE_CASING.get());
    }

    /** 生成方块物品模型：父模型为对应方块的模型 */
    private void blockItem(net.minecraft.world.level.block.Block block) {
        String name = block.getDescriptionId().replace("block." + Inspiration.MODID + ".", "");
        withExistingParent(name, modLoc("block/" + name));
    }

}