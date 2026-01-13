package com.starcrosser.inspiration.block;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Inspiration.MODID);
    private static <T extends Block> void registerBlockItems(String name , DeferredBlock<T> block){
        ModItems.ITEMS.register(name,()->new BlockItem(block.get(), new Item.Properties()));
    }
    private static <T extends Block> DeferredBlock<T> registerBlocks(String name , Supplier<T> block){
        DeferredBlock<T> blocks=BLOCKS.register(name,block);
        registerBlockItems(name,blocks);
        return blocks;
    }
    public static final DeferredBlock<Block> LIGNITE_BLOCK =
            registerBlocks("lignite_block",()->new Block(BlockBehaviour.Properties.of().strength(1.5F,6.0F)
                    .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> LAPIS_INGOT_BLOCK =
            registerBlocks("lapis_ingot_block",()->new Block(BlockBehaviour.Properties.of().strength(2.5F,6.0F)
                    .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> REDSTONE_INGOT_BLOCK =
            registerBlocks("redstone_ingot_block",()->new Block(BlockBehaviour.Properties.of().strength(2.5F,6.0F)
                    .requiresCorrectToolForDrops()));
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
