package com.starcrosser.inspiration.block;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.fluid.ModFluids;
import com.starcrosser.inspiration.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.starcrosser.inspiration.Inspiration.MODID;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Blocks FLUID_BLOCKS = DeferredRegister.createBlocks(MODID);
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
    public static final DeferredBlock<Block> EPHEMERAL_ORE =
            registerBlocks("ephemeral_ore",()->new Block(BlockBehaviour.Properties.of().strength(1.0F,10.0F)
                    .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> CESIUM_SALT_ORE =
            registerBlocks("cesium_salt_ore",()->new Block(BlockBehaviour.Properties.of().strength(1.0F,6.0F)
                    .requiresCorrectToolForDrops()));
    public static final DeferredBlock<ReinforcedTNTBlock> INTENSIFIED_TNT =
            registerBlocks("intensified_tnt",()->new ReinforcedTNTBlock(BlockBehaviour.Properties.of().strength(0.0F,0.0F)
                    ));
    public static final DeferredHolder<Block, LiquidBlock> MIND_FLUID_BLOCK = registerBlocks(
            "mind_fluid_block",
            () -> new LiquidBlock(
                    ModFluids.Fluids.MIND_FLUID.get(),
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_CYAN)
                            .replaceable()
                            .strength(100.0f)
                            .lightLevel(state -> 2)
                            .pushReaction(PushReaction.DESTROY)
                            .noLootTable()
                            .liquid()
                            .sound(SoundType.EMPTY)
            )
    );
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
