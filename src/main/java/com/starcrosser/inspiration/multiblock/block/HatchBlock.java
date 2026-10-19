package com.starcrosser.inspiration.multiblock.block;

import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.BasicTile;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * 五种仓室共用的方块类。
 * <p>
 * 仓室的区别全在方块实体里（物品槽 / 能量缓冲 / 充能罐），方块本身只需要把
 * tile 类与工厂交给 Titanium —— 所以这里用一个泛型类配五个实例，而不是写五个类。
 */
public class HatchBlock<T extends BasicTile<T>> extends BasicTileBlock<T> {

    private final BlockEntityType.BlockEntitySupplier<T> factory;

    public HatchBlock(BlockBehaviour.Properties properties, Class<T> tileClass,
                      BlockEntityType.BlockEntitySupplier<T> factory) {
        super(properties, tileClass);
        this.factory = factory;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return factory;
    }
}
