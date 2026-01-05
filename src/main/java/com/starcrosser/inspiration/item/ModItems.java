package com.starcrosser.inspiration.item;

import com.starcrosser.inspiration.Inspiration;
import net.minecraft.world.item.Item;
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
    public static final DeferredItem<Item> INSPIRRY=
            ITEMS.register("inspirry", () ->new Item(new Item.Properties().food(ModFoods.INSPIRRY)));
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
