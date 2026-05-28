package com.starcrosser.inspiration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", true);

    public static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
    //copying from some fun mod
    private static final ModConfigSpec.BooleanValue CF_VISIBILITY = BUILDER.comment("Should the configurable fluid be invisible?").define("cFVisibility", false);
    private static final ModConfigSpec.BooleanValue CF_BOATING = BUILDER.comment("Should the configurable fluid support boats?").define("cFBoating", true);
    private static final ModConfigSpec.BooleanValue CF_HYDRATE = BUILDER.comment("Should the configurable fluid hydrate things?").define("cFHydration", true);
    private static final ModConfigSpec.BooleanValue CF_INFINITE = BUILDER.comment("Should the configurable fluid be infinite?").define("cFInfinite", true);
    private static final ModConfigSpec.BooleanValue CF_EXTINGUISHES = BUILDER.comment("Should the configurable fluid put out fires?").define("cExtinguish", true);
    private static final ModConfigSpec.BooleanValue CF_EVAPORATE_IN_NETHER = BUILDER.comment("Should the configurable fluid evaporate in the nether?").define("cFEvaporateInNether", false);
    private static final ModConfigSpec.BooleanValue CF_RIDE_UNDER = BUILDER.comment("Should players be able to ride under the configurable fluid?").define("cFRideUnder", true);
    private static final ModConfigSpec.BooleanValue CF_DROWN = BUILDER.comment("Can the configurable fluid drown players?").define("cFDrown", true);
    private static final ModConfigSpec.BooleanValue CF_SWIM = BUILDER.comment("Can you swim in the configurable fluid?").define("cFSwim", true);
    private static final ModConfigSpec.DoubleValue CF_PUSH_STRENGTH = BUILDER.comment("How hard should the configurable fluid push? (values between 0 and 0.1 are normal)").defineInRange("cFPushStrength", 0.05, -100.0, 100.0);
    private static final ModConfigSpec.IntValue CF_COLOR = BUILDER.comment("Configurable fluid color (ARGB)?").defineInRange("cFColor", -12618012, Integer.MIN_VALUE, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue CF_DAMAGE_MULTIPLIER = BUILDER.comment("How much should the configurable fluid scale fall damage?").defineInRange("cFDamageMultiplier", 0, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue CF_LIGHT = BUILDER.comment("How much light should the configurable fluid emit?").defineInRange("cFLight", 0, 0, 15);
    private static final ModConfigSpec.IntValue CF_RESPONSIVENESS = BUILDER.comment("How responsive should the configurable fluid be (ticks, higher is slower)?").defineInRange("cFResponsiveness", 5, 0, 15);
    private static final ModConfigSpec.IntValue CF_SLOPE_FIND_DISTANCE = BUILDER.comment("How far should the configurable fluid look for height changes before deciding where to flow?").defineInRange("cFHeightChangeDistance", 3, 1, 8);
    private static final ModConfigSpec.EnumValue<Rarity> CF_RARITY = BUILDER.comment("How rare should the configurable fluid be?").defineEnum("cFRarity", Rarity.EPIC);
    private static final ModConfigSpec.BooleanValue FLOOD_DECAYS = BUILDER.comment("Should flood blocks decay into regular water?").define("floodDecays", true);
    private static final ModConfigSpec.BooleanValue DESTROY_FLOOD = BUILDER.comment("HELP! I ignored the warnings! (Destroys, but does not reverse the effect of, Flood.)").define("destroyFlood", false);

}
