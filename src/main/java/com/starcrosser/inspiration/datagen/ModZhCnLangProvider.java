// 文件：ModZhCnLangProvider.java
package com.starcrosser.inspiration.datagen;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModZhCnLangProvider extends LanguageProvider {

    public ModZhCnLangProvider(PackOutput output) {
        super(output, Inspiration.MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        // 物品
        add(ModItems.INSPIRATION.get(), "灵感");
        add(ModItems.COOKED_INSPIRATION.get(), "烤灵感");
        add(ModItems.LIGNITE.get(), "褐煤");
        add(ModItems.INSPIREAD.get(), "灵感面包");
        add(ModItems.INSPIRRY.get(), "灵感浆果");
        add(ModItems.COKE.get(), "焦煤");
        add(ModItems.EXP_SWORD.get(), "灵感之剑");
        add(ModItems.STORM_SWORD.get(), "风暴军刀");
        add(ModItems.STICKY_AXE.get(), "灵感之斧");
        add(ModItems.HEAD_HUNTER_AXE.get(), "斩首者");
        add(ModItems.EXP_PICKAXE.get(), "灵感之镐");
        add(ModItems.REDSTONE_INGOT.get(), "红石锭");
        add(ModItems.LAPIS_INGOT.get(), "青金石锭");
        add(ModItems.UNSTABLE_CORE.get(), "§7不稳核心");
        add(ModItems.UNSTABLE_SMITHING_TEMPLATE.get(), "§7不稳锻造模板");
        add(ModItems.WORLD_BREAKER.get(), "§9破§a界§4者§5XX");
        add(ModItems.EXTREME_ALLOY.get(), "§9极限§4合金");
        add(ModItems.EPHEMERAL_MATERIAL.get(), "§5无恒物质");
        add(ModItems.SOD.get(), "草皮");
        add(ModItems.EXP_HOE.get(), "灵感之锄");
        add(ModItems.SCYTHE_OF_DEATH.get(), "死神之镰");
        add(ModItems.EMPTY_BEACON.get(), "空信标");

        // 方块
        add(ModBlocks.REDSTONE_INGOT_BLOCK.get(), "红石锭方块");
        add(ModBlocks.LAPIS_INGOT_BLOCK.get(), "青金石锭方块");
        add(ModBlocks.LIGNITE_BLOCK.get(), "褐煤块");
        add(ModBlocks.EPHEMERAL_ORE.get(), "§5无恒矿石");

        // 工具提示
        add("tooltip.inspiration.exp_sword.description", "攻击敌人时掉落经验球");
        add("tooltip.inspiration.storm_sowrd.description", "右键发射风弹攻击");
        add("tooltip.inspiration.sticky_axe.description", "为什么要给树木去皮？");
        add("tooltip.inspiration.exp_pickaxe.description", "挖掘时掉落经验球");

        // 物品组
        add("itemGroup.inspire_tab", "灵感");

        // 配置界面（保持与英文相同，未汉化）
        add("inspiration.configuration.title", "Inspiration Configs");
        add("inspiration.configuration.section.inspiration.common.toml", "Inspiration Configs");
        add("inspiration.configuration.section.inspiration.common.toml.title", "Inspiration Configs");
        add("inspiration.configuration.items", "Item List");
        add("inspiration.configuration.logDirtBlock", "Log Dirt Block");
        add("inspiration.configuration.magicNumberIntroduction", "Magic Number Text");
        add("inspiration.configuration.magicNumber", "Magic Number");
    }
}