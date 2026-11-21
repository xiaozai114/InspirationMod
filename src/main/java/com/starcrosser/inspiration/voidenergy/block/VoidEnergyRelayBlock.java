package com.starcrosser.inspiration.voidenergy.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * 虚空能量中继器：本身不存能量，作用是「把所在格的团簇共享给周围 5×5×5 的机器」。
 * <p>
 * 必须放在团簇所在的那一格（团簇是实体，与方块可以重叠），
 * 查找逻辑见 {@code VoidEnergyNetwork#findCluster}。
 */
public class VoidEnergyRelayBlock extends Block {

    /** 中继半径：以中继器为中心，各方向 2 格，合起来 5×5×5。 */
    public static final int RANGE = 2;

    public VoidEnergyRelayBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(4.0F, 10.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL)
                .lightLevel(state -> 4));
    }
}
