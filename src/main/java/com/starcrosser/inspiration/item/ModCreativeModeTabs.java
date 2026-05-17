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
                        output.accept(ModItems.COKE);
                        output.accept(ModItems.EXP_SWORD);
                        output.accept(ModItems.STORM_SWORD);
                        output.accept(ModItems.STICKY_AXE);
                        output.accept(ModItems.HEAD_HUNTER_AXE);
                        output.accept(ModItems.EXP_PICKAXE);
                        output.accept(ModItems.REDSTONE_INGOT);
                        output.accept(ModItems.LAPIS_INGOT);
                        output.accept(ModBlocks.LAPIS_INGOT_BLOCK);
                        output.accept(ModBlocks.REDSTONE_INGOT_BLOCK);
                        output.accept(ModItems.UNSTABLE_CORE);
                        output.accept(ModItems.UNSTABLE_SMITHING_TEMPLATE);
                        output.accept(ModItems.EXTREME_ALLOY);
                        output.accept(ModBlocks.EPHEMERAL_ORE);
                        output.accept(ModItems.EPHEMERAL_MATERIAL);
                        output.accept(ModItems.SOD);
                        output.accept(ModItems.EXP_HOE);
                        output.accept(ModItems.SCYTHE_OF_DEATH);
                        output.accept(ModItems.EMPTY_BEACON);
                        output.accept(ModItems.CESIUM_SALT);
                        output.accept(ModItems.RAW_CESIUM_SALT);
                        output.accept(ModBlocks.CESIUM_SALT_ORE);
                        output.accept(ModBlocks.INTENSIFIED_TNT);
                    })
                    .build());
    public static void register(IEventBus eventBus) {
        CREATIVE_MOD_TABS.register(eventBus);
    }
}
