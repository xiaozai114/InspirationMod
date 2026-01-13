package com.starcrosser.inspiration.item;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.item.custom.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Inspiration.MODID);
    public static final DeferredItem<Item> INSPIRATION=
            ITEMS.register("inspiration", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> COOKED_INSPIRATION=
            ITEMS.register("cooked_inspiration", () ->new Item(new Item.Properties().food(ModFoods.COOKED_INSPIRATION)));
    public static final DeferredItem<Item> LIGNITE=
            ITEMS.register("lignite", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> COKE=
            ITEMS.register("coke", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> INSPIREAD=
            ITEMS.register("inspiread", () ->new Item(new Item.Properties().food(ModFoods.INSPIREAD)));
    public static final DeferredItem<Item> REDSTONE_INGOT=
            ITEMS.register("redstone_ingot", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> LAPIS_INGOT=
            ITEMS.register("lapis_ingot", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> INSPIRRY=
            ITEMS.register("inspirry", () ->new Item(new Item.Properties().food(ModFoods.INSPIRRY)));
    public static final DeferredItem<Item> EXP_SWORD=
            ITEMS.register("exp_sword", () ->new ExperienceSword(Tiers.IRON, 3,-2.2F,5,
                    new Item.Properties().durability(899).rarity(Rarity.RARE)));
    public static final DeferredItem<Item> STORM_SWORD=
            ITEMS.register("storm_sword",()->new StormSword(Tiers.DIAMOND, 4, -2.2F, 5,60,
                    new Item.Properties().durability(1799).rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> STICKY_AXE=
        ITEMS.register("sticky_axe", () ->new StickyAxe(Tiers.IRON, 7,-3.2F,
                    new Item.Properties().durability(899).rarity(Rarity.RARE)));
    public static final DeferredItem<Item> HEAD_HUNTER_AXE=
            ITEMS.register("head_hunter_axe", () ->new HeadHunterAxe(Tiers.DIAMOND, 9,-3.2F,
                    new Item.Properties().durability(1799).rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> EXP_PICKAXE=
            ITEMS.register("exp_pickaxe", () ->new ExperiencePickaxe(Tiers.IRON, 0,-2.4F,
                    new Item.Properties().durability(899).rarity(Rarity.RARE)));
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
