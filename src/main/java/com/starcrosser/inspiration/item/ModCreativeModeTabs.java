package com.starcrosser.inspiration.item;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MOD_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Inspiration.MODID);
    public static final Supplier<CreativeModeTab> INSPIRATION_TAB =
            CREATIVE_MOD_TABS.register("inspire_tab", ()-> CreativeModeTab.builder()
                    .icon(()->new ItemStack(ModItems.INSPIRATION.get()))
                    .title(Component.translatable("itemGroup.inspire_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.INSPIRATION);
                        output.accept(ModItems.LIGNITE);
                        output.accept(ModItems.COOKED_INSPIRATION);
                        output.accept(ModBlocks.LIGNITE_BLOCK);
                        output.accept(ModItems.INSPIREAD);
                        output.accept(ModItems.INSPIRRY);
                    })
                    .build());
    public static void register(IEventBus eventBus) {
        CREATIVE_MOD_TABS.register(eventBus);
    }
}
