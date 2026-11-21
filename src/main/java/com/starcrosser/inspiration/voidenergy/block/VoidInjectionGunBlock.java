package com.starcrosser.inspiration.voidenergy.block;

import com.starcrosser.inspiration.injection.block.InjectionGunBlock;
import com.starcrosser.inspiration.injection.blockentity.InjectionGunTile;
import com.starcrosser.inspiration.voidenergy.blockentity.VoidInjectionGunTile;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * 虚空注入枪：外形与普通注入枪一致（同一套贴图与模型），只换方块实体。
 */
public class VoidInjectionGunBlock extends InjectionGunBlock {

    @Override
    public BlockEntityType.BlockEntitySupplier<InjectionGunTile> getTileEntityFactory() {
        return VoidInjectionGunTile::new;
    }
}
