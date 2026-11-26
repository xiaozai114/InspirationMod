package com.starcrosser.inspiration.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;

/**
 * 升级模板工厂。
 * <p>
 * 两个模板都与下界合金升级模板同构：文案沿用原版的「应用于 / 材料」标题，
 * 底材图标列出可升级的工具，附加材料图标是锭。升级关系由数据生成里的锻造转换配方定义。
 * <ul>
 *     <li>{@link #create()} —— 翡翠钢升级模板：灵感工具 → 翡翠钢工具，材料是翡翠钢锭；</li>
 *     <li>{@link #createEnergized()} —— 注能翡翠钢升级模板：翡翠钢工具 → 注能翡翠钢工具，
 *         材料是注能翡翠钢，底材多列一个铲。</li>
 * </ul>
 */
public final class JadeSteelUpgradeTemplate {

    private static final String JADE_KEY_PREFIX = "item.inspiration.jade_steel_upgrade";
    private static final String ENERGIZED_KEY_PREFIX = "item.inspiration.energized_jade_steel_upgrade";

    /** 可升级的底材图标：剑 / 镐 / 斧 / 锄。 */
    private static final List<ResourceLocation> JADE_BASE_SLOT_ICONS = List.of(
            vanillaIcon("item/empty_slot_sword"),
            vanillaIcon("item/empty_slot_pickaxe"),
            vanillaIcon("item/empty_slot_axe"),
            vanillaIcon("item/empty_slot_hoe"));

    /** 注能档的底材图标多一个铲（翡翠钢档没有铲）。 */
    private static final List<ResourceLocation> ENERGIZED_BASE_SLOT_ICONS = List.of(
            vanillaIcon("item/empty_slot_sword"),
            vanillaIcon("item/empty_slot_pickaxe"),
            vanillaIcon("item/empty_slot_axe"),
            vanillaIcon("item/empty_slot_shovel"),
            vanillaIcon("item/empty_armor_slot_helmet"),
            vanillaIcon("item/empty_armor_slot_chestplate"),
            vanillaIcon("item/empty_armor_slot_leggings"),
            vanillaIcon("item/empty_armor_slot_boots"),
            vanillaIcon("item/empty_slot_hoe"));

    /** 附加材料图标：锭。 */
    private static final List<ResourceLocation> ADDITION_SLOT_ICONS = List.of(
            vanillaIcon("item/empty_slot_ingot"));

    private JadeSteelUpgradeTemplate() {
    }

    public static SmithingTemplateItem create() {
        return create(JADE_KEY_PREFIX, "upgrade.inspiration.jade_steel_upgrade", JADE_BASE_SLOT_ICONS);
    }

    public static SmithingTemplateItem createEnergized() {
        return create(ENERGIZED_KEY_PREFIX, "upgrade.inspiration.energized_jade_steel_upgrade",
                ENERGIZED_BASE_SLOT_ICONS);
    }

    private static SmithingTemplateItem create(String keyPrefix, String upgradeTitleKey,
                                               List<ResourceLocation> baseSlotIcons) {
        return new SmithingTemplateItem(
                description(keyPrefix + ".applies_to"),
                description(keyPrefix + ".ingredients"),
                Component.translatable(upgradeTitleKey).withStyle(ChatFormatting.GRAY),
                Component.translatable(keyPrefix + ".base_slot_description"),
                Component.translatable(keyPrefix + ".additions_slot_description"),
                baseSlotIcons,
                ADDITION_SLOT_ICONS);
    }

    /** 「应用于」「材料」这类标题用原版键，保持与下界合金模板一致的灰色。 */
    private static Component description(String key) {
        return Component.translatable(key).withStyle(ChatFormatting.BLUE);
    }

    private static ResourceLocation vanillaIcon(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }
}
