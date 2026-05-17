// 文件：ModEnUsLangProvider.java
package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModEnUsLangProvider extends LanguageProvider {

    public ModEnUsLangProvider(PackOutput output) {
        super(output, Inspiration.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // 物品
        add(ModItems.INSPIRATION.get(), "inspiration");
        add(ModItems.COOKED_INSPIRATION.get(), "cooked inspiration");
        add(ModItems.LIGNITE.get(), "lignite");
        add(ModItems.INSPIREAD.get(), "inspiread");
        add(ModItems.INSPIRRY.get(), "inspirry");
        add(ModItems.COKE.get(), "coke");
        add(ModItems.EXP_SWORD.get(), "Inspiration Sword");
        add(ModItems.STORM_SWORD.get(), "Storm Sword");
        add(ModItems.STICKY_AXE.get(), "Inspiration Axe");
        add(ModItems.HEAD_HUNTER_AXE.get(), "Head Hunter");
        add(ModItems.EXP_PICKAXE.get(), "Inspiration Pickaxe");
        add(ModItems.REDSTONE_INGOT.get(), "Redstone Ingot");
        add(ModItems.LAPIS_INGOT.get(), "Lapis Ingot");
        add(ModItems.UNSTABLE_CORE.get(), "§7Unstable Core");
        add(ModItems.UNSTABLE_SMITHING_TEMPLATE.get(), "§7Unstable Smithing Template");
        add(ModItems.WORLD_BREAKER.get(), "§9WORLD§4BREAKER§5XX");
        add(ModItems.EXTREME_ALLOY.get(), "§9Extreme §4Alloy");
        add(ModItems.EPHEMERAL_MATERIAL.get(), "§5Ephemeral Material");
        add(ModItems.SOD.get(), "Sod");
        add(ModItems.EXP_HOE.get(), "Inspiration Hoe");
        add(ModItems.SCYTHE_OF_DEATH.get(), "Scythe of Death");
        add(ModItems.EMPTY_BEACON.get(), "Empty Beacon");
        add(ModItems.CESIUM_SALT.get(), "Cesium Salt");
        add(ModBlocks.CESIUM_SALT_ORE.get(), "Cesium Salt Ore");
        add(ModItems.RAW_CESIUM_SALT.get(), "Raw Cesium Salt");

        // 方块
        add(ModBlocks.REDSTONE_INGOT_BLOCK.get(), "Redstone Ingot Block");
        add(ModBlocks.LAPIS_INGOT_BLOCK.get(), "Lapis Ingot Block");
        add(ModBlocks.LIGNITE_BLOCK.get(), "lignite block");
        add(ModBlocks.INTENSIFIED_TNT.get(), "intensified_tnt");
        add(ModBlocks.EPHEMERAL_ORE.get(), "§5Ephemeral Ore");

        // 工具提示
        add("tooltip.inspiration.exp_sword.description", "Create experience orb when attack");
        add("tooltip.inspiration.storm_sowrd.description", "Right click for windburst");
        add("tooltip.inspiration.sticky_axe.description", "Try Stripping some wood?");
        add("tooltip.inspiration.exp_pickaxe.description", "Create experience orb when mining");

        // 物品组
        add("itemGroup.inspire_tab", "Inspiration");

        // 配置界面
        add("inspiration.configuration.title", "Inspiration Configs");
        add("inspiration.configuration.section.inspiration.common.toml", "Inspiration Configs");
        add("inspiration.configuration.section.inspiration.common.toml.title", "Inspiration Configs");
        add("inspiration.configuration.items", "Item List");
        add("inspiration.configuration.logDirtBlock", "Log Dirt Block");
        add("inspiration.configuration.magicNumberIntroduction", "Magic Number Text");
        add("inspiration.configuration.magicNumber", "Magic Number");
    }
}