package com.starcrosser.inspiration.voidenergy;

import com.starcrosser.inspiration.Inspiration;
import com.starcrosser.inspiration.voidenergy.block.CrackedBedrockBlock;
import com.starcrosser.inspiration.voidenergy.block.VoidEnergyExtractorBlock;
import com.starcrosser.inspiration.voidenergy.block.VoidEnergyRelayBlock;
import com.starcrosser.inspiration.voidenergy.blockentity.VoidEnergyExtractorTile;
import com.starcrosser.inspiration.voidenergy.entity.VoidClusterEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 虚空能量体系的方块 / 方块实体 / 实体注册。
 * <p>
 * 这三台东西不走 Titanium 的机器注册：提取器与中继器都没有界面和配方，
 * 团簇本身更是实体。普通方块与 BlockEntity 直接在这里登记，物品只登记方块物品。
 */
public final class ModVoidEnergy {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Inspiration.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Inspiration.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Inspiration.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Inspiration.MODID);

    public static final DeferredBlock<CrackedBedrockBlock> CRACKED_BEDROCK =
            BLOCKS.register("cracked_bedrock", CrackedBedrockBlock::new);
    public static final DeferredBlock<VoidEnergyExtractorBlock> VOID_ENERGY_EXTRACTOR =
            BLOCKS.register("void_energy_extractor", VoidEnergyExtractorBlock::new);
    public static final DeferredBlock<VoidEnergyRelayBlock> VOID_ENERGY_RELAY =
            BLOCKS.register("void_energy_relay", VoidEnergyRelayBlock::new);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VoidEnergyExtractorTile>>
            VOID_ENERGY_EXTRACTOR_TYPE = BLOCK_ENTITIES.register("void_energy_extractor",
            () -> BlockEntityType.Builder.of(VoidEnergyExtractorTile::new, VOID_ENERGY_EXTRACTOR.get()).build(null));

    public static final DeferredHolder<EntityType<?>, EntityType<VoidClusterEntity>> VOID_CLUSTER =
            ENTITIES.register("void_cluster", () -> EntityType.Builder
                    .<VoidClusterEntity>of(VoidClusterEntity::new, MobCategory.MISC)
                    .sized(0.8F, 0.8F)
                    .clientTrackingRange(8)
                    .updateInterval(10)
                    .fireImmune()
                    .build("void_cluster"));

    private ModVoidEnergy() {
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        ENTITIES.register(bus);
        ITEMS.registerSimpleBlockItem(CRACKED_BEDROCK);
        ITEMS.registerSimpleBlockItem(VOID_ENERGY_EXTRACTOR);
        ITEMS.registerSimpleBlockItem(VOID_ENERGY_RELAY);
    }
}
