package com.starcrosser.inspiration.multiblock.data;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Each entry simulates one structure. Repeated source IDs weight the choice of loot table. */
public enum StructureSite {
    SHIPWRECK(12_000, 1000,
            "chests/shipwreck_supply", "chests/shipwreck_treasure",
            "chests/shipwreck_map"),
    MINESHAFT(4_800, 500,
            "chests/abandoned_mineshaft"),
    MONSTER_ROOM(4_800, 500,
            "chests/simple_dungeon"),
    DESERT_PYRAMID(7_200, 800,
            "chests/desert_pyramid", "archaeology/desert_pyramid"),
    DESERT_WELL(2_400, 300,
            "archaeology/desert_well"),
    JUNGLE_TEMPLE(7_200, 800,
            "chests/jungle_temple", "chests/jungle_temple_dispenser"),
    IGLOO(3_600, 400,
            "chests/igloo_chest"),
    OCEAN_RUIN(4_800, 600,
            "chests/underwater_ruin_big", "chests/underwater_ruin_small",
            "archaeology/ocean_ruin_warm", "archaeology/ocean_ruin_cold"),
    TRAIL_RUINS(6_000, 650,
            "archaeology/trail_ruins_common", "archaeology/trail_ruins_common",
            "archaeology/trail_ruins_common", "archaeology/trail_ruins_rare"),
    BURIED_TREASURE(9_600, 1000,
            "chests/buried_treasure"),
    VILLAGE(3_600, 400,
            "chests/village/village_armorer", "chests/village/village_butcher",
            "chests/village/village_cartographer", "chests/village/village_desert_house",
            "chests/village/village_fisher", "chests/village/village_fletcher",
            "chests/village/village_mason", "chests/village/village_plains_house",
            "chests/village/village_savanna_house", "chests/village/village_shepherd",
            "chests/village/village_snowy_house", "chests/village/village_taiga_house",
            "chests/village/village_tannery", "chests/village/village_temple",
            "chests/village/village_toolsmith", "chests/village/village_weaponsmith"),
    PILLAGER_OUTPOST(6_000, 650,
            "chests/pillager_outpost"),
    STRONGHOLD(9_600, 1200,
            "chests/stronghold_corridor", "chests/stronghold_crossing",
            "chests/stronghold_library"),
    WOODLAND_MANSION(12_000, 1400,
            "chests/woodland_mansion"),
    ANCIENT_CITY(14_400, 2000,
            "chests/ancient_city", "chests/ancient_city",
            "chests/ancient_city", "chests/ancient_city_ice_box"),
    RUINED_PORTAL(4_800, 600,
            "chests/ruined_portal"),
    NETHER_FORTRESS(8_400, 1000,
            "chests/nether_bridge"),
    BASTION_REMNANT(12_000, 1600,
            "chests/bastion_bridge", "chests/bastion_hoglin_stable",
            "chests/bastion_other", "chests/bastion_treasure"),
    END_CITY(14_400, 2200,
            "chests/end_city_treasure"),
    TRIAL_CHAMBERS(6_000, 800,
            "chests/trial_chambers/entrance", "chests/trial_chambers/corridor",
            "chests/trial_chambers/intersection", "chests/trial_chambers/intersection_barrel",
            "chests/trial_chambers/supply", "pots/trial_chambers/corridor",
            "dispensers/trial_chambers/chamber", "dispensers/trial_chambers/corridor",
            "dispensers/trial_chambers/water"),
    TRIAL_VAULT(9_600, 1400,
            "chests/trial_chambers/reward"),
    OMINOUS_VAULT(16_000, 2400,
            "chests/trial_chambers/reward_ominous");

    private final SimulationSpec spec;

    StructureSite(int duration, int energyPerTick, String... tables) {
        spec = new SimulationSpec(duration, energyPerTick,
                Arrays.stream(tables).map(ResourceLocation::withDefaultNamespace).toList());
    }

    public String cardId() {
        return name().toLowerCase(Locale.ROOT) + "_data";
    }

    public static Map<StructureSite, DeferredItem<Item>> registerCards(DeferredRegister.Items items) {
        var cards = new EnumMap<StructureSite, DeferredItem<Item>>(StructureSite.class);
        for (var site : values()) {
            var rarity = site.spec.energyPerTick() >= 1200 ? Rarity.RARE : Rarity.UNCOMMON;
            cards.put(site, items.register(site.cardId(),
                    () -> new StructureDataItem(new Item.Properties().rarity(rarity), site.spec)));
        }
        return Map.copyOf(cards);
    }
}
