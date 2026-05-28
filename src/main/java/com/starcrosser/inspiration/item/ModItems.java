package com.starcrosser.inspiration.item;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.fluid.ModFluids;
import com.starcrosser.inspiration.item.custom.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(Inspiration.MODID);
    public static final DeferredItem<Item> INSPIRATION=
            ITEMS.register("inspiration", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> CESIUM_SALT=
            ITEMS.register("cesium_salt", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_CESIUM_SALT=
            ITEMS.register("raw_cesium_salt", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> COOKED_INSPIRATION=
            ITEMS.register("cooked_inspiration", () ->new Item(new Item.Properties().food(ModFoods.COOKED_INSPIRATION)));
    public static final DeferredItem<Item> LIGNITE=
            ITEMS.register("lignite", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> EMPTY_BEACON=
            ITEMS.register("empty_beacon", () ->new EmptyBeacon(new Item.Properties()));
    public static final DeferredItem<Item> POWER_BEACON=
            ITEMS.register("power_beacon", () ->new PowerBeacon(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> COKE=
            ITEMS.register("coke", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> UNSTABLE_SMITHING_TEMPLATE=
            ITEMS.register("unstable_smithing_template", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> INSPIREAD=
            ITEMS.register("inspiread", () ->new Item(new Item.Properties().food(ModFoods.INSPIREAD)));
    public static final DeferredItem<Item> REDSTONE_INGOT=
            ITEMS.register("redstone_ingot", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> LAPIS_INGOT=
            ITEMS.register("lapis_ingot", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> UNSTABLE_CORE=
            ITEMS.register("unstable_core", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> EXTREME_ALLOY=
            ITEMS.register("extreme_alloy", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> EPHEMERAL_MATERIAL=
            ITEMS.register("ephemeral_material", () ->new Item(new Item.Properties()));
    public static final DeferredItem<Item> SOD=
            ITEMS.register("sod", () ->new Item(new Item.Properties().food(ModFoods.SOD)));
    public static final DeferredItem<Item> INSPIRRY=
            ITEMS.register("inspirry", () ->new Item(new Item.Properties().food(ModFoods.INSPIRRY)));
    public static final DeferredItem<Item> EXPLODING_CANDY=
            ITEMS.register("exploding_candy", () ->new ExplodingCandy(new Item.Properties().food(ModFoods.EXPLODING_CANDY)));
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
            ITEMS.register("exp_pickaxe", () ->new ExperiencePickaxe(Tiers.DIAMOND, 0,-2.4F,
                    new Item.Properties().durability(899).rarity(Rarity.RARE)));
    public static final DeferredItem<Item> WORLD_BREAKER=
            ITEMS.register("world_breaker", () ->new BedrockPickaxe(Tiers.NETHERITE, 0,-2.0F,
                    new Item.Properties().durability(999999).rarity(Rarity.EPIC)));
    public static final DeferredItem<Item> EXP_HOE=
            ITEMS.register("exp_hoe", () ->new ExperienceHoe(Tiers.IRON, -2.0F,-0.0F,
                    new Item.Properties().durability(899).rarity(Rarity.RARE)));
    public static final DeferredItem<Item> SCYTHE_OF_DEATH=
            ITEMS.register("scythe_of_death", () ->new Scythe(Tiers.DIAMOND, 3.0F,-2.8F,
                    new Item.Properties().durability(1799).rarity(Rarity.EPIC)));

    public static final DeferredHolder<Item, BucketItem> MIND_FLUID_BUCKET = ITEMS.register(
            "mind_fluid_bucket",
            ()->new BucketItem(ModFluids.Fluids.MIND_FLUID.get(),
                    new Item.Properties())
    );
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
